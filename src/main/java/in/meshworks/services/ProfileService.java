package in.meshworks.services;

import in.meshworks.beans.ProxyResponse;
import in.meshworks.mongo.MongoFactory;
import in.meshworks.beans.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Boolean> updateProfile(final String name, final String mobileNumber, final String referral) {
        Profile profile = findByMobileNumber(mobileNumber);
        if (profile == null) {
            return ResponseEntity.badRequest().body(false);
        }

        profile.setName(name);
        profile.setReferralNumber(referral);
        try {
            mongoFactory.getMongoTemplate().save(profile);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            log.error(ex.getMessage() + " Failed to save profile: " + profile);
        }

        return ResponseEntity.ok(true);
    }

    public int getNibsCount(String mobilNumber) {
        Profile profileFetched = findByMobileNumber(mobilNumber);
        return profileFetched.getNibsCount();
    }

    public ResponseEntity<Profile> verifyOTP(final String mobileNumber, final String token) {
        Profile profile = findByMobileNumber(mobileNumber);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        if (profile.getLatestOTP().equals(token)) {
            return ResponseEntity.ok(profile);
        }
        else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    public ResponseEntity<Boolean> initiateProfile(final String mobileNumber, final String deviceId) {
        String otp = otpService.sendOTP(mobileNumber);
        if (otp == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Profile profile = findByMobileNumber(mobileNumber);
        if (profile == null){
            profile = new Profile(null, mobileNumber, null, deviceId);
        }
        profile.setLatestOTP(otp);
        mongoFactory.getMongoTemplate().save(profile);
        return ResponseEntity.ok(true);
    }

    public Profile findByMobileNumber(final String mobileNumber){
        Query query = new Query();
        query.addCriteria(Criteria.where("mobileNumber").is(mobileNumber));
        return mongoFactory.getMongoTemplate().findOne(query, Profile.class);
    }

    private void saveLatestOTP(final String mobileNumber, final String otp){
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
