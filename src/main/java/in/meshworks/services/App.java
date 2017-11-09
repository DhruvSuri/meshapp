package in.meshworks.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by harshvardhansharma on 07/11/17.
 */
public class App {

//    public static void main(String args[]) {
//        int i = 0;
//        Map<String, Integer> map = new HashMap<>();
//        int proxyCount = 0;
//        while (i < 100) {
//            i++;
//            new Thread() {
//                public void run() {
//                    try {
//                        OkHttpClient client = new OkHttpClient();
//
//
//                        Request request = new Request.Builder()
//                                .url("http://api.flaircraft.co/proxy?url=http://checkip.amazonaws.com&type=0")
//                                .build();
//                        Response execute = client.newCall(request).execute();
//                        synchronized (map){
//                            String ip = execute.body().string();
//                            Integer count = map.get(ip);
//                            if (count == null) {
//                                count = 0;
//                            }
//                            map.put(ip, count + 1);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.start();
//        }
//
//        try {
//            Thread.sleep(15000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println(map.toString());
//        System.out.println("Total Count " + proxyCount);
//        System.out.println("Unique Count" + map.keySet().size());
//
//    }

}
