package in.meshworks.proxy;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by dhruv.suri on 11/05/17.
 */
@Service
public class ProxyService {

    @Autowired
    SocketService socketService;

    public String doProxy(String url, HttpHeaders headers, String requestBodyString, int timeout, String requestType) {
        if (url.isEmpty()) {
            return "Url should not be emply";
        }
        Request request;
        if (requestType == "GET") {
            request = buildRequestGET(url, headers);
        } else {
            request = buildRequestPOST(url, headers, requestBodyString);
        }

        return socketService.sendProxyRequest(request, timeout);
    }

    private Request buildRequestGET(String url, HttpHeaders headers) {
        Request.Builder builder = new Request.Builder()
                .url(url);
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getKey());
        }
        return builder.build();
    }

    private Request buildRequestPOST(String url, HttpHeaders headers, String requestBody) {
        Request.Builder builder = new Request.Builder()
                .url(url);

        builder.post(RequestBody.create(MediaType.parse(headers.getContentType().toString()), requestBody));
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getKey());
        }
        return builder.build();
    }

    public String list() {
        return socketService.getConnections();
    }
}
