package in.meshworks.proxy.Beans;

import okhttp3.Request;

import java.util.Date;

/**
 * Created by dhruv.suri on 31/05/17.
 */
public class ProxyResponse {
    private Profile profile;
    private int responseStatus;
    private Request request;
    private long requestSentAt;
    private long requestReceivedAt;
    private long responseSentAt;
    private long responseReceivedAt;
    private long dataUsed;
    private String responseBody;


    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
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

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public long getRequestSentAt() {
        return requestSentAt;
    }

    public void setRequestSentAt(long requestSentAt) {
        this.requestSentAt = requestSentAt;
    }

    public long getDataUsed() {
        return dataUsed;
    }

    public void setDataUsed(long dataUsed) {
        this.dataUsed = dataUsed;
    }

    @Override
    public String toString() {
        return "ProxyResponse{" +
                ", profile=" + profile +
                ", responseStatus=" + responseStatus +
                ", request=" + request.url() +
                ", requestSentAt=" + new Date(requestSentAt) +
                ", requestReceivedAt=" + new Date(requestReceivedAt) +
                ", responseSentAt=" + new Date(responseSentAt) +
                ", responseReceivedAt=" + new Date(responseReceivedAt) +
                ", responseBody='" + responseBody + '\'' +
                ", dataUsed='" + dataUsed + '\'' +
                '}';
    }
}
