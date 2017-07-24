package in.meshworks.beans;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by dhruv.suri on 22/06/17.
 */
@Document
public class Profile {

    private String id;
    private String name;
    @Indexed(unique = true)
    private String mobileNumber;
    private String deviceId;
    private String referralNumber;
    private float nibsCount;
    private boolean isVerified;
    private int latestOTP;

    public Profile() {
    }

    public Profile(String name, String mobileNumber, String referral) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.referralNumber = referral;
    }

    public Profile(String name, String mobileNumber) {
        this.name = name;
        this.mobileNumber = mobileNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public float getNibsCount() {
        return nibsCount;
    }

    public void setNibsCount(float nibsCount) {
        this.nibsCount = nibsCount;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getReferralNumber() {
        return referralNumber;
    }

    public void setReferralNumber(String referralNumber) {
        this.referralNumber = referralNumber;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public int getLatestOTP() {
        return latestOTP;
    }

    public void setLatestOTP(int latestOTP) {
        this.latestOTP = latestOTP;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", referralNumber='" + referralNumber + '\'' +
                ", nibsCount=" + nibsCount +
                ", isVerified=" + isVerified +
                ", latestOTP=" + latestOTP +
                '}';
    }
}
