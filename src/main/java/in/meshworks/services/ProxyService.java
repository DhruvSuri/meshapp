package in.meshworks.services;

import in.meshworks.beans.ProxyResponse;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by dhruv.suri on 11/05/17.
 */
@Service
public class ProxyService {

    @Autowired
    SocketService socketService;

    @Autowired
    RequestResponseService requestResponseService;

    public ResponseEntity<Object> proxy(String url, HttpHeaders headers, String requestBody, int timeout, HttpMethod httpMethod) {
        Request.Builder request;
        switch (httpMethod){
            case GET:
                request = requestResponseService.buildGetRequest(url, headers);
                break;
            case POST:
                request = requestResponseService.buildPostRequest(url, headers, requestBody);
                break;
            default:
                request = null;
        }
        ProxyResponse proxyResponse = socketService.getProxyResponse(request, timeout);
        if (proxyResponse == null) {
            return new ResponseEntity<>("No nodes available", HttpStatus.SERVICE_UNAVAILABLE);
        }
        return requestResponseService.buildGenericResponse(proxyResponse);
    }

    public String list() {
        return socketService.getConnections();
    }
}
