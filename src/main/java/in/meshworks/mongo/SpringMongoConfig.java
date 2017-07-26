package in.meshworks.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.Arrays;

@Configuration
public class SpringMongoConfig {

    public
    @Bean
    MongoDbFactory mongoDbFactory() throws Exception {
        return new SimpleMongoDbFactory(new MongoClient(new ServerAddress("13.126.216.38"), Arrays.asList(MongoCredential.createScramSha1Credential("elb","admin","elbproxymesh".toCharArray()))), "local");
    }

    public
    @Bean
    MongoTemplate mongoTemplate() throws Exception {

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());

        return mongoTemplate;

    }

//    db.createUser(
//    {
//        user: "myUserAdmin",
//                pwd: "abc123",
//            roles: [ { role: "userAdminAnyDatabase", db: "admin" } ]
//    }
//    )


//    MongoCredential.createScramSha1Credential("elb","admin","elbproxymesh".toCharArray()))), "local")

}

