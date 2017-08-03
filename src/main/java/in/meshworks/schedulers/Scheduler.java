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
     * To be run every 1 min ~ 1 * 60 * 1000 ms
     */
    @Scheduled(fixedRate = 10000)
    public void monitorConnections(){
        socketService.sendNibsRequest();
    }

    /**
     * To be run every 10 mins ~ 10 * 60 * 1000 ms
     */
    @Scheduled(fixedRate = 600000)
    public void updateDataConsumptionStats(){
        socketService.updateDataConsumptionStats();
    }

    @Scheduled(fixedRate = 21600000)
    public void calculateNibs(){
        profileService.calculateNibs();
    }
}
