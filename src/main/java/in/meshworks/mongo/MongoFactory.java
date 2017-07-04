package in.meshworks.mongo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by home on 27/06/16.
 */
@Service
public class MongoFactory {
    private static MongoTemplate mongoTemplate;

    private MongoFactory() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
        mongoTemplate = (MongoTemplate) ctx.getBean("mongoTemplate");
    }


    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}
