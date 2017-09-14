package in.meshworks.controllers;

import in.meshworks.redis.RedisFactory;
import in.meshworks.services.ProxyService;
import in.meshworks.utils.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class BaseController {

    @Autowired
    ProxyService proxyService;

    @RequestMapping(method = RequestMethod.GET, value = "mail")
    public void mail(@RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("size") String size, @RequestParam("msg") String msg) {
        String text = name + "  " + email + "  " + size + "  " + msg + "  ";
        RedisFactory.mail(text);
        new MailService().sendProxyMail(text);
    }



}
