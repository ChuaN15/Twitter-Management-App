package chuan.twittertwitterlittlestar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evamoni on 24-Oct-17.
 */

public class FavoritesResponseModel {


    @SerializedName("user")
    @Expose
    private TwitterUser results = new TwitterUser();




    public TwitterUser getResults() {
        return results;
    }
}
