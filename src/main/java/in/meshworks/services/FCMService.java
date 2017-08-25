package in.meshworks.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.util.JSON;
import in.meshworks.beans.Profile;
import in.meshworks.beans.ProxyResponse;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harshvardhansharma on 25/08/17.
 */
@Service
public class FCMService {

    @Autowired
    private ProfileService profileService;

    private static String API_KEY = "key=AAAAo5XKUC0:APA91bFiMrrWa2D4gw9RTO1cY5xAE8JjQD6riwIxUnL1YCYmpPts1tYhpvv-oCSEadCesqLp73EzuvXcbQbuisKQPk8R12YGLEDsp9zIGbSiBEmjNFstQfg8EBu_walSuHsyfZzYl-U5";

    public String initiateNodes() {
        return callFCMAPI("START");
    }

    public void terminateNodes() {
        callFCMAPI("STOP");
    }

    private String callFCMAPI(String op) {
        List<Profile> profiles = profileService.getAllProfiles();

        StringBuilder requestBodyStringBuilder = new StringBuilder("{ \"data\": { \"OP\" : \"" + op + "\"}, \"registration_ids\": [");
        int index = 0;
        for (index = 0; index < profiles.size(); index++) {
            Profile profile = profiles.get(index);
            if (profile.getFcmToken() != null && !profile.getFcmToken().isEmpty()) {
                requestBodyStringBuilder.append("\"" + profile.getFcmToken() + "\"");
                if (index < profiles.size() - 1) {
                    requestBodyStringBuilder.append(", ");
                }
            }
        }
        requestBodyStringBuilder.append("] }");

        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .header("Authorization", API_KEY)
                .post(RequestBody.create(MediaType.parse("application/json"), requestBodyStringBuilder.toString()))
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

}
