package com.bachelor.robin.android_twitter.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bachelor.robin.android_twitter.R;
import com.bachelor.robin.android_twitter.modele.TweetModele;
import com.bachelor.robin.android_twitter.api.TwitterApi;
import com.bachelor.robin.android_twitter.json.Json;
import com.bachelor.robin.android_twitter.tweet.Tweet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lv;
    private ImageView avatar;
    private TextView pseudo;
    private TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.tweetList);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().
                permitAll().build();
        StrictMode.setThreadPolicy(policy);
        TwitterApi tah = new TwitterApi();
        Json json = new Json();

        userAccount();
        try{
            json.ReadJson(tah.getTwitterAuth());
        }catch(Exception e)
        {

        }
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayList<Tweet> tweets = new ArrayList<>();
        tweets = json.getTweets();
        /*ArrayList<String> tweetList = new ArrayList<>();
        for(int i = 0; i < tweets.size(); i++) {
            tweetList.add(tweets.get(i).getText());
        }*/

        /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.row_tweet,
                tweetList );*/
        TweetModele tweetAdapter = new TweetModele(MainActivity.this, tweets);
        lv.setAdapter(tweetAdapter);
    }

    public void userAccount() {
        TwitterApi ta = new TwitterApi();
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj = ta.userInfo();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
             name = (TextView) findViewById(R.id.name);
             pseudo = (TextView) findViewById(R.id.pseudo);
             avatar = (ImageView) findViewById(R.id.avatar);
             name.setText(jsonObj.getString("name"));
            String screen_name = "@"+jsonObj.getString("screen_name");
            pseudo.setText(screen_name);
            //avatar.setImageDrawable(new ColorDrawable(Color.BLUE));

                try {

                    URL myFileUrl = new URL (jsonObj.getString("profile_image_url"));
                    HttpURLConnection conn =
                            (HttpURLConnection) myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    avatar.setImageBitmap(BitmapFactory.decodeStream(is));

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
