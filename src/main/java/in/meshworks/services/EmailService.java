package in.meshworks.services;

import in.meshworks.beans.NeverBounceResponse;
import in.meshworks.utils.AzazteUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by dhruv.suri on 26/11/17.
 */

@Service
public class EmailService {

    @Autowired
    ProxyService proxyService;

    private static String callback = "__neverbounce_729594";

    public ResponseEntity verifyEmail(String email) {
        String url = "https://api.neverbounce.com/v4/poe/check?key=pub_nvrbnc_registration&email=" + email + "&callback=" + callback;
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept-encoding", "gzip, deflate, br");
        headers.add("accept-language", "en-IN,en-GB;q=0.8,en-US;q=0.6,en;q=0.4");
        headers.add("cache-control", "no-cache");
        headers.add("connection", "keep-alive");
        headers.add("dnt", "1");
        headers.add("pragma", "no-cache");
        headers.add("referer", "https://app.neverbounce.com/register");
        headers.add("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

        ResponseEntity<Object> response = proxyService.proxy(url, headers, null, 30, HttpMethod.GET, true, NodeService.ListType.BASIC);
        System.out.println(response.getBody().toString());
        NeverBounceResponse neverBounceResponse = parseResponse((String) response.getBody());
        return processResponse(neverBounceResponse);
    }

    private ResponseEntity processResponse(NeverBounceResponse neverBounceResponse) {
        HttpStatus status;
        String body;
        if (neverBounceResponse.getStatus().equals("success")) {
            status = HttpStatus.OK;
            body = neverBounceResponse.getResult();
        } else {
            status = HttpStatus.EXPECTATION_FAILED;
            String errorCode;
            if (neverBounceResponse.getResult().equals("throttle_triggered")) {
                errorCode = getRateLimitErrorString();
            } else {
                errorCode = getSaltString(10);
            }

            body = "Error code = " + errorCode + ".Contact Support. Thanks for joining our Beta program";
        }
        return new ResponseEntity(body, new LinkedMultiValueMap<>(), status);
    }

    public NeverBounceResponse parseResponse(String body) {
        System.out.println("Recived body as : " + body);
        StringTokenizer tokenizer = new StringTokenizer(body, "()");
        String parsedBody = tokenizer.nextToken();
        parsedBody = tokenizer.nextToken();
        return AzazteUtils.fromJson(parsedBody, NeverBounceResponse.class);
    }

    public static void main(String args[]) {
        String url = "https://api.neverbounce.com/v4/poe/check?key=pub_nvrbnc_registration&email=" + "sdhruv93@gmail.com" + "&callback=" + callback;

        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder().url(url)
                .addHeader("accept-encoding", "gzip, deflate, br")
                .addHeader("accept-language", "en-IN,en-GB;q=0.8,en-US;q=0.6,en;q=0.4")
                .addHeader("cache-control", "no-cache")
                .addHeader("connection", "keep-alive")
                .addHeader("dnt", "1")
                .addHeader("pragma", "no-cache")
                .addHeader("referer", "https://app.neverbounce.com/register")
                .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36").build();

        try {
            Response response = client.newCall(req).execute();
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getRateLimitErrorString() {
        return "HVs420H";
    }

    public String getSaltString(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
