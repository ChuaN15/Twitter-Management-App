package chuan.twittertwitterlittlestar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evamoni on 24-Oct-17.
 */

public class FollowersResponseModel {

    @SerializedName("next_cursor")
    @Expose
    private BigInteger nextCursor;

    @SerializedName("users")
    @Expose
    private List<TwitterFollowers> results = new ArrayList<TwitterFollowers>();




    public BigInteger getNextCursor() {
        return nextCursor;
    }

    public void setNextCursorStr(BigInteger nextCursor) {
        this.nextCursor = nextCursor;
    }

    public List<TwitterFollowers> getResults() {
        return results;
    }


    public void setResults(List<TwitterFollowers> results) {
        this.results = results;
    }
}
