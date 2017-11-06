package in.meshworks.services;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import in.meshworks.beans.Node;
import in.meshworks.beans.Req;
import in.meshworks.beans.Res;
import in.meshworks.mongo.MongoFactory;
import in.meshworks.utils.AzazteUtils;
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
    private final int DefaultPort = 9000;
    private final String DefaultEvent = "factor";
    private static SocketIOServer server;
    private String str = "";
    private List<Node> list = Collections.synchronizedList(new ArrayList<>());

//    @Autowired
//    MongoFactory mongoFactory;

    @Autowired
    MixpanelService analyticsService;

    public SocketService() {
        Thread tt = new Thread() {

            public void run() {
                log.debug("Starting server");
                Configuration config = new Configuration();
                SocketConfig socketConfig = new SocketConfig();

                config.setPingTimeout(30000);
                config.setPingInterval(10000);
                config.setUpgradeTimeout(10000000);
                config.setMaxFramePayloadLength(Integer.MAX_VALUE);
                config.setMaxHttpContentLength(Integer.MAX_VALUE);
                config.setPort(DefaultPort);
                socketConfig.setReuseAddress(true);
                config.setSocketConfig(socketConfig);

                server = new SocketIOServer(config);

                server.addConnectListener(new ConnectListener() {

                    @Override
                    public void onConnect(SocketIOClient client) {
                        log.debug("Connected socket : " + client.getSessionId());
                        final Node node = new Node(client);
//                        try {
//                            Thread.currentThread().sleep(500);
//                        } catch (InterruptedException ex) {
//                            ex.printStackTrace();
//                        }

                        if (!isAlreadyAddedToList(node)) {
                            analyticsService.track(node.getUniqueID(), "Connected");
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
                        Node node = new Node(socketIOClient);
                        analyticsService.track(node.getUniqueID(), "Disconnected");
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

    private boolean isValidNode(Node node) {
        return node != null &&
                node.getClient() != null && node.getClient().isChannelOpen() &&
                node.getUniqueID() != null && !node.getUniqueID().trim().equals("") &&
                node.getVersion() != null && node.getVersion().compareTo("4.0.0") >= 0;
    }

    private boolean isAlreadyAddedToList(Node node) {
        if (node.getUniqueID().equals("3c3cdecc34962460")) {
            return true;
        }

        for (Node item : list) {
            if (item.getUniqueID() == null || item.getUniqueID().equals(node.getUniqueID())) {
                return true;
            }
        }
        return false;
    }

    public Res getProxyResponse(final Req request, int timeout) {
        final Thread currentThread = Thread.currentThread();
        log.debug("Held thread : " + currentThread.getId());

        while (true) {
            final ArrayList<Res> response = new ArrayList<Res>();

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
                            final Res proxyResponse = AzazteUtils.fromJson(result, Res.class);
                            if (proxyResponse.getCode() != 0) {
                                response.add(proxyResponse);
                                proxyResponse.setRequestURL(request.getUrl());
                                proxyResponse.setDataUsed(proxyResponse.getBody().length);
                                log.debug("Response from client: " + client.getSessionId() + " data: " + proxyResponse.getBody()[0] + "  From thread : " + currentThread.getId());
                            }
                            else if (proxyResponse.getCode() == -1) {
                                proxyResponse.setCode(801);
                                response.add(proxyResponse);
                            }
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
                    log.debug("Sending request to " + node.getUniqueID());
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

                    Res presponse = response.get(0);
                    //saveToDb(presponse);
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
//            mongoFactory.getMongoTemplate().save(objectToSave);
        } catch (Exception failed) {
            failed.printStackTrace();
            log.error("Failed to save to mongo " + objectToSave.toString());
        }
    }

    public String getConnections() {
        return "Size = " + list.size() + "\n\n" + list.toString();
    }

    public int getConnectionCount(){
        return list.size();
    }
}


