package in.meshworks.services;

import in.meshworks.beans.Parcel;
import in.meshworks.beans.Profile;
import in.meshworks.mongo.MongoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by dhruv.suri on 28/07/17.
 */
@Service
public class ParcelService {

    @Autowired
    SocketService socketService;

    @Autowired
    MongoFactory mongoFactory;

    public void generateViews(String url, int numbOfViews, int timeSpan, String parcelId) {
        Random r = new Random();

        Parcel parcel = findById(parcelId);
        parcel.setUrl(url);
        int i = 0;
        while (i < numbOfViews) {
            int random = r.nextInt(timeSpan + 1);
            ViewRunner viewRunner = new ViewRunner(parcel, random);
            new Thread(viewRunner).start();
            i++;
        }
    }

    private class ViewRunner implements Runnable {
        private Parcel parcel;
        private int sleep;

        public ViewRunner(Parcel parcel, int sleep) {
            this.parcel = parcel;
            this.sleep = sleep * 1000;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(this.sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            socketService.webviewRequest(parcel);
        }
    }

    public Parcel findById(final String parcelId){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(parcelId));
        return mongoFactory.getMongoTemplate().findOne(query, Parcel.class);
    }
}
