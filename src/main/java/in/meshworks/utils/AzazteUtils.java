package in.meshworks.utils;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by home on 27/06/16.
 */
public class AzazteUtils {
    private static Gson GSON_INSTANCE = new Gson();

    public static <T> T fromJson(String data, Class<T> clazz) {
        return GSON_INSTANCE.fromJson(data, clazz);
    }

    public static String toJson(Object obj) {
        return GSON_INSTANCE.toJson(obj);
    }

    public static long time() {
        return new Date().getTime();
    }


    public static void main(String args[]) {

        int i = 0;
        Map<String, Integer> map = new HashMap();
        int proxyCount = 0;
        while (i < 1000) {
            try {
                i++;
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("http://api.flaircraft.co:8080/proxy?url=http://checkip.amazonaws.com")
                        .build();
                Response execute = client.newCall(request).execute();
                String ip = execute.body().string();
                Integer count = map.get(ip);
                if (count == null) {
                    count = 0;
                }
                proxyCount++;
                map.put(ip, count + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println(map.toString());
        System.out.println(map.keySet().size());
        System.out.println(proxyCount);
    }
}
