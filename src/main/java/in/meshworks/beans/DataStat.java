package in.meshworks.beans;

/**
 * Created by harshvardhansharma on 03/08/17.
 */
public class DataStat {

    private long dataInBytes;

    public DataStat() {
    }

    public long getDataInBytes() {
        return dataInBytes;
    }

    public void setDataInBytes(long dataInBytes) {
        this.dataInBytes = dataInBytes;
    }

    @Override
    public String toString() {
        return "DataStat{" +
                "dataInBytes=" + dataInBytes +
                '}';
    }
}
