package in.meshworks.beans;

import java.io.Serializable;

/**
 * Created by harshvardhansharma on 09/08/17.
 */
public class Parcel implements Serializable {

    private String url;
    private String jsInjection;

    public Parcel() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJsInjection() {
        return jsInjection;
    }

    public void setJsInjection(String jsInjection) {
        this.jsInjection = jsInjection;
    }
}



