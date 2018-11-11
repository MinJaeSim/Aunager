package yellow7918.ajou.ac.aunager.routine;


import java.io.Serializable;

public class Routine implements Serializable {
    private long date;
    private String email;

    private String weather;
    private String sleepHour;
    private boolean isToilet;
    private long toiletTime;
    private boolean isMedicine;

    private String extraInfo;

    public Routine() {

    }

    public Routine(long date, String email, String weather, String sleepHour, boolean isToilet, long toiletTime, boolean isMedicine, String extraInfo) {
        this.date = date;
        this.email = email;
        this.weather = weather;
        this.sleepHour = sleepHour;
        this.isToilet = isToilet;
        this.isMedicine = isMedicine;
        this.extraInfo = extraInfo;
        this.toiletTime = toiletTime;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getSleepHour() {
        return sleepHour;
    }

    public void setSleepHour(String sleepHour) {
        this.sleepHour = sleepHour;
    }

    public boolean isToilet() {
        return isToilet;
    }

    public void setToilet(boolean toilet) {
        isToilet = toilet;
    }

    public boolean isMedicine() {
        return isMedicine;
    }

    public void setMedicine(boolean medicine) {
        isMedicine = medicine;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public long getToiletTime() {
        return toiletTime;
    }

    public void setToiletTime(long toiletTime) {
        this.toiletTime = toiletTime;
    }
}
