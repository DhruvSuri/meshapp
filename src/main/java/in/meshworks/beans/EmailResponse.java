package in.meshworks.beans;

/**
 * Created by dhruv.suri on 26/12/17.
 */
public class EmailResponse {
    String status;
    String result;
    boolean dns;
    boolean acceptsAll;
    boolean dnsMx;
    boolean badDns;

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
