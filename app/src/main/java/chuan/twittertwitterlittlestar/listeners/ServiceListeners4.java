package chuan.twittertwitterlittlestar.listeners;



import java.util.ArrayList;

import chuan.twittertwitterlittlestar.model.FavoritesResponseModel;
import chuan.twittertwitterlittlestar.model.TwitterUser;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
/**
 * Created by User on 5/2/2018.
 */

public interface ServiceListeners4 {
    @GET("1.1/favorites/list.json")
    Call<ArrayList<FavoritesResponseModel>> list(@Query("user_id") long id, @Query("count") int count);
/*
    @FormUrlEncoded
    @POST("1.1/followers/ids.json" +
            "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13")
    Call<Tweet> getFollowerNumber(@Field("user_id") Long userId,
                                  @Field()*/
}
