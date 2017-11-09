package in.meshworks.services;

import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by dhruv.suri on 05/11/17.
 */
@Service
public class MixpanelService {
    private MessageBuilder messageBuilder =
            new MessageBuilder("140fba6eddfc9a770329caa45b53ae21");
    private MixpanelAPI mixpanel = new MixpanelAPI();


    public void track(String id, String event) {
        try {
            JSONObject sentEvent =
                    messageBuilder.event(id, event, null);
            mixpanel.sendMessage(sentEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void trackGeneric(String event, String value){

        try {
            JSONObject properties = new JSONObject();
            properties.put(event, value);
            JSONObject update =
                    messageBuilder.append("13793", properties);
            mixpanel.sendMessage(update);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        new MixpanelService().trackGeneric("test", "1");
        new MixpanelService().trackGeneric("test", "2");
        new MixpanelService().trackGeneric("test", "3");
        new MixpanelService().trackGeneric("test", "4");
    }
}
