package in.meshworks.controllers;

import in.meshworks.proxy.ProxyService;
import in.meshworks.proxy.beans.Profile;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String proxyGET(WebRequest request, @RequestParam("url") String url, @RequestParam(value = "timeout", required = false) Integer timeout) {
        if (timeout == null){
            timeout = 30;
        }
        return proxyService.doProxy(request, url, timeout);
    }

    @RequestMapping(method = RequestMethod.GET, value = "listview")
    public String getListInfo() {
        return proxyService.list();
    }

    @RequestMapping(value = "proxy", method = RequestMethod.POST, @)
    public ResponseEntity<Object> proxyPOST(@RequestBody Object      requestBodyString, @RequestParam("url") String url) {
        return "done";
    }

    @RequestMapping(value = "proxy", method = RequestMethod.POST)
    public ResponseEntity<Object> proxyPOST(@RequestBody Object      requestBodyString, @RequestParam("url") String url) {
        return "done";
    }




}
