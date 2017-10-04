package in.meshworks.services;

import in.meshworks.beans.*;
import in.meshworks.mongo.MongoFactory;
import in.meshworks.utils.AzazteUtils;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by dhruv.suri on 11/05/17.
 */
@Service
public class SocketService {
    private static final Logger log = LoggerFactory.getLogger(SocketService.class);
    private final int DefaultPort = 9004;
    private final String DefaultEvent = "proxy";
    private final String WebView = "webView";
    private final String ProfileEvent = "profile";
    private final String DataConsumptionEvent = "dataStats";
    private static SocketIOServer server;
    private String str = "";
    private List<Node> list = Collections.synchronizedList(new ArrayList<>());

    @Autowired
    MongoFactory mongoFactory;

    @Autowired
    ProfileService profileService;

    public SocketService() {
        Thread tt = new Thread() {

            public void run() {
                log.debug("Starting server");
                Configuration config = new Configuration();
                config.setPingInterval(5000);
                config.setPingTimeout(30000);
                config.setMaxFramePayloadLength(Integer.MAX_VALUE);
                config.setMaxHttpContentLength(Integer.MAX_VALUE);
                config.setPort(DefaultPort);

                server = new SocketIOServer(config);

                server.addConnectListener(new ConnectListener() {
                    @Override
                    public void onConnect(SocketIOClient socketIOClient) {
                        log.debug("Connected socket : " + socketIOClient.getSessionId());
                        final Node node = new Node(socketIOClient);
                        if (!isAlreadyAddedToList(node)) {
                            list.add(node);
                        }
                        log.debug("Server list size : " + server.getAllClients().size());
                        log.debug("MyList list size : " + list.size());
                    }
                });

                server.addDisconnectListener(new DisconnectListener() {
                    @Override
                    public void onDisconnect(SocketIOClient socketIOClient) {
                        removeFromList(socketIOClient.getSessionId());
                        log.debug("Disconnected socket : " + socketIOClient.getSessionId());
                        log.debug("Server list size : " + server.getAllClients().size());
                        log.debug("MyList list size : " + list.size());
                    }
                });
                server.start();

                try {
                    Thread.sleep(Integer.MAX_VALUE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    server.stop();
                }
                server.stop();
            }
        };
        tt.start();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        server.stop();
    }

    private void removeFromList(UUID sessionID) {
        for (Node node : list) {
            if (node.getSessionID() == sessionID) {
                list.remove(node);
                log.debug("REMOVING FROM LIST: " + node);
                break;
            }
        }
    }

    public void updateDataConsumptionStats() {
        for (Node node : list) {
            SocketIOClient client = node.getClient();
            if (isValidNode(node)) {
                updateDataConsumptionStats(node);
            }
        }
    }

    private boolean isValidNode(Node node) {
        return node != null &&
                node.getClient() != null && node.getClient().isChannelOpen() &&
                node.getUniqueKey() != null && !node.getUniqueKey().trim().equals("") &&
                node.getVersion() != null && node.getVersion().compareTo("4.0.0") >= 0;
    }

    private boolean isAlreadyAddedToList(Node node) {
        for (Node item : list) {
            if (item.getUniqueKey().equals(node.getUniqueKey())) {
                return true;
            }
        }
        return false;
    }

    private void updateDataConsumptionStats(Node node) {
        int timeoutInSeconds = 5;
        node.getClient().sendEvent(DataConsumptionEvent, new AckCallback<String>(String.class, timeoutInSeconds) {
            @Override
            public void onSuccess(String result) {
                DataStat dataStat = AzazteUtils.fromJson(result, DataStat.class);
                Profile profile = profileService.findByMobileNumber(node.getUniqueKey());
                updateDataConsumptionStats(dataStat, profile);
                updateNibs(profile);
            }

            @Override
            public void onTimeout() {
                log.debug("[DATA CONSUMPTION STATS] Timed out for DataConsumptionEvent for Node: " + node);
            }
        });

        Profile profile = profileService.findByMobileNumber(node.getUniqueKey());
        node.getClient().sendEvent(ProfileEvent, AzazteUtils.toJson(profile));
    }

    private void updateDataConsumptionStats(DataStat dataStat, Profile profile) {
        if (dataStat != null && profile != null) {
            long dataInBytes = dataStat.getDataInBytes();
            if (dataInBytes > 0) {
                if (profile.getCurrentDataConsumption() == null) {
                    profile.setCurrentDataConsumption(0l);
                }

                profile.setCurrentDataConsumption(profile.getCurrentDataConsumption() + dataInBytes);
                profileService.updateProfile(profile);
            }
        }
    }

    /**
     * 500 Mb ~ 30 NIBS
     * i.e.,
     * 500 * 1024 * 1024 bytes ~ 30 NIBS
     * x bytes = (30 * x) / 500 * 1024 * 1024 NIBS
     *
     * @param profile
     */
    private void updateNibs(Profile profile) {
        if (profile.getCurrentDataConsumption() != null) {
            profile.setNibsActual((30.0f * profile.getCurrentDataConsumption()) / (500 * 1024 * 1024));
            profile.setNibsCount((int) profile.getNibsActual());
            profileService.updateProfile(profile);
        }
    }

    public ProxyResponse webviewRequest(final Parcel parcel) {
        Node node = getNextNode();
        if (node == null) {
            return null;
        }

        SocketIOClient client = node.getClient();


        ProxyResponse response = new ProxyResponse();
        response.setRequestUrl(parcel.getUrl());
        response.setUniqueKey(node.getUniqueKey());
        response.setRequestSentAt(new Date().getTime());


        client.sendEvent(WebView, new AckCallback<String>(String.class, 10) {
            @Override
            public void onSuccess(String result) {
                if (result.equals("true")) {
                    response.setStatus("success");
                } else {
                    response.setStatus("failed");
                }
                saveToDb(response);
            }

            @Override
            public void onTimeout() {
                response.setStatus("timeout");
                saveToDb(response);
            }
        }, AzazteUtils.toJson(parcel));

        return response;
    }


    public ProxyResponse getProxyResponse(final ProxyRequest request, int timeout) {
        final Thread currentThread = Thread.currentThread();
        log.debug("Held thread : " + currentThread.getId());

        while (true) {
            final ArrayList<ProxyResponse> response = new ArrayList<ProxyResponse>();

            Node node = getNextNode();
            if (node == null) {
                return null;
            }
            final SocketIOClient client = node.getClient();
            final long requestSentAt = new Date().getTime();

            client.sendEvent(DefaultEvent, new AckCallback<String>(String.class, timeout) {
                @Override
                public void onSuccess(String result) {
                    synchronized (currentThread) {
                        try {
                            final ProxyResponse proxyResponse = AzazteUtils.fromJson(result, ProxyResponse.class);
                            response.add(proxyResponse);
                            node.setProfile(new Profile(proxyResponse.getName(), proxyResponse.getUniqueKey()));
                            proxyResponse.setResponseReceivedAt(new Date().getTime());
                            proxyResponse.setRequestSentAt(requestSentAt);
                            proxyResponse.setRequestUrl(request.getUrl());
                            proxyResponse.setDataUsed(proxyResponse.getResponseBody().length);

                            log.debug("Response from client: " + client.getSessionId() + " data: " + proxyResponse.getResponseBody()[0] + "  From thread : " + currentThread.getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            currentThread.notify();
                        }
                    }
                }

                @Override
                public void onTimeout() {
                    synchronized (currentThread) {
                        log.debug("Timed out for thread : " + currentThread.getId());
                        currentThread.notify();
                    }
                }
            }, AzazteUtils.toJson(request));

            synchronized (currentThread) {
                try {
                    log.debug("Waiting - " + "  From thread : " + currentThread.getId());
                    log.debug("Sending request to " + node.getProfile());
                    currentThread.wait(timeout * 1000);
                    log.debug("Notified - " + " From thread : " + currentThread.getId());
                    if (response.size() == 0) {
                        log.debug("Response size 0.. Continuing ");
                        continue;
                    }

                    if (response.size() > 1) {
                        log.debug("Horrible ... !! How can number of responses go above 1... Gandu coding skills");
                        System.out.println("Horrible ... !! How can number of responses go above 1... Gandu coding skills");
                    }

                    ProxyResponse presponse = response.get(0);
                    saveToDb(presponse);
                    log.debug("Request completed.Releasing thread : " + currentThread.getId());
                    return presponse;
                } catch (Exception e) {
                    log.debug(e.getMessage());
                }
            }
        }
    }

    public Node getNextNode() {
//        This function returns next available node.Logic may be complex in future
        if (list.size() == 0) {
            return null;
        }
        Node node = list.remove(0);
        list.add(node);
        return node;
    }

    private void saveToDb(Object objectToSave) {
        try {
//            com.mongodb.client.MongoDatabase database = new MongoClient(new ServerAddress("52.66.66.36"), Arrays.asList(MongoCredential.createScramSha1Credential("elb", "admin", "elbproxymesh".toCharArray()))).getDatabase("local");
//            MongoCollection<Document> profile = database.getCollection("Profile");
//
//            profile.insertOne(objectToSave, new SingleResultCallback<Void>() {
//                @Override
//                public void onResult(final Void result, final Throwable t) {
//
//                }
//            });
            mongoFactory.getMongoTemplate().save(objectToSave);
        } catch (Exception failed) {
            failed.printStackTrace();
            log.error("Failed to save to mongo " + objectToSave.toString());
        }
    }

    public String getConnections() {
        return "Size = " + list.size() + "\n\n" + list.toString();
    }
}


