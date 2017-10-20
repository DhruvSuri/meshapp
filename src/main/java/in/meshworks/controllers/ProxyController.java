package in.meshworks.controllers;

import in.meshworks.services.DomainHandler;
import in.meshworks.services.ProxyService;
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
    DomainHandler domainService;

    @RequestMapping(value = "temp", method = RequestMethod.GET)
    public ResponseEntity<Object> webview() {
        return new ResponseEntity<>("Sent Successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "proxy", method = RequestMethod.GET)
    public ResponseEntity<Object> proxyGET(@RequestParam("url") String url, @RequestHeader(required = false) HttpHeaders headers, @RequestParam(value = "timeout", required = false) Integer timeout, @RequestParam(value = "header", required = false) boolean headersAllowed) {
        if (timeout == null) {
            timeout = 30;
        }

//        if (!domainService.isValidDomain(url)){
//            return new ResponseEntity<Object>("Invalid Domain", HttpStatus.BAD_REQUEST);
//        }

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

    @RequestMapping(method = RequestMethod.GET, value = "addHost")
    public void addHost(@RequestParam("url") String url) {
        domainService.addDomain(url);
    }

    @RequestMapping(method = RequestMethod.GET, value = "getHosts")
    public String getHosts() {
        return domainService.getValidDomains();
    }

    @RequestMapping(method = RequestMethod.GET, value = "resetHosts")
    public String resetHosts() {
        return domainService.resetHosts();
    }
}
