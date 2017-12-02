package in.meshworks.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dhruv.suri on 26/11/17.
 */
public class NeverBounceResponse {
    String status;
    String result;

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
}
