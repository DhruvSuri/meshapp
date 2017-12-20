package in.meshworks.services;

import com.corundumstudio.socketio.*;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.listener.ExceptionListener;
import com.corundumstudio.socketio.store.HazelcastStoreFactory;
import com.corundumstudio.socketio.store.StoreFactory;
import in.meshworks.beans.Node;
import in.meshworks.beans.Req;
import in.meshworks.beans.Res;
import in.meshworks.utils.AzazteUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
    private Thread serverThread;

    @Autowired
    NodeService nodeService;

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

                config.setExceptionListener(new ExceptionListener() {
                    @Override
                    public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
                        log.info(">>> onEventException: client: " + client.getSessionId() + " :: " + client.isChannelOpen());
                        log.info(e.getMessage());
                        log.info(">>>");
                    }

                    @Override
                    public void onDisconnectException(Exception e, SocketIOClient client) {
                        log.info(">>> onDisconnectException: client: " + client.getSessionId() + " :: " + client.isChannelOpen());
                        log.info(e.getMessage());
                        log.info(">>>");
                    }

                    @Override
                    public void onConnectException(Exception e, SocketIOClient client) {
                        log.info(">>> onConnectException: client: " + client.getSessionId() + " :: " + client.isChannelOpen());
                        log.info(e.getMessage());
                        log.info(">>>");
                    }

                    @Override
                    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
                        return false;
                    }
                });

                config.setAckMode(AckMode.AUTO);

                server = new SocketIOServer(config);

                server.addConnectListener(new ConnectListener() {

                    @Override
                    public void onConnect(SocketIOClient client) {
                        log.debug("Connected socket : " + client.getSessionId());
                        final Node node = new Node(client);
                        nodeService.addNode(node);
                    }

                });

                server.addDisconnectListener(new DisconnectListener() {
                    @Override
                    public void onDisconnect(SocketIOClient socketIOClient) {
                        if (socketIOClient.getSessionId() == null) {
                            return;
                        }
                        nodeService.removeSocketIOClient(socketIOClient);
                    }
                });

                server.start();

                try {
                    Thread.sleep(Integer.MAX_VALUE);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
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

    public Res getProxyResponse(final Req request, int timeout, NodeService.ListType listType) {

        AtomicReference<Res> notifier = new AtomicReference<>();
        int n = 3;

        for (int i = 0; i < n; i++) {
            new Thread() {
                public void run() {
                    Node node = nodeService.getNextNode(listType);
                    Res res = getResponse(node, request, timeout, listType);
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
//                    ex.printStackTrace();
                }
            }
        }

        if (listType == NodeService.ListType.BASIC) {
            nodeService.promote(notifier.get().getUniqueID());
        }
        System.out.println(">> >> >> [" + notifier.get().getCode() + "] : " + notifier.get().getUniqueID());
        return notifier.get();
    }

    private Res getResponse(Node node, Req request, int timeout, NodeService.ListType listType) {
        final AtomicReference<Res> notifier = new AtomicReference();

        final SocketIOClient client = node.getClient();
        client.sendEvent(defaultEvent, new AckCallback<String>(String.class, timeout) {

            @Override
            public void onSuccess(String result) {
                final Res proxyResponse = AzazteUtils.fromJson(result, Res.class);
                proxyResponse.setUniqueID(node.getUniqueID());

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
//                    ex.printStackTrace();
                }
            }
        }

        return notifier.get();
    }

    public String getConnections(NodeService.ListType listType) {
        List<Node> nodes = nodeService.getNodes(listType);
        return "Size = " + nodes.size() + "\n\n" + nodes;
    }

}


