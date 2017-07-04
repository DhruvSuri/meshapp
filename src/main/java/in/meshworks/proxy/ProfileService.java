package in.meshworks.proxy;

import in.meshworks.mongo.MongoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dhruv.suri on 03/07/17.
 */
@Service
public class ProfileService {
    @Autowired
    MongoFactory mongoFactory;

    public void createOrUpdateProfile(String name, String phoneNumber, long nibsEarned) {
        
    }
}
