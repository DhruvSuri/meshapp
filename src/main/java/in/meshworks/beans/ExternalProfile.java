package in.meshworks.beans;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by dhruv.suri on 20/09/17.
 */
@Document
public class ExternalProfile {
    private String id;
    private String source;
    @Indexed(unique = true)
    private String mobileNumber;
    private int otpCount;
    private String latestOTP;

    public ExternalProfile(String mobileNumber, String source) {
        this.mobileNumber = mobileNumber;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getOtpCount() {
        return otpCount;
    }

    public void setOtpCount(int otpCount) {
        this.otpCount = otpCount;
    }

    public String getLatestOTP() {
        return latestOTP;
    }

    public void setLatestOTP(String latestOTP) {
        this.latestOTP = latestOTP;
    }
}
