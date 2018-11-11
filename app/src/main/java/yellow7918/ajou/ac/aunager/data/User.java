package yellow7918.ajou.ac.aunager.data;

public class User {
    private String email;
    private String name;
    private int age;
    private String profileImage;
    private String gender;

    public User() {
    }

    public User(String email, String name, int age, String profileImage, String gender) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.profileImage = profileImage;
        this.gender = gender;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
