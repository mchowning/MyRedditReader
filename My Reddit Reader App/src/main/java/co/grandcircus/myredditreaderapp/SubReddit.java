package co.grandcircus.myredditreaderapp;

/**
 * Created by Matt on 11/6/13.
 */
public class SubReddit {

    private String name;
    private String url;     // urlExtension that follows http://www.reddit.com/

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return getName();
    }
}
