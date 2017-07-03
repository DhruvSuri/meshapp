package in.meshworks;

import in.meshworks.Redis.RedisFactory;
import in.meshworks.proxy.ProxyService;
import in.meshworks.proxy.SocketService;
import in.meshworks.utils.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Queue;

@RestController
public class BaseController {

    @Autowired
    SocketService socketService;

    @Autowired
    MailService mailService;

    @Autowired
    ProxyService proxyService;


    @RequestMapping(method = RequestMethod.GET, value = "proxy")
    public String doProxy(WebRequest request, @RequestParam("url") String url, @RequestParam(value = "timeout", required = false) Integer timeout) {
        if (timeout == null){
            timeout = 30;
        }
        return proxyService.doProxy(request, url, timeout);
    }

    @RequestMapping(method = RequestMethod.GET, value = "listview")
    public String getListInfo() {
        return proxyService.list();
    }

    @RequestMapping(method = RequestMethod.GET, value = "mail")
    public void mail(@RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("size") String size, @RequestParam("msg") String msg) {
        String text = name + "  " + email + "  " + size + "  " + msg + "  ";
        RedisFactory.mail(text);
        new MailService().sendProxyMail(text);
    }



}
