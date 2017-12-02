package in.meshworks.services;

import in.meshworks.beans.NeverBounceResponse;
import in.meshworks.utils.AzazteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

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
        String url = "https%3A%2F%2Fapi.neverbounce.com%2Fv4%2Fpoe%2Fcheck%3Fkey%3Dpub_nvrbnc_registration%26email%3D" + email + "%26callback%3D" + callback;
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
        StringTokenizer tokenizer = new StringTokenizer(body, "()");
        String parsedBody = tokenizer.nextToken();
        parsedBody = tokenizer.nextToken();
        return AzazteUtils.fromJson(parsedBody, NeverBounceResponse.class);
    }

    public static void main(String args[]) {
        String sampleResponse = callback + "({\"status\":\"success\",\"result\":\"valid\",\"flags\":[\"smtp_connectable\",\"has_dns\",\"has_dns_mx\"],\"suggested_correction\":\"\",\"next_limit\":11,\"allow_entry\":true,\"transaction_id\":\"NBPOE-TXN-5a22eed880e99\",\"confirmation_token\":\"fc5687d9d1fb94291ca3315e7b2c7bc7\",\"retry_token\":\"\",\"execution_time\":425})%";
        EmailService service = new EmailService();
        service.parseResponse(sampleResponse);
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
