package in.meshworks.controllers;

import in.meshworks.services.ProxyService;
import in.meshworks.services.ParcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by harshvardhansharma on 04/07/17.
 */

@RestController
public class ProxyController {

    @Autowired
    ProxyService proxyService;

    @Autowired
    ParcelService viewService;

    @RequestMapping(value = "temp", method = RequestMethod.GET)
    public ResponseEntity<Object> webview() {
        return new ResponseEntity<>("Sent Successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public ResponseEntity<Object> webview(@RequestParam("url") String url, @RequestParam("views") Integer views, @RequestParam("span") Integer span) {
        if (url == null || url.isEmpty()) {
            return new ResponseEntity<>("Invalid URL", HttpStatus.BAD_REQUEST);
        }

        viewService.generateViews(url, views, span);

        return new ResponseEntity<>("Sent Successfully", HttpStatus.CREATED);
    }


    @RequestMapping(value = "proxy", method = RequestMethod.GET)
    public ResponseEntity<Object> proxyGET(@RequestParam("url") String url, @RequestHeader(required = false) HttpHeaders headers, @RequestParam(value = "timeout", required = false) Integer timeout, @RequestParam(value = "header", required = false) boolean headersAllowed) {
        if (timeout == null) {
            timeout = 30;
        }

        if (url == null || url.isEmpty()) {
            return new ResponseEntity<Object>("Invalid URL", HttpStatus.BAD_REQUEST);
        }

        return proxyService.proxy(url, headers, null, timeout, HttpMethod.GET, headersAllowed);
    }

    @RequestMapping(value = "proxy", method = RequestMethod.POST)
    public ResponseEntity<Object> proxyPOST(@RequestParam("url") String url, @RequestHeader HttpHeaders headers, @RequestBody String requestBody, @RequestParam(value = "timeout", required = false) Integer timeout, @RequestParam(value = "header", required = false) boolean headersAllowed) {
        if (timeout == null) {
            timeout = 30;
        }

        if (url == null || url.isEmpty()) {
            return new ResponseEntity<Object>("Invalid URL", HttpStatus.BAD_REQUEST);
        }

        return proxyService.proxy(url, headers, requestBody, timeout, HttpMethod.POST, headersAllowed);
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
