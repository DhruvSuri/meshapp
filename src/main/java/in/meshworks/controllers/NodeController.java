package in.meshworks.controllers;

import in.meshworks.beans.Profile;
import in.meshworks.services.FCMService;
import in.meshworks.services.ProfileService;
import in.meshworks.services.SocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by harshvardhansharma on 04/07/17.
 */
@RestController
public class NodeController {

    @Autowired
    ProfileService profileService;

    @Autowired
    FCMService fcmService;

    @RequestMapping(value = "updateProfile", method = RequestMethod.GET)
    public ResponseEntity<Boolean> updateProfile(@RequestParam("mobileNumber") String mobileNumber, @RequestParam("name") String name, @RequestParam(value = "referral", required = false) String referral) {
        return profileService.updateProfile(name, mobileNumber, referral);
    }

    @RequestMapping(value = "verifyOTP", method = RequestMethod.GET)
    public ResponseEntity<Profile> verifyOTP(@RequestParam("mobileNumber") String mobileNumber, @RequestParam("token") String token) {
//        Receiving complete SMS in tokeken
        return profileService.verifyOTP(mobileNumber, token.substring(0,4));
    }

    @RequestMapping(value = "initiateProfile", method = RequestMethod.GET)
    public ResponseEntity<Boolean> initiateProfile(@RequestParam("mobileNumber") String mobileNumber, @RequestParam("deviceId") String deviceId) {
        return profileService.initiateProfile(mobileNumber, deviceId);
    }

    @RequestMapping(value = "updateFCMToken", method = RequestMethod.GET)
    public ResponseEntity<Boolean> updateFCMToken(@RequestParam("mobileNumber") String mobileNumber, @RequestParam("token") String token) {
        return profileService.updateToken(mobileNumber, token);
    }

    @RequestMapping(value = "initiateNodes", method = RequestMethod.GET)
    public ResponseEntity<String> initiateNodes() {
        ResponseEntity<String> responseEntity = ResponseEntity.ok(fcmService.initiateNodes());
        return responseEntity;
    }

    @RequestMapping(value = "terminateNodes", method = RequestMethod.GET)
    public ResponseEntity<Boolean> terminateNodes() {
        fcmService.terminateNodes();
        ResponseEntity<Boolean> responseEntity = ResponseEntity.ok().body(true);
        return responseEntity;
    }

}
