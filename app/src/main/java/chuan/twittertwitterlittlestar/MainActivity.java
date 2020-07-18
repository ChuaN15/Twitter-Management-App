package chuan.twittertwitterlittlestar;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.app.FragmentManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chuan.twittertwitterlittlestar.client.MyTwitterApiClient;
import chuan.twittertwitterlittlestar.model.FollowersResponseModel;
import chuan.twittertwitterlittlestar.model.FriendsResponseModel;
import chuan.twittertwitterlittlestar.model.TwitterFollowers;
import chuan.twittertwitterlittlestar.model.TwitterFriends;
import chuan.twittertwitterlittlestar.model.TwitterUser;
import chuan.twittertwitterlittlestar.model.TwitterUser;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements
        FollowingFragment.OnFragmentInteractionListener,
        FollowerFragment.OnFragmentInteractionListener,
        NotFollowerFragment.OnFragmentInteractionListener,
        UnfollowerFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    TwitterUser twitterUser;

    TextView tv3;

    private String mParam1;
    private String mParam2;
    TwitterSession twitterSession;
    TwitterAuthToken twitterAuthToken;
    ArrayList<String> imgid = new ArrayList<String>();
    private ImageView dialog_img;
    Button hello;
    Button close;

    public Dialog MyDialog;
    ArrayList<String> user_id = new ArrayList<String>();
    long loggedUserTwitterId;

    ListView mainListView;

    public ArrayAdapter<String> listAdapter ;

    List<TwitterFriends> twitterFriends;

    List<TwitterFollowers> twitterFollowers;

    ArrayList<String> friendsList = new ArrayList<String>();

    ArrayList<String> followersList = new ArrayList<String>();

    ArrayList<String> followers_img = new ArrayList<String>();
    ArrayList<String> followers_user = new ArrayList<String>();

    ArrayList<String> notfollowersList = new ArrayList<String>();

    ArrayList<String> unfollowersList = new ArrayList<String>();

    private UnfollowerFragment.OnFragmentInteractionListener mListener;
    ArrayList<Long> notfollowersId = new ArrayList<Long>();
    ArrayList<Long> followersId = new ArrayList<Long>();
    private BigInteger cursor = BigInteger.valueOf(-1);
    private boolean bool;
    private List<TwitterFollowers> otherFollowers;
    private List<TwitterFriends> otherFollowing;
    private TextView num_following_view;
    private TextView num_follower_view;
    ArrayList<TwitterFriends> object = new ArrayList<TwitterFriends>();
    ArrayList<TwitterFollowers> object2 = new ArrayList<TwitterFollowers>();
    ArrayList<Long> ids = new ArrayList<Long>();
    ArrayList<Long> followers_id = new ArrayList<Long>();
    private ProgressBar loader;

    ArrayList<String> userList = new ArrayList<String>();

    private TextView tvunfollowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.CONSUMER_KEY), getString(R.string.CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);

        setContentView(R.layout.activity_main);

        twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        twitterAuthToken = twitterSession.getAuthToken();
        loggedUserTwitterId = twitterSession.getId();



        TextView tv2 = (TextView) findViewById(R.id.tv2) ;
        tvunfollowers = (TextView) findViewById(R.id.tv4) ;
        tv3 = (TextView) findViewById(R.id.tv3) ;

        tv2.setText(twitterSession.getUserName());
        //tv3.setText(String.valueOf(twitterSession.getUserId()));
        tv3.setText("");

        SharedPreferences pref = this.getSharedPreferences("MyPref",0);
        SharedPreferences.Editor editor = pref.edit();

        String string1 = pref.getString("usernane","");
        String string2 = twitterSession.getUserName().toString();

        if(string1.equals(string2))
        {
            loadTwitterFollowers2();
            Log.e("222", "onCreate: " );
        }
        else
        {
            editor.putString("usernane",twitterSession.getUserName().toString()).apply();
            loadTwitterFollowers();
            Log.e("22","haih" );
        }

        loadTwitterFriends();

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


    }

    public BigInteger getCursor(){
        return this.cursor;
    }
    public void setCursor(BigInteger cursorValue){
        Log.e("setCursor","set");
        this.cursor = cursorValue;
    }

    private List<TwitterFollowers> fetchResults2(Response<FollowersResponseModel> response) {
        FollowersResponseModel responseModel = response.body();
        if (responseModel != null) {
            return responseModel.getResults();
        }
        else{
            return Collections.emptyList();
        }
    }

    private BigInteger fetchResults4(Response<FollowersResponseModel> response) {
        FollowersResponseModel responseModel = response.body();
        if (responseModel != null) {
            Log.e("lol",String.valueOf(responseModel.getNextCursor()));
            return responseModel.getNextCursor();
        }
        else{
            Log.e("lol","dsa");
            return BigInteger.ZERO;
        }

    }

    public void setBool(boolean bool){
        this.bool = bool;
    }
    public boolean getBool(){
        return this.bool;
    }

    private void loadTwitterFollowers() {
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService2().list(loggedUserTwitterId, getCursor(), 200).enqueue(new retrofit2.Callback<FollowersResponseModel>() {
            @Override
            public void onResponse(Call<FollowersResponseModel> call, Response<FollowersResponseModel> response) {
                Log.e("onResponse", response.toString());
                twitterFollowers = fetchResults2(response);
                setCursor(fetchResults4(response));


                Set<String> set1 = new HashSet<String>();
                Set<String> set2 = new HashSet<String>();
                Set<String> set3 = new HashSet<String>();

                for (int k=0;k<twitterFollowers.size();k++){
                    set1.add(twitterFollowers.get(k).getName());
                    set2.add(twitterFollowers.get(k).getProfilePictureUrl().replace("_normal",""));
                    set3.add(twitterFollowers.get(k).getScreenName());
                    Log.e("Twitter Friends","Id:"+twitterFollowers.get(k).getId()+" Name:"+twitterFollowers.get(k).getName()+" pickUrl:"+twitterFollowers.get(k).getProfilePictureUrl());
                }

                SharedPreferences pref = getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();

                editor.putStringSet("lastFollowersList", set1).apply();
                editor.putStringSet("imgid2", set2).apply();
                editor.putStringSet("user_id2", set3).apply();
            }

            @Override
            public void onFailure(Call<FollowersResponseModel> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }

        });
    }

    private void loadTwitterFollowers2() {
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService2().list(loggedUserTwitterId, getCursor(), 200).enqueue(new retrofit2.Callback<FollowersResponseModel>() {
            @Override
            public void onResponse(Call<FollowersResponseModel> call, Response<FollowersResponseModel> response) {
                Log.e("onResponse", response.toString());
                twitterFollowers = fetchResults2(response);
                setCursor(fetchResults4(response));


                for (int k = 0; k < twitterFollowers.size(); k++) {
                    object2.add(twitterFollowers.get(k));
                    followersList.add(twitterFollowers.get(k).getName());
                    followers_img.add(twitterFollowers.get(k).getProfilePictureUrl().replace("_normal",""));
                    followers_user.add(twitterFollowers.get(k).getScreenName());
                    followers_id.add(twitterFollowers.get(k).getId());
                    Log.e("Twitter Friends", "Id:" + twitterFollowers.get(k).getId() + " Name:" + twitterFollowers.get(k).getName() + " pickUrl:" + twitterFollowers.get(k).getProfilePictureUrl());
                }
                if (!getCursor().equals(BigInteger.ZERO)) {
                    loadTwitterFollowers2();
                }
                else{
                    setBool(true);
                }

                SharedPreferences pref = getSharedPreferences("MyPref", 0); // 0 - for private mode

                Set<String> set = new HashSet<String>(pref.getStringSet("lastFollowersList", new HashSet<String>()));
                Set<String> imgid2 = new HashSet<String>(pref.getStringSet("imgid2", new HashSet<String>()));
                Set<String> user_id2 = new HashSet<String>(pref.getStringSet("user_id2", new HashSet<String>()));

                Log.e("123", "onCreateView: " + imgid2.size() );

                for(int i = 0;i < followersList.size();i++)
                {
                    if(set.contains(followersList.get(i).toString()))
                    {
                        user_id2.remove(followers_user.get(i).toString());
                        set.remove(followersList.get(i).toString());
                        imgid2.remove(followers_img.get(i).toString());
                        i--;

                        Log.e("12345", "onCreateView: " + imgid2.size() );
                    }
                }

                Log.e("1234", "onCreateView: " + imgid2.size() );

                unfollowersList.addAll(set);
                user_id.addAll(user_id2);
                imgid.addAll(imgid2);

                tv3.setText(String.valueOf(unfollowersList.size()) + " Unfollowers since your last check");
                tvunfollowers.setText("");

                if(unfollowersList.size() == 0)
                {
                    Set<String> set1 = new HashSet<String>();
                    Set<String> set2 = new HashSet<String>();
                    Set<String> set3 = new HashSet<String>();

                    for (int k=0;k<twitterFollowers.size();k++){
                        set1.add(twitterFollowers.get(k).getName());
                        set2.add(twitterFollowers.get(k).getProfilePictureUrl().replace("_normal",""));
                        set3.add(twitterFollowers.get(k).getScreenName());
                        Log.e("Twitter Friends","Id:"+twitterFollowers.get(k).getId()+" Name:"+twitterFollowers.get(k).getName()+" pickUrl:"+twitterFollowers.get(k).getProfilePictureUrl());
                    }

                    SharedPreferences.Editor editor = pref.edit();

                    editor.putStringSet("lastFollowersList", set1).apply();
                    editor.putStringSet("imgid2", set2).apply();
                    editor.putStringSet("user_id2", set3).apply();
                }
            }

            @Override
            public void onFailure(Call<FollowersResponseModel> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }

        });
    }

    private void initialiseNavigation() {
        ImageView iv1 = (ImageView) findViewById(R.id.iv1) ;
        String bigger = twitterUser.getProfilePictureUrl().replace("_normal", "");
        Picasso.with(this).load(bigger).resize(iv1.getWidth(),iv1.getHeight()).noFade().into(iv1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        ImageView iv_header = hView.findViewById(R.id.image_profile);

        TextView iv_text_name = hView.findViewById(R.id.headerText1);
        TextView iv_text_sn = hView.findViewById(R.id.headerText2);
        iv_text_name.setText(String.valueOf(twitterUser.getName()));
        iv_text_sn.setText("@"+String.valueOf(twitterUser.getScreenName()));
        TextView iv_text1 = hView.findViewById(R.id.following_number_header);
        TextView iv_text2 = hView.findViewById(R.id.follower_number_header);
        iv_text1.setText(String.valueOf(twitterUser.getFriends()));
        iv_text2.setText(String.valueOf(twitterUser.getFollowers()));


        Log.e("1111",String.valueOf(iv_header));
        Log.e("112",twitterUser.getProfilePictureUrl());
        Picasso.with(this).load(bigger).fit().into(iv_header);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void loadTwitterFriends() {
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        Log.e("id",String.valueOf(loggedUserTwitterId));
        myTwitterApiClient.getCustomTwitterService3().list(loggedUserTwitterId).enqueue(new retrofit2.Callback<TwitterUser>() {
            @Override
            public void onResponse(Call<TwitterUser> call, Response<TwitterUser> response) {
                Log.e("onResponseMain", response.toString());
                twitterUser = fetchResults(response);
                Log.e("user",String.valueOf(twitterUser));
                initialiseNavigation();
            }

            @Override
            public void onFailure(Call<TwitterUser> call, Throwable t) {
                Log.e("onFailure", t.toString());
                initialiseNavigation();
            }
        });
    }

    private TwitterUser fetchResults(Response<TwitterUser> response) {
        TwitterUser responseModel = response.body();
        if (responseModel != null) {
            Log.e("lol","asd");
            return responseModel;
        }
        else{
            Log.e("1","dsa");
            //Server returning null objects
            return new TwitterUser();
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_camera) {
            FollowingFragment fragmentt = new FollowingFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,fragmentt);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_gallery) {
            FollowerFragment fragmentt = new FollowerFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,fragmentt);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_slideshow) {
            NotFollowerFragment fragmentt = new NotFollowerFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,fragmentt);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }  else if (id == R.id.nav_unfollowers) {
            UnfollowerFragment fragmentt = new UnfollowerFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame,fragmentt);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}


