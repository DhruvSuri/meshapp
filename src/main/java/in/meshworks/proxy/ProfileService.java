package in.meshworks.proxy;

import com.mongodb.DBObject;
import in.meshworks.Mongo.MongoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
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
