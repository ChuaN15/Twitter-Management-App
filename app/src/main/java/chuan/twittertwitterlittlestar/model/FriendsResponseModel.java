package chuan.twittertwitterlittlestar.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evamoni on 24-Oct-17.
 */

public class FriendsResponseModel {



    @SerializedName("next_cursor_str")
    @Expose
    private String nextCursorStr;

    @SerializedName("next_cursor")
    @Expose
    private BigInteger nextCursor;

    @SerializedName("users")
    @Expose
    private List<TwitterFriends> results = new ArrayList<TwitterFriends>();





    public BigInteger getNextCursor() {
        return nextCursor;
    }

    public String getNextCursorStr() {
        return nextCursorStr;
    }

    public void setNextCursorStr(String nextCursorStr) {
        this.nextCursorStr = nextCursorStr;
    }

    public List<TwitterFriends> getResults() {
        return results;
    }

    public void setResults(List<TwitterFriends> results) {
        this.results = results;
    }
}
