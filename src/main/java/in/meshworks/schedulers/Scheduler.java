package in.meshworks.schedulers;

import in.meshworks.services.ProfileService;
import in.meshworks.services.ProxyService;
import in.meshworks.services.SocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by dhruv.suri on 30/05/17.
 */
@Component
public class Scheduler {
    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
    @Autowired
    SocketService socketService;

    @Autowired
    ProxyService proxyService;

    @Autowired
    ProfileService profileService;

    @Scheduled(fixedRate = 10000)
    public void monitorConnections(){
        socketService.sendNibsRequest();
    }

    @Scheduled(fixedRate = 1000000)
    public void calculateNibs(){
        profileService.calculateNibs();
    }
}
