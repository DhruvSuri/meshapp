package in.meshworks.proxy;

import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by dhruv.suri on 11/05/17.
 */
@Service
public class ProxyService {

    @Autowired
    SocketService socketService;

    public String doProxy(WebRequest request, String url, int timeout) {
        if (url.isEmpty()) {
            return "Url should not be emply";
        }
        return socketService.sendProxyRequest(buildRequest(url),timeout);
    }

    public String list(){
        return socketService.getConnections();
    }

    private Request buildRequest(String url) {

        Request request = new Request.Builder()
                .url(url)
                .build();
        return request;
    }
}
