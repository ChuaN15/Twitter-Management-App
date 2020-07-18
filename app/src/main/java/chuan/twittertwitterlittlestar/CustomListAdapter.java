package chuan.twittertwitterlittlestar;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import chuan.twittertwitterlittlestar.model.TwitterFriends;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList <String> itemname;
    private final ArrayList <String> imgid;
    private String current_img;
    private final ArrayList <String> user_id;


    public CustomListAdapter(Context context, ArrayList<String> itemname, ArrayList<String> imgid, ArrayList<String> user_id) {
        super(context, R.layout.simplerow, R.id.rowTextView, itemname);
        // TODO Auto-generated constructor stub
        this.user_id = user_id;
        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }

/*
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }
*/
/*
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
*/
    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View rowView=inflater.inflate(R.layout.simplerow, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.rowTextView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.profile_image);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
        txtTitle.setText(itemname.get(position));
        current_img = imgid.get(position);/*
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(current_img.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bmp);*/
        //new DownloadImageTask(imageView)
         //       .execute(current_img);
        Picasso.with(this.context).load(current_img).fit().into(imageView);
        /*
        try {
            Log.e("Image", current_img);
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(current_img).getContent());
            imageView.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //imageView.setImageResource(imgid.get(position));
        extratxt.setText("@"+user_id.get(position));
        return rowView;

    };
}


