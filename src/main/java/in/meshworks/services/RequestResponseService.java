package in.meshworks.services;

import in.meshworks.beans.ProxyRequest;
import in.meshworks.beans.ProxyResponse;
import okhttp3.CacheControl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

/**
 * Created by harshvardhansharma on 08/07/17.
 */
@Component
public class RequestResponseService {

    public ProxyRequest buildGetRequest(final String url, final HttpHeaders headers) {
        ProxyRequest proxyRequest = new ProxyRequest();
        proxyRequest.setMethod("GET");
        proxyRequest.setUrl(url);
        mapHeaders(proxyRequest, headers);
        return proxyRequest;
    }

    public ProxyRequest buildPostRequest(final String url, final HttpHeaders headers, final String requestBody) {
        ProxyRequest proxyRequest = new ProxyRequest();
        proxyRequest.setMethod("GET");
        proxyRequest.setUrl(url);
        mapHeaders(proxyRequest, headers);
        proxyRequest.setBody(requestBody.getBytes());
        return proxyRequest;
    }

    public ResponseEntity<Object> buildGenericResponse(ProxyResponse proxyResponse) {
        Object responseBody = proxyResponse.getResponseBody();

        MultiValueMap<String, String> headers = mapToMultiValueMap(proxyResponse.getResponseHeaders());
        HttpStatus status = HttpStatus.valueOf(proxyResponse.getResponseCode());

        return new ResponseEntity<>(responseBody, headers, status);
    }

    private MultiValueMap<String, String> mapToMultiValueMap(Map<String, List<String>> map) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            multiValueMap.put(entry.getKey(), entry.getValue());
        }
        return multiValueMap;
    }

    private void mapHeaders(ProxyRequest proxyRequest, HttpHeaders httpHeaders) {
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            if (!entry.getKey().equals("host")) {
                proxyRequest.getHeaders().put(entry.getKey(), listToCSV(entry.getValue()));
            }
        }
    }

    private String listToCSV(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String item : list) {
            sb.append(item);
            sb.append(";");
        }

        return sb.substring(0, sb.lastIndexOf(";"));
    }


}
