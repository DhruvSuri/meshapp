package in.meshworks.services;

import in.meshworks.beans.ExternalProfile;
import in.meshworks.mongo.MongoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * Created by dhruv.suri on 20/09/17.
 */
@Service
public class ExternalService {

    @Autowired
    MongoFactory mongoFactory;

    @Autowired
    OTPService otpService;

    public ResponseEntity<String> otp(String authKey, String tag, String mobileNumber, String sender, String msg, String requestKey) {
        if (isValidRequest(authKey, tag, mobileNumber, requestKey)) {
            String otp = otpService.sendOTP(mobileNumber, tag, msg);
            ExternalProfile profile = findByMobileNumber(mobileNumber);
            if (profile == null) {
                profile = new ExternalProfile(mobileNumber, sender);
            }
            profile.setLatestOTP(otp);
            profile.setOtpCount(profile.getOtpCount() + 1);
            mongoFactory.getMongoTemplate().save(profile);
            return ResponseEntity.ok(otp);
        } else {
            return ResponseEntity.badRequest().body("Invalid Keys");
        }
    }

    public ExternalProfile findByMobileNumber(final String mobileNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where("mobileNumber").is(mobileNumber));
        return mongoFactory.getMongoTemplate().findOne(query, ExternalProfile.class);
    }

    private boolean isValidRequest(String authKey, String tag, String mobileNumber, String requestKey) {
        String requestPart1 = getMashedKeys(authKey, tag, mobileNumber);

        StringTokenizer stk = new StringTokenizer(requestKey, ".");
        return stk.nextToken().equals(requestPart1) && validateTimeSpan(stk.nextToken());
    }

    private String getMashedKeys(String authKey, String tag, String mobileNumber) {
        String keys = authKey + tag + mobileNumber;
        return digest(keys);
    }

    private String digest(String data) {
        try {
            return new String(MessageDigest.getInstance("MD5").digest(data.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            return "";
        }
    }

    private boolean validateTimeSpan(String t1) {
        try {
            long timestamp = Long.parseLong(t1);
            return timestamp >= 0;
        }
        catch (Exception ex) {
            return false;
        }

    }
}
