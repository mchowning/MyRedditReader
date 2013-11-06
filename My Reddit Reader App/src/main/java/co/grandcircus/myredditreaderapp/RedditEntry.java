package co.grandcircus.myredditreaderapp;

/**
 * Created by Matt on 11/4/13.
 */
public class RedditEntry {

    private String title;
    private String thumbnailURL;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String toString() {
        return getTitle();
    }
}
