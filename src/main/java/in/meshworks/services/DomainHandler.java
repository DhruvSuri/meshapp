package in.meshworks.services;

import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dhruv.suri on 14/09/17.
 */
@Service
public class DomainHandler {
    private static Set<String> validDomains = new HashSet<>();

    public String getValidDomains(){
        return validDomains.toString();
    }

    public void addDomain(String urlString) {
        try {
            URL url = new URL(urlString);
            validDomains.add(url.getHost());
        } catch (MalformedURLException ignored) {}
    }

    public boolean isValidDomain(String urlString) {
        try {
            URL url = new URL(urlString);
            return validDomains.contains(url.getHost());
        } catch (MalformedURLException ignored) {}

        return false;
    }

    public String resetHosts() {
        validDomains = new HashSet<>();
        return getValidDomains();
    }
}
