package chuan.twittertwitterlittlestar;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
 * {@link NotFollowerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotFollowerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotFollowerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
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

    private OnFragmentInteractionListener mListener;
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

    public NotFollowerFragment() {
        this.bool = false;
        // Required empty public constructor
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
    public static NotFollowerFragment newInstance(String param1, String param2) {
        NotFollowerFragment fragment = new NotFollowerFragment();
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
        hello =  MyDialog.findViewById(R.id.hello);
        close =  MyDialog.findViewById(R.id.close);
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
        View view = inflater.inflate(R.layout.fragment_notfollower, container, false);

        twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        twitterAuthToken = twitterSession.getAuthToken();

        loggedUserTwitterId = twitterSession.getId();
        loader =  view.findViewById( R.id.progress_loader);
        loader.setVisibility(View.VISIBLE);


        mainListView = (ListView) view.findViewById( R.id.mainListView9 );
        listAdapter=new CustomListAdapter(this.getActivity(), notfollowersList, imgid, user_id);
        mainListView.setAdapter(listAdapter );


        loadTwitterFollowers();
        loadTwitterFriends();

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final String item = imgid.get(position);
                MyCustomAlertDialog(item, object.get(position));

            }

        });

        return view;
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


                for (int k = 0; k < twitterFollowers.size(); k++) {
                    object2.add(twitterFollowers.get(k));
                    followersList.add(twitterFollowers.get(k).getName());
                    followers_img.add(twitterFollowers.get(k).getProfilePictureUrl().replace("_normal",""));
                    followers_user.add(twitterFollowers.get(k).getScreenName());
                    followers_id.add(twitterFollowers.get(k).getId());
                    Log.e("Twitter Friends", "Id:" + twitterFollowers.get(k).getId() + " Name:" + twitterFollowers.get(k).getName() + " pickUrl:" + twitterFollowers.get(k).getProfilePictureUrl());
                }
                if (!getCursor().equals(BigInteger.ZERO)) {
                    loadTwitterFollowers();
                }
                else{
                    setBool(true);
                }
            }

            @Override
            public void onFailure(Call<FollowersResponseModel> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }

        });
    }


    private void loadTwitterFriends() {
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().list(loggedUserTwitterId, getCursor(), 200).enqueue(new retrofit2.Callback<FriendsResponseModel>() {
            @Override
            public void onResponse(Call<FriendsResponseModel> call, Response<FriendsResponseModel> response) {
                Collection listOne = followersList;
                Collection listImg = followers_img;
                Collection listUser = followers_user;
                Collection listobj = object2;
                Collection fol_id = followers_id;
                Log.e("onResponse",response.toString());
                twitterFriends = fetchResults(response);

                setCursor(fetchResults3(response));
                Log.e("onResponse","twitterfriends:"+twitterFriends.size());

                for (int k=0;k<twitterFriends.size();k++){
                    object.add(twitterFriends.get(k));
                    notfollowersList.add(twitterFriends.get(k).getName());
                    imgid.add(twitterFriends.get(k).getProfilePictureUrl().replace("_normal",""));
                    user_id.add(twitterFriends.get(k).getScreenName());
                    ids.add(twitterFriends.get(k).getId());
                    Log.e("Twitter Friends","Id:"+twitterFriends.get(k).getId()+" Name:"+twitterFriends.get(k).getName()+" pickUrl:"+twitterFriends.get(k).getProfilePictureUrl());
                }
                notfollowersList.removeAll(listOne);
                imgid.removeAll(listImg);
                user_id.removeAll(listUser);
                object.removeAll(listobj);
                ids.removeAll(fol_id);



                if (!getCursor().equals(BigInteger.ZERO)) {
                    loadTwitterFriends();
                }
                else{
                    if (getBool()) {
                        getActivity().setTitle(String.valueOf(notfollowersList.size()) + " Not Following Back");
                        listAdapter.notifyDataSetChanged();
                    }
                }

                loader.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<FriendsResponseModel> call, Throwable t) {
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



    private List<TwitterFriends> fetchResults(Response<FriendsResponseModel> response) {
        FriendsResponseModel responseModel = response.body();
        if (responseModel != null) {
            return responseModel.getResults();
        }
        else{
            return Collections.emptyList();
        }
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
