package in.meshworks.utils;

import com.google.gson.Gson;
import in.meshworks.beans.Res;
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
        while (i < 100) {
            i++;
            new Thread() {
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();


                        Request request = new Request.Builder()
                                .url("https://wnsly2hzi6.execute-api.ap-south-1.amazonaws.com/Prod?url=http://checkip.amazonaws.com&type=0")
                                .build();
                        Response execute = client.newCall(request).execute();
                        synchronized (map){
                            String ip = execute.body().string();
                            Integer count = map.get(ip);
                            if (count == null) {
                                count = 0;
                            }
                            map.put(ip, count + 1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(map.toString());
        System.out.println("Total Count " + proxyCount);
        System.out.println("Unique Count" + map.keySet().size());

    }
}
