package in.meshworks.beans;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.UUID;

/**
 * Created by dhruv.suri on 22/06/17.
 */
public class Node {

    private String uniqueKey;
    private String version;
    private Profile profile;
    private UUID sessionID;
    private SocketIOClient client;


    public Node(Profile profile, SocketIOClient client) {
        this.profile = profile;
        this.client = client;
    }

    public Node(SocketIOClient client) {
        this.client = client;
        this.sessionID = client.getSessionId();
        this.uniqueKey = client.getHandshakeData().getSingleUrlParam("uniqueKey");
        this.version = client.getHandshakeData().getSingleUrlParam("version");
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
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

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Node{" +
                "uniqueKey='" + uniqueKey + '\'' +
                ", version='" + version + '\'' +
                ", profile=" + profile +
                ", sessionID=" + sessionID +
                ", client=" + client +
                '}';
    }
}
