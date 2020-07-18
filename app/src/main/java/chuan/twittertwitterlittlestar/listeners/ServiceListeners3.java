package chuan.twittertwitterlittlestar.listeners;


import chuan.twittertwitterlittlestar.model.TwitterUser;
import chuan.twittertwitterlittlestar.model.UserResponseModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Altaf on 28-Oct-17.
 */

public interface ServiceListeners3 {
    //For getting friends :  @GET("1.1/friends/list.json")
    @GET("1.1/users/show.json")
    Call<TwitterUser> list(@Query("user_id") long id);
}


