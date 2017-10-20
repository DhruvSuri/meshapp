package in.meshworks.services;

import in.meshworks.beans.Req;
import in.meshworks.beans.Res;
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

    public Req buildGetRequest(final String url, final HttpHeaders headers) {
        Req proxyRequest = new Req();
        proxyRequest.setMethod("GET");
        proxyRequest.setUrl(url);
        mapHeaders(proxyRequest, headers);
        return proxyRequest;
    }

    public Req buildPostRequest(final String url, final HttpHeaders headers, final String requestBody) {
        Req proxyRequest = new Req();
        proxyRequest.setMethod("GET");
        proxyRequest.setUrl(url);
        mapHeaders(proxyRequest, headers);
        proxyRequest.setBody(requestBody.getBytes());
        return proxyRequest;
    }

    public ResponseEntity<Object> buildGenericResponse(Res proxyResponse) {
        Object responseBody = proxyResponse.getBody();

        MultiValueMap<String, String> headers = mapToMultiValueMap(proxyResponse.getHeaders());
        HttpStatus status = HttpStatus.valueOf(proxyResponse.getCode());

        return new ResponseEntity<>(responseBody, headers, status);
    }

    private MultiValueMap<String, String> mapToMultiValueMap(Map<String, List<String>> map) {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        if (map != null && map.entrySet() != null) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                multiValueMap.put(entry.getKey(), entry.getValue());
            }
        }
        return multiValueMap;
    }

    private void mapHeaders(Req proxyRequest, HttpHeaders httpHeaders) {
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
