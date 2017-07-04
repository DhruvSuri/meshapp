package in.meshworks.proxy.beans;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by dhruv.suri on 22/06/17.
 */
public class Profile {
    private String name;
    @Indexed
    private String phoneNumber;
    private int nibsEarned;
    private int nibsRedeemed;

    public Profile() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getNibsEarned() {
        return nibsEarned;
    }

    public void setNibsEarned(int nibsEarned) {
        this.nibsEarned = nibsEarned;
    }

    public int getNibsRedeemed() {
        return nibsRedeemed;
    }

    public void setNibsRedeemed(int nibsRedeemed) {
        this.nibsRedeemed = nibsRedeemed;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", nibsEarned=" + nibsEarned +
                ", nibsRedeemed=" + nibsRedeemed +
                '}';
    }
}
