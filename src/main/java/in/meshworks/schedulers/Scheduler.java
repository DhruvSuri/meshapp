package in.meshworks.schedulers;

import in.meshworks.services.ProfileService;
import in.meshworks.services.ProxyService;
import in.meshworks.services.SocketService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by dhruv.suri on 30/05/17.
 */
@Component
public class Scheduler {

    @Autowired
    SocketService socketService;

    @Autowired
    ProfileService profileService;

    /**
     * To be run every 5mins = 5 * 60 * 1000
     */
    @Scheduled(fixedRate = 300000)
    public void updateDataConsumptionStats(){
        socketService.updateDataConsumptionStats();
    }

    /**
     * To be run every 1 hour = 1 * 60 * 60 * 1000
     */
    @Scheduled(fixedRate = 3600000)
    public void calculateNibs(){
        profileService.calculateNibs();
    }
}
