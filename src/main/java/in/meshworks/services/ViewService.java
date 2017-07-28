package in.meshworks.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by dhruv.suri on 28/07/17.
 */
@Service
public class ViewService {
    @Autowired
    SocketService socketService;

    public void generateViews(String url, int numbOfViews, int timeSpan) {
        Random r = new Random();

        int i = 0;
        while (i < numbOfViews) {
            int random = r.nextInt(timeSpan + 1);
            ViewRunner viewRunner = new ViewRunner(url, random);
            new Thread(viewRunner).start();
            i++;
        }
    }

    private class ViewRunner implements Runnable {
        private String url;
        private int sleep;

        public ViewRunner(String url, int sleep) {
            this.url = url;
            this.sleep = sleep * 1000;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(this.sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            socketService.webviewRequest(url);
        }
    }
}
