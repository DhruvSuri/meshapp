package in.meshworks.beans;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.UUID;

/**
 * Created by dhruv.suri on 22/06/17.
 */
public class Node {

    private String uniqueID;
    private String version;
    private String appName;
    private UUID sessionID;
    private SocketIOClient client;

    public Node(String uniqueID, SocketIOClient client) {
        this.uniqueID = uniqueID;
        this.client = client;
    }

    public Node(SocketIOClient client) {
        this.client = client;
        this.sessionID = client.getSessionId();
        this.uniqueID = client.getHandshakeData().getSingleUrlParam("uniqueID");
        this.version = client.getHandshakeData().getSingleUrlParam("version");
        this.appName = client.getHandshakeData().getSingleUrlParam("appName");
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public UUID getSessionID() {
        return sessionID;
    }

    public void setSessionID(UUID sessionID) {
        this.sessionID = sessionID;
    }

    public SocketIOClient getClient() {
        return client;
    }

    public void setClient(SocketIOClient client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Node{" +
                "uniqueID='" + uniqueID + '\'' +
                ", version='" + version + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }
}
