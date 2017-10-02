package in.meshworks.beans;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by dhruv.suri on 31/05/17.
 */
@Document
public class ProxyResponse {
    private String id;
    private String name;
    private String uniqueKey;
    private int responseCode;
    private Map<String, List<String>> responseHeaders;
    private byte[] responseBody;
    private String requestUrl;

    private long requestSentAt;
    private long requestReceivedAt;
    private long responseSentAt;
    private long responseReceivedAt;

    private long dataUsed;
    private String status;

    public ProxyResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public byte[] getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(byte[] responseBody) {
        this.responseBody = responseBody;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public long getRequestSentAt() {
        return requestSentAt;
    }

    public void setRequestSentAt(long requestSentAt) {
        this.requestSentAt = requestSentAt;
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

    public long getResponseReceivedAt() {
        return responseReceivedAt;
    }

    public void setResponseReceivedAt(long responseReceivedAt) {
        this.responseReceivedAt = responseReceivedAt;
    }

    public long getDataUsed() {
        return dataUsed;
    }

    public void setDataUsed(long dataUsed) {
        this.dataUsed = dataUsed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ProxyResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", uniqueKey='" + uniqueKey + '\'' +
                ", responseCode=" + responseCode +
                ", responseHeaders=" + responseHeaders +
                ", responseBody=" + Arrays.toString(responseBody) +
                ", requestUrl='" + requestUrl + '\'' +
                ", requestSentAt=" + requestSentAt +
                ", requestReceivedAt=" + requestReceivedAt +
                ", responseSentAt=" + responseSentAt +
                ", responseReceivedAt=" + responseReceivedAt +
                ", dataUsed=" + dataUsed +
                ", status='" + status + '\'' +
                '}';
    }
}
