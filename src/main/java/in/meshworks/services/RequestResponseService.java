package in.meshworks.services;

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

    public Request.Builder buildGetRequest(final String url, final HttpHeaders headers) {
        Request.Builder builder = new Request.Builder().cacheControl(CacheControl.FORCE_NETWORK).url(url);
        mapHeaders(builder, headers);
        return builder;
    }

    public Request.Builder buildPostRequest(final String url, final HttpHeaders headers, final String requestBody) {
        Request.Builder builder = new Request.Builder().cacheControl(new CacheControl.Builder().noCache().build()).url(url);

        builder.post(RequestBody.create(MediaType.parse(headers.getContentType().toString()), requestBody));
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            builder.header(entry.getKey(), entry.getKey());
        }
        return builder;
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

    private void mapHeaders(Request.Builder requestBuilder, HttpHeaders httpHeaders) {
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            if (!entry.getKey().equals("host")) {
                requestBuilder.header(entry.getKey(), listToCSV(entry.getValue()));
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
