package in.meshworks.proxy.Beans;

/**
 * Created by dhruv.suri on 22/06/17.
 */
public class DeviceDetails {

    private long receivedBytes;
    private long sentBytes;

    public DeviceDetails(){
    }

    public long getReceivedBytes() {
        return receivedBytes;
    }

    public void setReceivedBytes(long receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public long getSentBytes() {
        return sentBytes;
    }

    public void setSentBytes(long sentBytes) {
        this.sentBytes = sentBytes;
    }

    @Override
    public String toString() {
        return "DeviceDetails{" +
                "receivedBytes=" + receivedBytes +
                ", sentBytes=" + sentBytes +
                '}';
    }
}
