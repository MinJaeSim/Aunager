package yellow7918.ajou.ac.aunager.routine;


public class Routine {
    private String userName;
    private String time;
    private String doing;
    private boolean isOn;
    private int routineId;
    private String profileImageUrl;
    private String uid;

    public Routine() {

    }

    public Routine(String userName, String time, String doing, boolean isOn, int alarmId, String profileImageUrl,String uid) {
        this.userName = userName;
        this.time = time;
        this.doing = doing;
        this.isOn = isOn;
        this.routineId = alarmId;
        this.profileImageUrl = profileImageUrl;
        this.uid = uid;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }

    public int getRoutineId() {
        return routineId;
    }

    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDoing() {
        return doing;
    }

    public void setDoing(String doing) {
        this.doing = doing;
    }
}
