package yellow7918.ajou.ac.aunager.social;

import java.io.Serializable;

public class SocialText implements Serializable {
    private String category;
    private String email;
    private String title;
    private String text;
    private long date;

    public SocialText() {

    }

    public SocialText(String category, String email, String title, String text, long date) {
        this.category = category;
        this.email = email;
        this.title = title;
        this.text = text;
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
