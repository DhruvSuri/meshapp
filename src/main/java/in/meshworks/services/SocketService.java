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
    private final int DefaultPort = 9001;
    private final String DefaultEvent = "proxy";
    private final String WebView = "webview";
    private final String ProfileEvent = "profile";
    private final String stats = "stats";
    private final String DataConsumptionEvent = "datastats";
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
                        list.add(node);
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
        for(Node node : list) {
            SocketIOClient client = node.getClient();
            if (isValidNode(node, "1.11")) {
                updateDataConsumptionStats(node);
            }
        }
    }

    private boolean isValidNode(Node node, String minAppVersion) {
        return node != null &&
                node.getClient() != null && node.getClient().isChannelOpen() &&
                node.getMobileNumber() != null && !node.getMobileNumber().trim().equals("") &&
                node.getVersion() != null && node.getVersion().compareTo(minAppVersion) >= 0;
    }

    private void updateDataConsumptionStats(Node node) {
        int timeoutInSeconds = 5;
        node.getClient().sendEvent(DataConsumptionEvent, new AckCallback<String>(String.class, timeoutInSeconds) {
            @Override
            public void onSuccess(String result) {
                DataStat dataStat = AzazteUtils.fromJson(result, DataStat.class);
                Profile profile = profileService.findByMobileNumber(node.getMobileNumber());
                updateDataConsumptionStats(dataStat, profile);
                updateNibs(profile);
            }

            @Override
            public void onTimeout() {
                log.debug("[DATA CONSUMPTION STATS] Timed out for DataConsumptionEvent for Node: " + node);
            }
        });

        Profile profile = profileService.findByMobileNumber(node.getMobileNumber());
        node.getClient().sendEvent(ProfileEvent, AzazteUtils.toJson(profile));
    }

    private void updateDataConsumptionStats(DataStat dataStat, Profile profile) {
        if (dataStat != null && profile != null) {
            long totalBytes = dataStat.getReceivedBytes() + dataStat.getSentBytes();
            if (profile.getPreviousDataConsumption() == null) {
                profile.setPreviousDataConsumption(0l);
            }
            if (profile.getCurrentDataConsumption() != null && profile.getCurrentDataConsumption() > totalBytes) {
                profile.setPreviousDataConsumption(profile.getPreviousDataConsumption() + profile.getCurrentDataConsumption());
            }
            profile.setCurrentDataConsumption(totalBytes);
            profileService.updateProfile(profile);
        }
    }

    /**
     * 500 Mb ~ 30 NIBS
     * i.e.,
     * 500 * 1024 * 1024 bytes ~ 30 NIBS
     * x bytes = (30 * x) / 500 * 1024 * 1024 NIBS
     * @param profile
     */
    private void updateNibs(Profile profile) {
        long totalDataConsumption = profile.getPreviousDataConsumption() + profile.getCurrentDataConsumption();
        profile.setNibsActual((30.0f * totalDataConsumption) / (500 * 1024 * 1024));
        profile.setNibsCount((int)profile.getNibsActual());
        profileService.updateProfile(profile);
    }

    public ProxyResponse webviewRequest(final String url) {
        Node node = getNextNode();
        if (node == null) {
            return null;
        }

        SocketIOClient client = node.getClient();


        ProxyResponse response = new ProxyResponse();
        response.setRequestUrl(url);
        response.setMobileNumber(node.getMobileNumber());
        response.setRequestSentAt(new Date().getTime());


        client.sendEvent(WebView, new AckCallback<String>(String.class, 10) {
            @Override
            public void onSuccess(String result) {
                if (result.equals("true")){
                    response.setStatus("success");
                }else{
                    response.setStatus("failed");
                }
                saveToDb(response);
            }

            @Override
            public void onTimeout() {
                response.setStatus("timeout");
                saveToDb(response);
            }
        }, url);

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
                            node.setProfile(new Profile(proxyResponse.getName(), proxyResponse.getMobileNumber()));
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

    public void sendNibsRequest() {
        Node node = getNextNode();
        if (node == null) {
            return;
        }
        SocketIOClient client = node.getClient();
        log.info("Sending Nibs to  " + node.getMobileNumber());
        Profile profile = profileService.findByMobileNumber(node.getMobileNumber());
        int nibs = profile.getNibsCount();
        int referralCount = profile.getReferralCount();
        int maxThreshold = 500;
        client.sendEvent(stats, "" + nibs + "," + (10 * referralCount) + "," + (nibs + 10 * referralCount) + "," + maxThreshold);
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

