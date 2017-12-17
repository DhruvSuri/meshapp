package in.meshworks.controllers;

import in.meshworks.services.DomainHandler;
import in.meshworks.services.EmailService;
import in.meshworks.services.NodeService;
import in.meshworks.services.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by harshvardhansharma on 04/07/17.
 */

@RestController
public class ProxyController {

    @Autowired
    ProxyService proxyService;

    @Autowired
    DomainHandler domainService;

    @Autowired
    EmailService emailService;

    @RequestMapping(value = "temp", method = RequestMethod.GET)
    public ResponseEntity<Object> webview() {
        return new ResponseEntity<>("Sent Successfully", HttpStatus.CREATED);
    }

    @RequestMapping(value = "email", method = RequestMethod.GET)
    public ResponseEntity<Object> emailGET(@RequestParam("email") String email){
        return emailService.verifyEmail(email);
    }

    @RequestMapping(value = "proxy", method = RequestMethod.GET)
    public ResponseEntity<Object> proxyGET(@RequestParam("url") String url, @RequestParam Map<String,String> allRequestParams, @RequestHeader(required = false) HttpHeaders headers, @RequestParam(value = "timeout", required = false) Integer timeout, @RequestParam(value = "headersAllowed", required = false) boolean headersAllowed) {
        if (timeout == null) {
            timeout = 30;
        }

        if (url == null || url.isEmpty()) {
            return new ResponseEntity<Object>("Invalid URL", HttpStatus.BAD_REQUEST);
        }

        ResponseEntity<Object> response = proxyService.proxy(url, headers, null, timeout, HttpMethod.GET, headersAllowed, listTypeParams(0));
        System.out.println(response.getBody().toString());
        System.out.println(response.toString());
        return response;
    }

    @RequestMapping(value = "proxy", method = RequestMethod.POST)
    public ResponseEntity<Object> proxyPOST(@RequestParam("url") String url, @RequestHeader HttpHeaders headers, @RequestBody String requestBody, @RequestParam(value = "timeout", required = false) Integer timeout, @RequestParam(value = "headersAllowed", required = false) boolean headersAllowed) {
        if (timeout == null) {
            timeout = 30;
        }

        if (url == null || url.isEmpty()) {
            return new ResponseEntity<Object>("Invalid URL", HttpStatus.BAD_REQUEST);
        }

        return proxyService.proxy(url, headers, requestBody, timeout, HttpMethod.POST, headersAllowed, listTypeParams(0));
    }

    @RequestMapping(method = RequestMethod.GET, value = "listview")
    public String getListInfo(@RequestParam(value = "type", required = false) Integer type) {
        return proxyService.list(listTypeParams(type));
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

    private NodeService.ListType listTypeParams(int type) {
        if (type != 0) {
            return NodeService.ListType.ULTIMATE;
        }
        else {
            return NodeService.ListType.BASIC;
        }
    }
}
