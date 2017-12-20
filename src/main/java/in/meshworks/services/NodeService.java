package in.meshworks.services;

import com.corundumstudio.socketio.SocketIOClient;
import in.meshworks.beans.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by harshvardhansharma on 07/11/17.
 */
@Service
public class NodeService {

    public enum ListType {
        BASIC,
        ULTIMATE
    }

    private List<Node> basicList = Collections.synchronizedList(new ArrayList<>());
    private List<Node> ultimateList = Collections.synchronizedList(new ArrayList<>());

    @Autowired
    MixpanelService mixpanelService;

    public Node getNextNode(ListType listType) {
        switch (listType) {
            case BASIC:
                return getNextNode(basicList);
            case ULTIMATE:
                return getNextNode(ultimateList);
        }
        return null;
    }

    public void addNode(Node node) {
        if (!isAlreadyAddedToList(node, basicList)) {
            basicList.add(node);
//            mixpanelService.track(node.getUniqueID(), "Connected");
        }
    }

    public SocketIOClient removeNodeByRemoteAddr(SocketAddress remoteAddr) {
        for (Node node: basicList) {
            if (node.getClient().getRemoteAddress().toString().equals(remoteAddr.toString())) {
                basicList.remove(node);
                ultimateList.remove(node);
                return node.getClient();
            }
        }
        return null;
    }

    public void removeSocketIOClient(SocketIOClient socketIOClient) {
        for (Node node : basicList) {
            if (node.getSessionID().equals(socketIOClient.getSessionId())) {
                basicList.remove(node);
                ultimateList.remove(node);
//                System.gc();
                break;
            }
        }
    }

    public List<Node> getNodes(ListType listType) {
        switch (listType) {
            case BASIC:
                return basicList;
            case ULTIMATE:
                return ultimateList;
        }
        return new ArrayList<Node>();
    }

    public void promote(Node node) {
        if (!isAlreadyAddedToList(node, ultimateList)) {
            ultimateList.add(node);
        }
    }

    public void promote(String uniqueID) {
        Node node = getNodeByUniqueID(uniqueID);
        if (!isAlreadyAddedToList(node, ultimateList)) {
            ultimateList.add(node);
        }
    }

    private boolean isAlreadyAddedToList(Node node, List<Node> list) {
        if (node == null) {
            return true;
        }

        for (Node item : list) {
            if (item.getUniqueID() == null || item.getUniqueID().equals(node.getUniqueID())) {
                return true;
            }
        }
        return false;
    }

    private Node getNodeByUniqueID(String uniqueID) {
        for (Node node: basicList) {
            if (node.getUniqueID().equals(uniqueID)) {
                return node;
            }
        }
        return null;
    }

    private Node getNextNode(List<Node> list) {
        if (list.size() == 0) {
            return null;
        }
        Node node = list.remove(0);
        list.add(node);
        return node;
    }

}
