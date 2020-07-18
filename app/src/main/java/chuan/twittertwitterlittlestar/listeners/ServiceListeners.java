package chuan.twittertwitterlittlestar.listeners;

import chuan.twittertwitterlittlestar.model.FriendsResponseModel;
import com.twitter.sdk.android.core.models.Tweet;

import java.math.BigInteger;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Altaf on 28-Oct-17.
 */

public interface ServiceListeners {
    //For getting friends :  @GET("1.1/friends/list.json")
    @GET("1.1/friends/list.json")
    Call<FriendsResponseModel> list(@Query("user_id") long id, @Query("cursor") BigInteger cursor, @Query("count") int count);

    @FormUrlEncoded
    @POST("/1.1/direct_messages/new.json?" +
            "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    Call<Tweet> sendPrivateMessage(@Field("user_id") Long userId,
                                   @Field("screen_name") String screenName,
                                   @Field("text") String text);
}

