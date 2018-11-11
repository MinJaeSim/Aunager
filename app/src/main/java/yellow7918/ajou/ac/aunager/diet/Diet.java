package yellow7918.ajou.ac.aunager.diet;


public class Diet {
    private String userName;
    private int type;
    private String date;
    private String meal;
    private int day;
    private String profileImageUrl;
    private String uid;

    public Diet() {

    }

    public Diet(String userName, int type, String date, String meal, int day, String profileImageUrl, String uid) {
        this.userName = userName;
        this.type = type;
        this.date = date;
        this.meal = meal;
        this.day = day;
        this.profileImageUrl = profileImageUrl;
        this.uid = uid;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
