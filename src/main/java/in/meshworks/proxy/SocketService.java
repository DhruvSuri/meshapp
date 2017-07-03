package in.meshworks.proxy;


import com.mongodb.DBCollection;
import com.mongodb.async.client.MongoCollection;
import in.meshworks.Mongo.MongoFactory;
import in.meshworks.proxy.Beans.Node;
import in.meshworks.proxy.Beans.Profile;
import in.meshworks.proxy.Beans.ProxyResponse;
import in.meshworks.utils.AzazteUtils;
import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import okhttp3.Request;
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
    private final String DefaultEvent = "proxy";
    private final String ProfileEvent = "profile";
    private final String DeviceDetails = "device_details";
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
                        socketIOClient.sendEvent(ProfileEvent, new AckCallback<String>(String.class) {
                            @Override
                            public void onSuccess(String profileString) {
                                node.setProfile(AzazteUtils.fromJson(profileString, Profile.class));
                                log.debug("Profile String : " + profileString);
                            }
                        });

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


    public String sendProxyRequest(final Request request, int timeout) {
        final Thread currentThread = Thread.currentThread();
        log.debug("Held thread : " + currentThread.getId());

        while (true) {
            Collection<SocketIOClient> clients = server.getAllClients();
//            List<SocketIOClient> list = new ArrayList<>();
            int size = clients.size();

            if (size == 0 || list.size() == 0) {
                log.debug("No active connections available.Releasing thread : " + currentThread.getId());
                return "No active connections available";
            }

            Node node = list.remove(0);
            list.add(node);
            SocketIOClient randomClient = node.getClient();

//            Iterator<SocketIOClient> itr = clients.iterator();
//            while(itr.hasNext()){
//                list.add(itr.next());
//            }

            final ArrayList<ProxyResponse> response = new ArrayList<ProxyResponse>();


//            int random = new Random().nextInt(list.size());
//            SocketIOClient randomClient = list.get(random);

//            int random = new Random().nextInt(clients.size());
//            log.debug("Random = " + random + "clients size = " + clients.size());
//            int i = 0;
//            SocketIOClient randomClient = null;
//            Iterator<SocketIOClient> iterator = clients.iterator();
//            while (i <= random) {
//                randomClient = iterator.next();
//                i++;
//            }

            final SocketIOClient client = randomClient;
            final long requestSentAt = new Date().getTime();

            client.sendEvent(DefaultEvent, new AckCallback<String>(String.class, timeout) {
                @Override
                public void onSuccess(String result) {
                    synchronized (currentThread) {
                        try {
                            final ProxyResponse proxyResponse = AzazteUtils.fromJson(result, ProxyResponse.class);
                            proxyResponse.setResponseReceivedAt(new Date().getTime());
                            proxyResponse.setResponseSentAt(requestSentAt);
                            proxyResponse.setRequest(request);
                            proxyResponse.setDataUsed(proxyResponse.getResponseBody().length());
                            response.add(proxyResponse);
                            log.debug("Response from client: " + client.getSessionId() + " data: " + proxyResponse.getResponseBody().substring(0, 20) + "  From thread : " + currentThread.getId());
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
                    log.debug("Notified - " + "  From thread : " + currentThread.getId());
                    if (response.size() == 0) {
                        log.debug("Response size 0... Continuing ");
                        continue;
                    }

                    if (response.size() > 1) {
                        log.debug("Horrible ... !! How can number of responses go above 1... Gandu coding skills");
                        System.out.println("Horrible ... !! How can number of responses go above 1... Gandu coding skills");
                    }

                    ProxyResponse presponse = response.get(0);
                    saveToDb(presponse);
                    profileService.createOrUpdateProfile(presponse.getProfile().getName(),presponse.getProfile().getPhoneNumber(),presponse.getDataUsed());

                    log.debug("Request completed.Releasing thread : " + currentThread.getId());
                    return presponse.toString();
                } catch (Exception e) {
                    log.debug(e.getMessage());
                }
            }
        }


    }

    private void saveToDb(Object objectToSave){
        try {
            mongoFactory.getMongoTemplate().save(objectToSave);
        }catch (Exception failed){
            failed.printStackTrace();
            log.error("Failed to save to Mongo " + objectToSave.toString());
        }
    }

    public String getConnections(){
        return list.toString();
    }
}


