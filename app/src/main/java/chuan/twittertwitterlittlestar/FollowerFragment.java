package chuan.twittertwitterlittlestar;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import chuan.twittertwitterlittlestar.client.MyTwitterApiClient;
import chuan.twittertwitterlittlestar.listeners.MyListener;
import chuan.twittertwitterlittlestar.model.FollowersResponseModel;
import chuan.twittertwitterlittlestar.model.FriendsResponseModel;
import chuan.twittertwitterlittlestar.model.TwitterFollowers;
import chuan.twittertwitterlittlestar.model.TwitterFriends;
import retrofit2.Call;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FollowerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FollowerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private boolean bool;
    private boolean bool2;
    public CustomListAdapter adapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TwitterSession twitterSession;
    TwitterAuthToken twitterAuthToken;
    public Dialog MyDialog;
    private ImageView dialog_img;
    Button hello;
    Button close;
    long loggedUserTwitterId;

    ListView mainListView;

    List<TwitterFollowers> twitterFollowers;

    List<TwitterFollowers> otherFollowers;

    ArrayList<String> followersList = new ArrayList<String>();

    ArrayList<String> imgid = new ArrayList<String>();

    ArrayList<String> user_id = new ArrayList<String>();

    BigInteger cursor = BigInteger.valueOf(-1);
    private OnFragmentInteractionListener mListener;
    private TextView num_follower_view;
    private List<TwitterFriends> otherFollowing;
    private TextView num_following_view;
    ArrayList<Long> following = new ArrayList<Long>();
    ArrayList<Long> follower = new ArrayList<Long>();
    ArrayList<TwitterFollowers> object2 = new ArrayList<TwitterFollowers>();
    private List<TwitterFriends> twitterFriends;
    ArrayList<Long> mutual_id = new ArrayList<>();
    private ProgressBar loader;

    public FollowerFragment() {
        // Required empty public constructor
        this.bool = false;
        this.bool2 = false;
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
    public static FollowerFragment newInstance(String param1, String param2) {
        FollowerFragment fragment = new FollowerFragment();
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

    public void MyCustomAlertDialog(String url, final TwitterFollowers object){
        final long following_num = object.getFriends();
        final long follower_num = object.getFollowers();
        MyDialog = new Dialog(this.getActivity());
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.fragment_dialog);
        MyDialog.setTitle("My Custom Dialog");
        final long id = object.getId();
        final String image = object.getProfilePictureUrl().replace("_normal","");
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
                i.putExtra("image",image);
                i.putExtra("friend",mutual_id);
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
        View view = inflater.inflate(R.layout.fragment_follower, container, false);

        twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        twitterAuthToken = twitterSession.getAuthToken();

        loggedUserTwitterId = twitterSession.getId();
        loader =  view.findViewById( R.id.progress_loader);
        loader.setVisibility(View.VISIBLE);

        mainListView = (ListView) view.findViewById( R.id.mainListView6 );
        adapter=new CustomListAdapter(this.getActivity(), followersList, imgid, user_id);
        //listAdapter = new ArrayAdapter<String>(this.getActivity(), R.layout.simplerow, followersList);
        mainListView.setAdapter(adapter);

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final String item = imgid.get(position);
                MyCustomAlertDialog(item,object2.get(position));

            }

        });
        loadTwitterFriends();
        loadTwitterFollowers();


        return view;
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

    private void loadTwitterFriends() {
        Log.e("CursorIn",String.valueOf(getCursor()));
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().list(loggedUserTwitterId,getCursor(),200).enqueue(new retrofit2.Callback<FriendsResponseModel>() {
            @Override
            public void onResponse(Call<FriendsResponseModel> call, Response<FriendsResponseModel> response) {

                Log.e("onResponselol", "Is response success" + response.isSuccessful());
                twitterFriends = fetchResults(response);

                setCursor(fetchResults5(response));
                Log.e("next cursorFriend", String.valueOf(twitterFriends.size()));
                for (int k=0;k<twitterFriends.size();k++){
                    Log.e("valueFriend",String.valueOf(twitterFriends.get(k).getFriends()));
                    mutual_id.add(twitterFriends.get(k).getId());
                    Log.e("Twitter Friends","Id:"+twitterFriends.get(k).getId()+" Name:"+twitterFriends.get(k).getName()+" pickUrl:"+twitterFriends.get(k).getProfilePictureUrl());
                }

                Log.e("onResponse", "twitterfriends:" + twitterFriends.size());
                if (!getCursor().equals(BigInteger.ZERO)) {
                    loadTwitterFriends();
                }
            }

            @Override
            public void onFailure(Call<FriendsResponseModel> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });

    }

    private void loadTwitterFollowers() {
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService2().list(loggedUserTwitterId, getCursor(), 200).enqueue(new retrofit2.Callback<FollowersResponseModel>() {
            @Override
            public void onResponse(Call<FollowersResponseModel> call, Response<FollowersResponseModel> response) {
                Log.e("onResponse",response.toString());
                twitterFollowers = fetchResults2(response);
                setCursor(fetchResults3(response));


                /*SharedPreferences pref = getContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();

                Set<String> set = new HashSet<String>();
                Set<String> set2 = new HashSet<String>();
                Set<String> set3 = new HashSet<String>();*/

                for (int k=0;k<twitterFollowers.size();k++){
                    object2.add(twitterFollowers.get(k));
                    followersList.add(twitterFollowers.get(k).getName());
                    imgid.add(twitterFollowers.get(k).getProfilePictureUrl().replace("_normal",""));
                    user_id.add(twitterFollowers.get(k).getScreenName());
                    /*set.add(twitterFollowers.get(k).getName());
                    set2.add(twitterFollowers.get(k).getProfilePictureUrl().replace("_normal",""));
                    set3.add(twitterFollowers.get(k).getScreenName());*/
                    Log.e("Twitter Friends","Id:"+twitterFollowers.get(k).getId()+" Name:"+twitterFollowers.get(k).getName()+" pickUrl:"+twitterFollowers.get(k).getProfilePictureUrl());
                }

                /*editor.putStringSet("lastFollowersList", set).apply();
                editor.putStringSet("imgid2", set2).apply();
                editor.putStringSet("user_id2", set3).apply();*/


                if (!getCursor().equals(BigInteger.ZERO)) {
                    loadTwitterFollowers();
                }
                else{
                    getActivity().setTitle(String.valueOf(followersList.size()) + " Followers");
                    adapter.notifyDataSetChanged();
                }

                loader.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<FollowersResponseModel> call, Throwable t) {
                Log.e("onFailure",t.toString());
            }

        });

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

    private BigInteger fetchResults3(Response<FollowersResponseModel> response) {
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
    private BigInteger fetchResults5(Response<FriendsResponseModel> response) {
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
