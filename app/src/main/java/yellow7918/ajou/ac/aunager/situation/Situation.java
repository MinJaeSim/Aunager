package yellow7918.ajou.ac.aunager.situation;

import java.io.Serializable;

public class Situation implements Serializable {
    private String situation;
    private String before;
    private String after;
    private String email;

    public Situation() {

    }

    public Situation(String situation, String before, String after, String email) {
        this.situation = situation;
        this.before = before;
        this.after = after;
        this.email = email;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
