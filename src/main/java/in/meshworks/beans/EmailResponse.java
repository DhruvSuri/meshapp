package in.meshworks.beans;

/**
 * Created by dhruv.suri on 26/12/17.
 */
public class EmailResponse {
    public String status;
    public String result;
    public boolean dns;
    public boolean acceptsAll;
    public boolean dnsMx;
    public boolean badDns;

    @Override
    public String toString() {
        return "EmailResponse{" +
                "status='" + status + '\'' +
                ", result='" + result + '\'' +
                ", dns=" + dns +
                ", acceptsAll=" + acceptsAll +
                ", dnsMx=" + dnsMx +
                ", badDns=" + badDns +
                '}';
    }
}
