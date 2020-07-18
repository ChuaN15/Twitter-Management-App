package chuan.twittertwitterlittlestar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import chuan.twittertwitterlittlestar.client.MyTwitterApiClient;
import chuan.twittertwitterlittlestar.listeners.MyListener;
import chuan.twittertwitterlittlestar.listeners.cursorListener;
import chuan.twittertwitterlittlestar.model.FollowersResponseModel;
import chuan.twittertwitterlittlestar.model.FriendsResponseModel;
import chuan.twittertwitterlittlestar.model.TwitterFollowers;
import chuan.twittertwitterlittlestar.model.TwitterFriends;
import retrofit2.Call;
import retrofit2.Response;

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
import com.twitter.sdk.android.core.models.Tweet;
import chuan.twittertwitterlittlestar.model.FollowersResponseModel;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FollowingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean bool;
    public BigInteger cursor;
    //public long cursor2 = 0;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TwitterSession twitterSession;
    TwitterAuthToken twitterAuthToken;

    private TextView num_follower_view;
    private TextView num_following_view;
    long loggedUserTwitterId;

    Button buttonTwitterLogin;
    ListView mainListView;
    ArrayList<Long> following = new ArrayList<Long>();
    ArrayList<Long> follower = new ArrayList<Long>();
    List<TwitterFriends> twitterFriends;
    ArrayList<Long> ids = new ArrayList<Long>();
    ArrayList<String> friendsList = new ArrayList<String>();

    ArrayList<String> imgid = new ArrayList<String>();

    ArrayList<String> user_id = new ArrayList<String>();
    Button hello;
    Button close;
    public Dialog MyDialog;

    public CustomListAdapter adapter;
    private OnFragmentInteractionListener mListener;
    private ImageView dialog_img;
    private List<TwitterFollowers> otherFollowers;
    private List<TwitterFriends> otherFollowing;
    ArrayList<TwitterFriends> object = new ArrayList<TwitterFriends>();
    private ProgressBar loader;

    public FollowingFragment() {
        // Required empty public constructor
        this.cursor = BigInteger.valueOf(-1);
        this.bool = false;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FollowingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FollowingFragment newInstance(String param1, String param2) {
        FollowingFragment fragment = new FollowingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void MyCustomAlertDialog(String url, final TwitterFriends object){
        final long following_num = object.getFriends();
        final long follower_num = object.getFollowers();
        final long id = object.getId();
        final String image = object.getProfilePictureUrl().replace("_normal","");
        MyDialog = new Dialog(this.getActivity());
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.fragment_dialog);
        MyDialog.setTitle("My Custom Dialog");
        dialog_img = MyDialog.findViewById(R.id.dialog_image);
        Picasso.with(this.getActivity()).load(url).fit().into(dialog_img);
        num_following_view = MyDialog.findViewById(R.id.following_number);
        num_following_view.setText(String.valueOf(following_num));
        num_follower_view = MyDialog.findViewById(R.id.follower_number);
        num_follower_view.setText(String.valueOf(follower_num));
        hello = (Button) MyDialog.findViewById(R.id.hello);
        close = (Button) MyDialog.findViewById(R.id.close);
        hello.setEnabled(true);

        close.setEnabled(true);

        hello.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Initialising stork", Toast.LENGTH_LONG).show();
                Intent i = new Intent(v.getContext(), DynamicRecyclerActivity.class);
                i.putExtra("id", id);
                i.putExtra("friend",ids);
                i.putExtra("image",image);
                i.putExtra("following_num",following_num);
                i.putExtra("follower_num",follower_num);
                startActivity(i);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDialog.cancel();
            }
        });

        MyDialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TwitterConfig config = new TwitterConfig.Builder(this.getActivity())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.CONSUMER_KEY), getString(R.string.CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following, container, false);

        twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        twitterAuthToken = twitterSession.getAuthToken();

        loggedUserTwitterId = twitterSession.getId();
        loader =  view.findViewById( R.id.progress_loader);
        loader.setVisibility(View.VISIBLE);

        adapter=new CustomListAdapter(this.getActivity(), friendsList, imgid, user_id);
        mainListView = (ListView) view.findViewById( R.id.mainListView5);

        //listAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.simplerow, friendsList);
        mainListView.setAdapter(adapter);
        Log.e("Times","zz");
        //setCursor(-1);

        loadTwitterFriends();

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final String item = imgid.get(position);
                MyCustomAlertDialog(item,object.get(position));

            }

        });

        return view;
    }


    public BigInteger getCursor(){
        return this.cursor;
    }
    public void setCursor(BigInteger cursorValue){
        this.cursor = cursorValue;
        Log.e("setCursor","set" + String.valueOf(this.cursor));
    }

    private void loadTwitterFriends() {
        Log.e("CursorIn",String.valueOf(getCursor()));
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().list(loggedUserTwitterId,getCursor(),200).enqueue(new retrofit2.Callback<FriendsResponseModel>() {
            @Override
            public void onResponse(Call<FriendsResponseModel> call, Response<FriendsResponseModel> response) {

                    Log.e("onResponselol", "Is response success" + response.isSuccessful());
                twitterFriends = fetchResults(response);


                setCursor(fetchResults3(response));
                Log.e("next cursor", "Cursor:" + getCursor());
                for (int k=0;k<twitterFriends.size();k++){
                    Log.e("value",String.valueOf(twitterFriends.get(k).getFriends()));
                    ids.add(twitterFriends.get(k).getId());
                    object.add(twitterFriends.get(k));
                    friendsList.add(twitterFriends.get(k).getName());
                    imgid.add(twitterFriends.get(k).getProfilePictureUrl().replace("_normal",""));
                    user_id.add(twitterFriends.get(k).getScreenName());
                    Log.e("Twitter Friends","Id:"+twitterFriends.get(k).getId()+" Name:"+twitterFriends.get(k).getName()+" pickUrl:"+twitterFriends.get(k).getProfilePictureUrl());
                }

                Log.e("onResponse", "twitterfriends:" + twitterFriends.size());
                if (!getCursor().equals(BigInteger.ZERO)) {
                    loadTwitterFriends();
                }
                else{
                    getActivity().setTitle(String.valueOf(friendsList.size()) + " Following");
                    adapter.notifyDataSetChanged();
                }

                loader.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<FriendsResponseModel> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });

    }



    private List<TwitterFriends> fetchResults(Response<FriendsResponseModel> response) {
        FriendsResponseModel responseModel = response.body();
        if (responseModel != null) {
            Log.e("lol","asd");
            return responseModel.getResults();
        }
        else{
            Log.e("1","dsa");
            //Server returning null objects
            return Collections.emptyList();
        }
    }


    private BigInteger fetchResults3(Response<FriendsResponseModel> response) {
        FriendsResponseModel responseModel = response.body();
        if (responseModel != null) {
            Log.e("lol",String.valueOf(responseModel.getNextCursor()));
            return responseModel.getNextCursor();
        }
        else{
            Log.e("2","dsa");
            return BigInteger.ZERO;
        }

    }


    public void sendMsg(long userId,String replyName,String msg){
        TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(session);
        Call<Tweet> call = myTwitterApiClient.getCustomTwitterService().sendPrivateMessage(userId,replyName,msg);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                Toast.makeText(getActivity(),"Message sent", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getActivity(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
