package chuan.twittertwitterlittlestar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Altaf on 28-Oct-17.
 */

public class TwitterUnFollowers {

    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("profile_image_url_https")
    @Expose
    private String profilePictureUrl;

    @SerializedName("screen_name")
    @Expose
    private String screenName;

    @SerializedName("friends_count")
    @Expose
    private long friendsCount;

    @SerializedName("followers_count")
    @Expose
    private long followersCount;

    public long getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public long getFriends() {
        return friendsCount;
    }

    public long getFollowers() {
        return followersCount;
    }

    public String getScreenName() {
        return screenName;
    }

}
