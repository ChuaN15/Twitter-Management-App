package chuan.twittertwitterlittlestar.client;

import chuan.twittertwitterlittlestar.listeners.ServiceListeners;
import chuan.twittertwitterlittlestar.listeners.ServiceListeners2;
import chuan.twittertwitterlittlestar.listeners.ServiceListeners3;
import chuan.twittertwitterlittlestar.listeners.ServiceListeners4;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * Created by Altaf on 28-Oct-17.
 */

public class MyTwitterApiClient extends TwitterApiClient {

    public MyTwitterApiClient(TwitterSession session) {
        super(session);
    }

    /**
     * Provide CustomService with defined endpoints
     */
    public ServiceListeners getCustomTwitterService() {
        return getService(ServiceListeners.class);
    }

    public ServiceListeners2 getCustomTwitterService2() {
        return getService(ServiceListeners2.class);
    }

    public ServiceListeners3 getCustomTwitterService3() {
        return getService(ServiceListeners3.class);
    }

    public ServiceListeners4 getCustomTwitterService4() {
        return getService(ServiceListeners4.class);
    }
}
