package in.meshworks.controllers;

import in.meshworks.services.ProfileService;
import in.meshworks.beans.Profile;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public void completeProfile(@RequestParam("name") String name, @RequestParam("mobileNumber") String mobileNumber, @RequestParam(value = "referral", required = false) String referral) {
        profileService.createOrUpdateProfile(name, mobileNumber, referral);
    }

    @RequestMapping(value = "verifyotp", method = RequestMethod.GET)
    public Profile verifyOTP(@RequestParam("mobileNumber") String mobileNumber, @RequestParam("token") String token) {
        return profileService.verifyOTP(mobileNumber, token);
    }

    @RequestMapping(value = "sendotp", method = RequestMethod.GET)
    public void initiateProfile(@RequestParam("mobileNumber") String mobileNumber, @RequestParam("deviceId") String deviceId) {
        profileService.initiateProfile(mobileNumber, deviceId);
    }
}
