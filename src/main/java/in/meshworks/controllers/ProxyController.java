package in.meshworks.controllers;

import in.meshworks.proxy.ProxyService;
import in.meshworks.proxy.beans.Profile;
import okhttp3.OkHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by harshvardhansharma on 04/07/17.
 */

@RestController
public class ProxyController {

    @Autowired
    ProxyService proxyService;

    @RequestMapping(value = "proxy", method = RequestMethod.GET)
    public String proxyGET(@RequestParam("url") String url, @RequestHeader(required = false) HttpHeaders headers, @RequestParam(value = "timeout", required = false) Integer timeout) {
        if (timeout == null) {
            timeout = 30;
        }
        return proxyService.doProxy(url, headers, null, timeout, "GET");
    }

    @RequestMapping(value = "proxy", method = RequestMethod.POST)
    public String proxyPOSTJson(@RequestParam("url") String url, @RequestBody String requestBodyString, @RequestHeader HttpHeaders headers, @RequestParam(value = "timeout", required = false) Integer timeout) {
        if (timeout == null) {
            timeout = 30;
        }

        return proxyService.doProxy(url, headers, requestBodyString, timeout, "POST");
    }

    @RequestMapping(method = RequestMethod.GET, value = "listview")
    public String getListInfo() {
        return proxyService.list();
    }


//    @RequestMapping(value = "proxy", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
//    public String proxyPOSTXml(@RequestBody String requestBodyString, @RequestParam("url") String url) {
//        return "done";
//    }


}
