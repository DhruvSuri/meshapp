package in.meshworks.services;

import in.meshworks.beans.ExternalProfile;
import in.meshworks.mongo.MongoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Created by dhruv.suri on 20/09/17.
 */
@Service
public class ExternalService {
    @Autowired
    MongoFactory mongoFactory;

    @Autowired
    OTPService otpService;

    public ResponseEntity<Boolean> otp(String mobileNumber, String source, String sender, String msg) {
        String otp = otpService.sendOTP(mobileNumber, sender, msg);
        ExternalProfile profile = findByMobileNumber(mobileNumber);
        if (profile == null) {
            profile = new ExternalProfile(mobileNumber, source);
        }
        profile.setLatestOTP(otp);
        profile.setOtpCount(profile.getOtpCount() + 1);
        mongoFactory.getMongoTemplate().save(profile);
        return ResponseEntity.ok(true);
    }

    public ExternalProfile findByMobileNumber(final String mobileNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where("mobileNumber").is(mobileNumber));
        return mongoFactory.getMongoTemplate().findOne(query, ExternalProfile.class);
    }
}
