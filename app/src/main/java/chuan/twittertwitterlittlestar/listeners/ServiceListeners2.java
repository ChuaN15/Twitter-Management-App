package chuan.twittertwitterlittlestar.listeners;

import java.math.BigInteger;

import chuan.twittertwitterlittlestar.model.FollowersResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Altaf on 28-Oct-17.
 */

public interface ServiceListeners2 {
    //For getting friends :  @GET("1.1/friends/list.json")
    @GET("1.1/followers/list.json")
    Call<FollowersResponseModel> list(@Query("user_id") long id,  @Query("cursor") BigInteger cursor, @Query("count") int count);
}


