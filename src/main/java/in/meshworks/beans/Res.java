package in.meshworks.beans;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by dhruv.suri on 31/05/17.
 */
@Document
public class Res {

    private String uniqueID;

    private int code;
    private Map<String, List<String>> headers;
    private byte[] body;

    private long requestReceivedAt;
    private long responseSentAt;

    private String requestURL;
    private long dataUsed;

    public Res() {
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public long getRequestReceivedAt() {
        return requestReceivedAt;
    }

    public void setRequestReceivedAt(long requestReceivedAt) {
        this.requestReceivedAt = requestReceivedAt;
    }

    public long getResponseSentAt() {
        return responseSentAt;
    }

    public void setResponseSentAt(long responseSentAt) {
        this.responseSentAt = responseSentAt;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public long getDataUsed() {
        return dataUsed;
    }

    public void setDataUsed(long dataUsed) {
        this.dataUsed = dataUsed;
    }

    @Override
    public String toString() {
        return "Res{" +
                "uniqueID='" + uniqueID + '\'' +
                ", code=" + code +
                ", headers=" + headers +
                ", body=" + Arrays.toString(body) +
                ", requestReceivedAt=" + requestReceivedAt +
                ", responseSentAt=" + responseSentAt +
                '}';
    }
}
