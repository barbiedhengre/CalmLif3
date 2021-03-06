package co.in.nextgencoder.calmlif3.model;

public class Moment {
    private String id;
    private String title;
    private String mood;
    private String momentDescription;
    private String imageUrl;    // Feature yet to be added
    private boolean isPublic;
    private User user;

    public Moment() { }

    public Moment(String title, String mood, String momentDescription, User user) {
        this.title = title;
        this.mood = mood;
        this.momentDescription = momentDescription;
        this.user = user;
    }

    public Moment( String id, String title, String mood, String momentDescription) {
        this.title = title;
        this.mood = mood;
        this.momentDescription = momentDescription;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getMomentDescription() {
        return momentDescription;
    }

    public void setMomentDescription(String momentDescription) {
        this.momentDescription = momentDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
