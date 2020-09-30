package co.in.nextgencoder.calmlif3.model;

public class User {
    private String id;
    private String name;
    private String mail;
    private String picture;
    private String gender;
    private String bio;

    public User() {

    }

    public User(String name, String mail) {
        this.name = name;
        this.mail = mail;
    }


    public User(String name, String mail, boolean isVerified) {
        this.name = name;
        this.mail = mail;
    }

    public User( String id, boolean isVerified, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
