package in.meshworks.beans;

/**
 * Created by harshvardhansharma on 03/08/17.
 */
public class DataStat {

    private long receivedBytes;
    private long sentBytes;

    public DataStat() {
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
        return "DataStat{" +
                "receivedBytes=" + receivedBytes +
                ", sentBytes=" + sentBytes +
                '}';
    }
}
