package in.meshworks.services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by dhruv.suri on 06/07/17.
 */
@Service
public class OTPService {
    private static String API_KEY = "168153Af6mUytiO59822219";

    public String sendOTP(String phone) {
        OkHttpClient client = new OkHttpClient();
        int code = randomPin();
        String url = String.format("http://api.msg91.com/api/sendotp.php?authkey=%s&mobile=%s&message=%s is your OTP for Retonet&sender=RETONT&otp=%s", API_KEY, phone, code, code);
        Request request = new Request.Builder()
                .url(url)
                .header("content-type", "application/x-www-form-urlencoded")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.isSuccessful() ? "" + code : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int randomPin() {
        return (int) (Math.random() * 9000) + 1000;
    }
}
