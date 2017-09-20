package in.meshworks.controllers;

import in.meshworks.beans.ExternalProfile;
import in.meshworks.beans.Profile;
import in.meshworks.mongo.MongoFactory;
import in.meshworks.services.ExternalService;
import in.meshworks.services.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dhruv.suri on 20/09/17.
 */
@RestController
public class ExternalController {

    @Autowired
    ExternalService service;
    @Autowired
    MongoFactory mongoFactory;

    @RequestMapping(value = "otp", method = RequestMethod.GET)
    public ResponseEntity<Boolean> otp(@RequestParam("mobileNumber") final String mobileNumber, @RequestParam("source") final String source, @RequestParam("sender") final String sender, @RequestParam("msg") final String msg) {
        return service.otp(mobileNumber, source, sender, msg);
    }
}
