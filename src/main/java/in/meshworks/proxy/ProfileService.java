package in.meshworks.proxy;

import in.meshworks.mongo.MongoFactory;
import in.meshworks.proxy.beans.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dhruv.suri on 03/07/17.
 */
@Service
public class ProfileService {
    private static final Logger log = LoggerFactory.getLogger(SocketService.class);

    @Autowired
    MongoFactory mongoFactory;

    @Autowired
    OTPService otpService;

    public void createOrUpdateProfile(String name, String phoneNumber, String referral) {
        Profile profile = null;
        try{
            profile = new Profile(name, phoneNumber, referral);
            mongoFactory.getMongoTemplate().save(profile);
        }catch(Exception failed){
            log.error("Failed to save profile " + profile);
        }
    }

    public Float getNibsCount(Profile profile) {
        if (profile == null){
            return null;
        }
        if (profile.getPhoneNumber() == null){
            return null;
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("phoneNumber").is(profile.getPhoneNumber()));
        Profile profileFetched = mongoFactory.getMongoTemplate().findOne(query, Profile.class);
        if (profileFetched == null){
            return null;
        }
        return profileFetched.getNibsCount();
    }

    public Profile verifyOTP(String phoneNumber, String token) {

        return null;
    }

    public void initiateProfile(String phoneNumber, String deviceId) {
        otpService.sendOTP(phoneNumber);
    }
}
