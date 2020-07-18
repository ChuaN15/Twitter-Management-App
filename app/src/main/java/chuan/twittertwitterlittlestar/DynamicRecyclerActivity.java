package chuan.twittertwitterlittlestar;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chuan.twittertwitterlittlestar.client.MyTwitterApiClient;
import chuan.twittertwitterlittlestar.model.FavoritesResponseModel;
import chuan.twittertwitterlittlestar.model.FriendsResponseModel;
import chuan.twittertwitterlittlestar.model.TwitterFriends;
import chuan.twittertwitterlittlestar.model.TwitterUser;
import retrofit2.Call;
import retrofit2.Response;

public class DynamicRecyclerActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private Context mContext;
    public BigInteger cursor;
    TwitterSession twitterSession;
    TwitterAuthToken twitterAuthToken;

    private TextView num_follower_view;
    private TextView num_following_view;
    long loggedUserTwitterId;
    ArrayList<Long> mutual_id = new ArrayList<>();
    ListView mainListView;
    List<TwitterFriends> twitterFriends;
    List<FavoritesResponseModel> bestFriends;

    ArrayList<String> friendsList = new ArrayList<String>();

    ArrayList<String> imgid = new ArrayList<String>();
    ArrayList<String> imgidbff = new ArrayList<String>();

    ArrayList<String> user_id = new ArrayList<String>();
    ArrayList<String> user_idbff = new ArrayList<String>();
    Button hello;
    Button close;
    public Dialog MyDialog;

    public CustomListAdapter adapter;
    private FollowingFragment.OnFragmentInteractionListener mListener;
    private ImageView dialog_img;
    ArrayList<TwitterFriends> object = new ArrayList<TwitterFriends>();
    private ArrayList<String> bff_names = new ArrayList<String>();
    private ImageView mImageView;
    private String image;
    private TextView mStorking;
    private TextView mStorker;
    private long num_follower;
    private long num_following;
    private ProgressBar loader;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_recycler);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
        loader =  findViewById( R.id.progress_loader);
        loader.setVisibility(View.VISIBLE);
        mContext = DynamicRecyclerActivity.this;
        mRecyclerView = findViewById(R.id.recyclerView);
        mImageView = findViewById(R.id.stork_image);
        twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        twitterAuthToken = twitterSession.getAuthToken();
        setCursor(BigInteger.valueOf(-1));
        //loadTwitterFriends();
        mutual_id = (ArrayList<Long>) getIntent().getSerializableExtra("friend");
        setCursor(BigInteger.valueOf(-1));
        long id = getIntent().getLongExtra("id", 0);
        image = getIntent().getStringExtra("image");
        num_follower = getIntent().getLongExtra("follower_num",0);
        num_following = getIntent().getLongExtra("following_num",0);
        Log.e("ID", String.valueOf(id));
        loadMutualFriends(id);
        loadBestFriends(id);
    }



    public void adapter() {

        mStorking = findViewById(R.id.following_number_stork);
        Log.e("haiz", String.valueOf(mStorking));
        mStorker = findViewById(R.id.follower_number_stork);
        mStorking.setText(String.valueOf(num_following));
        mStorker.setText(String.valueOf(num_follower));
        Picasso.with(this).load(image).fit().into(mImageView);
            RecyclerDataAdapter recyclerDataAdapter = new RecyclerDataAdapter(friendsList);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.setAdapter(recyclerDataAdapter);
            mRecyclerView.setHasFixedSize(true);
    }

    public BigInteger getCursor(){
        return this.cursor;
    }
    public void setCursor(BigInteger cursorValue){
        this.cursor = cursorValue;
        Log.e("setCursor","set" + String.valueOf(this.cursor));
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

    private List<FavoritesResponseModel> fetchResults4(Response<ArrayList<FavoritesResponseModel>> response) {
        List<FavoritesResponseModel> responseModel = response.body();
        if ((responseModel != null ? responseModel.size() : 0) != 0) {
            Log.e("lol", String.valueOf(responseModel.get(0).getResults().getId()));
            Log.e("lol", responseModel.get(0).getResults().getScreenName());
            return responseModel;
        }
        else{
            Log.e("1","dsa");
            //Server returning null objects
            return Collections.emptyList();
        }
    }


    private void loadBestFriends(final long id) {
        Log.e("CursorIn",String.valueOf(getCursor()));
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService4().list(id, 200).enqueue(new retrofit2.Callback<ArrayList<FavoritesResponseModel>>() {
            @Override
            public void onResponse(Call<ArrayList<FavoritesResponseModel>> call, Response<ArrayList<FavoritesResponseModel>> response) {

                Log.e("onResponseFriend", "Is response success" + response.isSuccessful() +response.message());
                bestFriends = fetchResults4(response);




                for (int k=0;k<bestFriends.size();k++){
                    if (!bff_names.contains(bestFriends.get(k).getResults().getName())) {
                        bff_names.add(bestFriends.get(k).getResults().getName());
                    }

                    Log.e("Twitter Friends Friend","Id:"+bestFriends.get(k).getResults().getId()+" Name:"+ bestFriends.get(k).getResults().getScreenName()+ " pickUrl:"+bestFriends.get(k).getResults().getProfilePictureUrl());
                }

                Log.e("onResponse", "mutual id:" + mutual_id.size());

            }

            @Override
            public void onFailure(Call<ArrayList<FavoritesResponseModel>> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });

    }

    private void loadMutualFriends(final long id) {
        Log.e("CursorIn",String.valueOf(getCursor()));
        MyTwitterApiClient myTwitterApiClient = new MyTwitterApiClient(twitterSession);
        myTwitterApiClient.getCustomTwitterService().list(id,getCursor(),200).enqueue(new retrofit2.Callback<FriendsResponseModel>() {
            @Override
            public void onResponse(Call<FriendsResponseModel> call, Response<FriendsResponseModel> response) {

                Log.e("onResponselol", "Is response success" + response.isSuccessful());
                twitterFriends = fetchResults(response);


                setCursor(fetchResults3(response));
                Log.e("next cursor", "Cursor:" + getCursor());
                for (int k=0;k<twitterFriends.size();k++){
                    //Log.e("mutualll",String.valueOf(mutual_id.get(31)));
                    //Log.e("mutualll",String.valueOf(twitterFriends.get(k).getId()));
                    //Log.e("mutual id", String.valueOf(mutual_id.contains(twitterFriends.get(k).getId())));
                    if (mutual_id.contains(twitterFriends.get(k).getId())) {
                        object.add(twitterFriends.get(k));
                        Log.e("added","asd");
                        friendsList.add(twitterFriends.get(k).getName());
                        imgid.add(twitterFriends.get(k).getProfilePictureUrl());
                        user_id.add(twitterFriends.get(k).getScreenName());
                        Log.e("Twitter Friends SOHAI", "Id:" + twitterFriends.get(k).getId() + " Name:" + twitterFriends.get(k).getName() + " pickUrl:" + twitterFriends.get(k).getProfilePictureUrl());
                    }
                }

                Log.e("onResponse", "twitterfriends:" + twitterFriends.size());
                if (!getCursor().equals(BigInteger.ZERO)) {
                    loadMutualFriends(id);
                }
                else{
                    adapter();
                    loader.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<FriendsResponseModel> call, Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });

    }

    private class RecyclerDataAdapter extends RecyclerView.Adapter<RecyclerDataAdapter.MyViewHolder> {
        ArrayList<String> friendsList = new ArrayList<>();
        RecyclerDataAdapter(ArrayList<String> friendsList) {
            this.friendsList = friendsList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.e("got","asd");
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parent_child_listing, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            //int noOfChildTextViews = holder.linearLayout_childItems.getChildCount();
            //int noOfChildTextViews2 = holder.linearLayout_childItems2.getChildCount();
            int noOfChild = friendsList.size();
            //int noOfChild2 = bff_names.size();
            Log.e("WTF", String.valueOf(bff_names.size()));
            Log.e("child size",String.valueOf(noOfChild));
            /*
            if (noOfChild < noOfChildTextViews) {
                for (int index = noOfChild; index < noOfChildTextViews; index++) {
                    TextView currentTextView = (TextView) holder.linearLayout_childItems.getChildAt(index);
                    currentTextView.setVisibility(View.GONE);
                }
            }
            if (noOfChild2 < noOfChildTextViews2) {
                for (int index = noOfChild2; index < noOfChildTextViews; index++) {
                    TextView currentTextView = (TextView) holder.linearLayout_childItems2.getChildAt(index);
                    currentTextView.setVisibility(View.GONE);
                }
            }*/
            if (holder.intMaxNoOfChild == 0){
                TextView currentTextView = (TextView) holder.linearLayout_childItems.getChildAt(0);
                currentTextView.setText(R.string.nomf);
            }

            for (int textViewIndex = 0; textViewIndex < holder.intMaxNoOfChild; textViewIndex++) {
                TextView currentTextView = (TextView) holder.linearLayout_childItems.getChildAt(textViewIndex);
                currentTextView.setText(friendsList.get(textViewIndex));

            }

            if (holder.intMaxNoOfChild2 == 0){
                TextView currentTextView = (TextView) holder.linearLayout_childItems2.getChildAt(0);
                currentTextView.setText(R.string.nobff);
            }
            for (int textViewIndex = 0; textViewIndex < holder.intMaxNoOfChild2; textViewIndex++) {
                TextView currentTextView = (TextView) holder.linearLayout_childItems2.getChildAt(textViewIndex);
                Log.e("OK", String.valueOf(textViewIndex));
                currentTextView.setText(bff_names.get(textViewIndex));

            }
        }

        @Override
        public int getItemCount() {
            Log.e("sizeeee",String.valueOf(friendsList.size()));
            return 1;
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private Context context;
            private TextView textView_parentName;
            private TextView textView_parentName2;
            private LinearLayout linearLayout_childItems;
            private LinearLayout linearLayout_childItems2;
            private int intMaxNoOfChild;
            private int intMaxNoOfChild2;
            MyViewHolder(View itemView) {
                super(itemView);
                context = itemView.getContext();
                linearLayout_childItems = itemView.findViewById(R.id.ll_child_items);
                linearLayout_childItems.setVisibility(View.GONE);
                linearLayout_childItems2 = itemView.findViewById(R.id.ll_child_items2);
                linearLayout_childItems2.setVisibility(View.GONE);

                intMaxNoOfChild = 10;
                int intMaxSizeTemp = friendsList.size();
                if (intMaxSizeTemp < 10) intMaxNoOfChild = intMaxSizeTemp;
                for (int indexView = 0; indexView < intMaxNoOfChild; indexView++) {
                    TextView textView = new TextView(context);
                    textView.setId(indexView);
                    textView.setPadding(0, 20, 0, 20);
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackground(ContextCompat.getDrawable(context, R.drawable.background_sub_module_text));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    textView.setOnClickListener(this);
                    linearLayout_childItems.addView(textView, layoutParams);
                }

                if (intMaxNoOfChild == 0){
                    TextView textView = new TextView(context);
                    textView.setId(0);
                    textView.setPadding(0, 20, 0, 20);
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackground(ContextCompat.getDrawable(context, R.drawable.background_sub_module_text));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    textView.setOnClickListener(this);
                    linearLayout_childItems.addView(textView, layoutParams);
                }
                intMaxNoOfChild2 = 10;
                int intMaxSizeTemp2 = bff_names.size();
                if (intMaxSizeTemp2 < 10) intMaxNoOfChild2 = intMaxSizeTemp2;
                for (int indexView = 0; indexView < intMaxNoOfChild2; indexView++) {
                    TextView textView = new TextView(context);
                    textView.setId(indexView);
                    textView.setPadding(0, 20, 0, 20);
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackground(ContextCompat.getDrawable(context, R.drawable.background_sub_module_text));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    textView.setOnClickListener(this);
                    linearLayout_childItems2.addView(textView, layoutParams);
                }
                if (intMaxNoOfChild2 == 0){
                    TextView textView = new TextView(context);
                    textView.setId(0);
                    textView.setPadding(0, 20, 0, 20);
                    textView.setGravity(Gravity.CENTER);
                    textView.setBackground(ContextCompat.getDrawable(context, R.drawable.background_sub_module_text));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    textView.setOnClickListener(this);
                    linearLayout_childItems2.addView(textView, layoutParams);
                }

                textView_parentName = itemView.findViewById(R.id.tv_parentName);
                textView_parentName2 = itemView.findViewById(R.id.tv_parentName2);
                textView_parentName.setOnClickListener(this);
                textView_parentName2.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.tv_parentName) {
                    if (linearLayout_childItems.getVisibility() == View.VISIBLE) {
                        linearLayout_childItems.setVisibility(View.GONE);
                    } else {
                        linearLayout_childItems.setVisibility(View.VISIBLE);
                    }
                }
                else if (view.getId() == R.id.tv_parentName2){
                    if (linearLayout_childItems2.getVisibility() == View.VISIBLE) {
                        linearLayout_childItems2.setVisibility(View.GONE);
                    } else {
                        linearLayout_childItems2.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    TextView textViewClicked = (TextView) view;
                    Toast.makeText(context, "" + textViewClicked.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
