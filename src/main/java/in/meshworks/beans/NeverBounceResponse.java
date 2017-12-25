package in.meshworks.beans;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;

/**
 * Created by dhruv.suri on 26/11/17.
 */
public class NeverBounceResponse {
    String status;
    String result;
    ArrayList<String> flags;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public ArrayList<String> getFlags() {
        return flags;
    }

    public void setFlags(ArrayList<String> flags) {
        this.flags = flags;
    }

    public EmailResponse getSuccessEmailResponse() {
        EmailResponse response = new EmailResponse();
        response.status = HttpStatus.ACCEPTED.toString();
        response.result = this.result;
        for (String flag : flags) {
            switch (flag) {
                case "has_dns":
                    response.dns = true;
                case "accepts_all":
                    response.acceptsAll = true;
                case "has_dns_mx":
                    response.dnsMx = true;
                case "bad_dns":
                    response.badDns = true;
            }
        }
        return response;
    }

    public EmailResponse getFailureResponse(String errorCode) {
        EmailResponse response = new EmailResponse();
        response.status = HttpStatus.EXPECTATION_FAILED.toString();
        response.result = "Error code = " + errorCode + ".Contact Support. Thanks for joining our Beta program";
        return response;
    }
}
