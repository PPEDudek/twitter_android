package com.bachelor.robin.api_twitter.main;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bachelor.robin.api_twitter.R;
import com.bachelor.robin.api_twitter.auth.TwitterApi;
import com.bachelor.robin.api_twitter.auth.TwitterAuthHeader;
import com.bachelor.robin.api_twitter.json.Json;
import com.bachelor.robin.api_twitter.tweet.Tweet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lv;
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
        try{
            json.ReadJson(tah.getTwitterAuth());
        }catch(Exception e)
        {

        }
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayList<Tweet> tweets;
        tweets = json.getTweets();
        ArrayList<String> tweetList = new ArrayList<>();
        for(int i = 0; i < tweets.size(); i++) {
            tweetList.add(tweets.get(i).getText());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.row_tweet,
                tweetList );

        lv.setAdapter(arrayAdapter);

    }
}
