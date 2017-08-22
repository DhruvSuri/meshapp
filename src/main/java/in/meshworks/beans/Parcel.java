package in.meshworks.beans;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Created by harshvardhansharma on 09/08/17.
 */
@Document
public class Parcel implements Serializable {

    private String id;
    private String url;
    private String jsInjection;
    private String description;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}



