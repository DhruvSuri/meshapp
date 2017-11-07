package in.meshworks.services;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import in.meshworks.beans.Node;
import in.meshworks.beans.Req;
import in.meshworks.beans.Res;
import in.meshworks.utils.AzazteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by dhruv.suri on 11/05/17.
 */
@Service
public class SocketService {

    private static final Logger log = LoggerFactory.getLogger(SocketService.class);
    private final int defaultPort = 9000;
    private final String defaultEvent = "factor";
    private static SocketIOServer server;
    private List<Node> list = Collections.synchronizedList(new ArrayList<>());
    private Thread serverThread;

    @Autowired
    MixpanelService analyticsService;

    public SocketService() {

        serverThread = new Thread() {

            public void run() {
                log.debug("Starting server");
                Configuration config = new Configuration();
                SocketConfig socketConfig = new SocketConfig();

                config.setPingTimeout(30000);
                config.setPingInterval(10000);
                config.setUpgradeTimeout(10000000);
                config.setMaxFramePayloadLength(Integer.MAX_VALUE);
                config.setMaxHttpContentLength(Integer.MAX_VALUE);
                config.setPort(defaultPort);
                socketConfig.setReuseAddress(true);
                config.setSocketConfig(socketConfig);

                server = new SocketIOServer(config);

                server.addConnectListener(new ConnectListener() {

                    @Override
                    public void onConnect(SocketIOClient client) {
                        log.debug("Connected socket : " + client.getSessionId());
                        final Node node = new Node(client);

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
        serverThread.start();
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
                analyticsService.track(node.getUniqueID(), "Disconnected");
                break;
            }
        }
    }

    private boolean isAlreadyAddedToList(Node node) {
        for (Node item : list) {
            if (item.getUniqueID() == null || item.getUniqueID().equals(node.getUniqueID())) {
                return true;
            }
        }
        return false;
    }

    public Res getProxyResponse(final Req request, int timeout) {

        AtomicReference<Res> notifier = new AtomicReference<>();
        int n = 5;

        for (int i = 0; i < n; i++) {
            new Thread() {
                public void run() {
                    Res res = getResponse(request, timeout);
                    synchronized (notifier) {
                        notifier.set(res);
                        notifier.notify();
                    }
                }
            }.start();
        }

        synchronized (notifier) {
            while(notifier.get() == null) {
                try {
                    notifier.wait();
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return notifier.get();
    }

    private Res getResponse(Req request, int timeout) {

        final AtomicReference<Res> notifier = new AtomicReference();

        Node node = getNextNode();
        if (node == null ){
            return null;
        }

        final SocketIOClient client = node.getClient();
        client.sendEvent(defaultEvent, new AckCallback<String>(String.class, timeout) {

            @Override
            public void onSuccess(String result) {
                final Res proxyResponse = AzazteUtils.fromJson(result, Res.class);
                synchronized (notifier) {
                    notifier.set(proxyResponse);
                    notifier.notify();
                }
            }

            @Override
            public void onTimeout() {
                Res res = new Res();
                res.setCode(801);
                synchronized (notifier) {
                    notifier.set(res);
                    notifier.notify();
                }
            }

        }, AzazteUtils.toJson(request));

        synchronized (notifier) {
            while(notifier.get() == null) {
                try {
                    notifier.wait();
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return notifier.get();
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

    public String getConnections() {
        return "Size = " + list.size() + "\n\n" + list.toString();
    }

    public int getConnectionCount(){
        return list.size();
    }
}


