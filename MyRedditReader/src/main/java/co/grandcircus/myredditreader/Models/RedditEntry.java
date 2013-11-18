package co.grandcircus.myredditreader.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Matt on 11/4/13.
 */
public class RedditEntry implements Parcelable {

    private String title;
    private String thumbnailURL;

    public RedditEntry() {}

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

    /*
     * Methods for making this object parcelable, which makes it able to be passed between
     * activities.
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(title);
        out.writeString(thumbnailURL);
    }

    public static final Parcelable.Creator<RedditEntry> CREATOR =
            new Parcelable.Creator<RedditEntry>() {

                @Override
                public RedditEntry createFromParcel(Parcel in) {
                    return new RedditEntry(in);
                }

                @Override
                public RedditEntry[] newArray(int i) {
                    return new RedditEntry[i];
                }
            };

    private RedditEntry(Parcel in) {
        title = in.readString();
        thumbnailURL = in.readString();
    }

}
