package in.meshworks.controllers;

import in.meshworks.services.ExternalService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "otp", method = RequestMethod.GET)
    public ResponseEntity<String> otp(
            @RequestParam("authKey") final String authKey,
            @RequestParam("tag") final String tag,
            @RequestParam("mobileNumber") final String mobileNumber,
            @RequestParam("sender") final String sender,
            @RequestParam("msg") final String msg,
            @RequestParam("requestKey") final String requestKey) {
        return service.otp(authKey, tag, mobileNumber, sender, msg, requestKey);
    }
}
