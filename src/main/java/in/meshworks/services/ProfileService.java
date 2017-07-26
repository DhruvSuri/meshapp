package in.meshworks.services;

import in.meshworks.beans.ProxyResponse;
import in.meshworks.mongo.MongoFactory;
import in.meshworks.beans.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
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

    public void createOrUpdateProfile(String name, String mobileNumber, String referral) {
        Profile profile = null;
        try {
            profile = new Profile(name, mobileNumber, referral);
            mongoFactory.getMongoTemplate().save(profile);
        } catch (Exception failed) {
            failed.printStackTrace();
            log.error(failed.getMessage() + "Failed to save profile " + profile);
        }
    }

    public int getNibsCount(String mobilNumber) {
        Profile profileFetched = findByMobileNumber(mobilNumber);
        return profileFetched.getNibsCount();
    }

    public Profile verifyOTP(String mobileNumber, String token) {
        Profile profile = findByMobileNumber(mobileNumber);
        if (profile.getLatestOTP().equals(token)){
            return profile;
        }
        return null;
    }

    public void initiateProfile(String mobileNumber, String deviceId) {
        String otp = otpService.sendOTP(mobileNumber);
        Profile profile = findByMobileNumber(mobileNumber);
        if (profile == null){
            createOrUpdateProfile("",mobileNumber,"");
        }
        saveLatestOTP(mobileNumber,otp);
    }

    public Profile findByMobileNumber(String mobileNumber){
        Query query = new Query();
        query.addCriteria(Criteria.where("mobileNumber").is(mobileNumber));
        return mongoFactory.getMongoTemplate().findOne(query, Profile.class);
    }

    private void saveLatestOTP(String mobileNumber, String otp){
        Profile profile = findByMobileNumber(mobileNumber);
        profile.setLatestOTP(otp);
        mongoFactory.getMongoTemplate().save(profile);
    }

    public void calculateNibs() {
        List<Profile> list = mongoFactory.getMongoTemplate().findAll(Profile.class);
        for (Profile profile : list) {
            Query query = new Query();
            query.addCriteria(Criteria.where("mobileNumber").is(profile.getMobileNumber()));
            List<ProxyResponse> responses = mongoFactory.getMongoTemplate().find(query, ProxyResponse.class);
            int count = 0;
            for (ProxyResponse response : responses) {
                count += response.getDataUsed();
            }
            profile.setNibsCount(getNibsFromBytes(count));

            query = new Query();
            query.addCriteria(Criteria.where("referralNumber").is(profile.getMobileNumber()));
            long referralCount = mongoFactory.getMongoTemplate().count(query,Profile.class);
            profile.setReferralCount((int)referralCount);
            mongoFactory.getMongoTemplate().save(profile);
        }
    }

    public int getNibsFromBytes(int count){
        int nibs = 60;
        nibs = nibs * count;
        nibs = nibs / 1000;
        nibs = nibs / 1000;
        nibs = nibs / 1000;
        return nibs +1;
    }
}
