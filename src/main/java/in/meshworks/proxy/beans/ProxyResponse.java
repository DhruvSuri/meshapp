package in.meshworks.proxy.beans;

import okhttp3.Request;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by dhruv.suri on 31/05/17.
 */
@Document
public class ProxyResponse {
    private String name;
    private String phoneNumber;
    private int responseStatus;
    private String requestUrl;
    private long requestSentAt;
    private long requestReceivedAt;
    private long responseSentAt;
    private long responseReceivedAt;
    private long dataUsed;
    private String responseBody;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public String toString() {
        return "ProxyResponse{" +
                ", phone=" + phoneNumber +
                ", name=" + name +
                ", responseStatus=" + responseStatus +
                ", request=" + requestUrl +
                ", requestSentAt=" + new Date(requestSentAt) +
                ", requestReceivedAt=" + new Date(requestReceivedAt) +
                ", responseSentAt=" + new Date(responseSentAt) +
                ", responseReceivedAt=" + new Date(responseReceivedAt) +
                ", responseBody='" + responseBody.substring(0,50) + '\'' +
                ", dataUsed='" + dataUsed + '\'' +
                '}';
    }
}
