package in.meshworks.services;

import in.meshworks.beans.Req;
import in.meshworks.beans.Res;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Created by dhruv.suri on 11/05/17.
 */
@Service
public class ProxyService {

    @Autowired
    SocketService socketService;

    @Autowired
    RequestResponseService requestResponseService;

    public ResponseEntity<Object> proxy(String url, HttpHeaders headers, String requestBody, int timeout, HttpMethod httpMethod, boolean headersAllowed, NodeService.ListType listType) {
        Req proxyRequest;
        switch (httpMethod) {
            case GET:
                    proxyRequest = requestResponseService.buildGetRequest(url, headers);
                break;
            case POST:
                proxyRequest = requestResponseService.buildPostRequest(url, headers, requestBody);
                break;
            default:
                proxyRequest = null;
        }
        if (!headersAllowed) {
            proxyRequest.setHeaders(new HashMap<>());
        }
        Res proxyResponse = socketService.getProxyResponse(proxyRequest, timeout, listType);
        if (proxyResponse == null) {
            return new ResponseEntity<>("No nodes available", HttpStatus.SERVICE_UNAVAILABLE);
        }else if (proxyResponse.getCode() == 801){
            return new ResponseEntity<>("Something went wrong with the website", HttpStatus.EXPECTATION_FAILED);
        }
        return requestResponseService.buildGenericResponse(proxyResponse);
    }

    public String list(NodeService.ListType listType) {
        return socketService.getConnections(listType);
    }
}
