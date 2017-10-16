package in.meshworks.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by harshvardhansharma on 24/07/17.
 */
public class Req {

    private String method;
    private String url;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public Req() {
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
