package in.meshworks.controllers;

import in.meshworks.proxy.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by harshvardhansharma on 04/07/17.
 */

@Controller
public class ProxyController {

    @Autowired
    ProxyService proxyService;

    @RequestMapping(value = "proxy", method = RequestMethod.GET)
    public String proxyGET(WebRequest request, @RequestParam("url") String url, @RequestParam(value = "timeout", required = false) Integer timeout) {
        if (timeout == null){
            timeout = 30;
        }
        return proxyService.doProxy(request, url, timeout);
    }

    @RequestMapping(value = "proxy", method = RequestMethod.POST)
    public String proxyPOST(WebRequest request, @RequestParam("url") String url, @RequestParam(value = "timeout", required = false) Integer timeout) {
        if (timeout == null){
            timeout = 30;
        }
        return proxyService.doProxy(request, url, timeout);
    }

}
