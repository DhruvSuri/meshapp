package in.meshworks.controllers;

import in.meshworks.proxy.ProfileService;
import in.meshworks.proxy.ProxyService;
import in.meshworks.proxy.beans.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by harshvardhansharma on 04/07/17.
 */
@RestController
public class NodeController {

    @Autowired
    ProfileService profileService;

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public void completeProfile(@RequestParam("name") String name, @RequestParam("phoneNumber") String phoneNumber, @RequestParam(value = "referral", required = false) String referral) {
        profileService.createOrUpdateProfile(name, phoneNumber, referral);
    }

    @RequestMapping(value = "verifyotp", method = RequestMethod.GET)
    public Profile verifyOTP(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("token") String token) {
        return profileService.verifyOTP(phoneNumber, token);
    }

    @RequestMapping(value = "sendotp", method = RequestMethod.GET)
    public void initiateProfile(@RequestParam("phoneNumber") String phoneNumber, @RequestParam("deviceId") String deviceId) {
        profileService.initiateProfile(phoneNumber, deviceId);
    }
}
