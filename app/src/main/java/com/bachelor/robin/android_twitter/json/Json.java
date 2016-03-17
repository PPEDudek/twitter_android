package com.bachelor.robin.android_twitter.json;

import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bachelor.robin.android_twitter.tweet.Tweet;

public class Json {
    static JSONArray jobj = null;
    static String json = "";

    private ArrayList<Tweet> tweets = new ArrayList<>();


    public JSONArray ReadJson(String response) throws IOException, JSONException {
        json = response;
        ParseJson();
        return jobj;
    }


    public void ParseJson () throws JSONException {
        String tempDate = "";
        String tempText = "";
        String tempName = "";
        String tempScreen_name = "";
        String tempAvatar = "";

		/*
		 * 	Parsing of Json
		 */
        JSONArray jsonarray = new JSONArray(json);

        for(int i = 0; i < jsonarray.length(); i++) {

            //Récupération premier tweet
            JSONObject tweetObj = jsonarray.getJSONObject(i);
            tempDate = tweetObj.getString("created_at");
            tempText = tweetObj.getString("text");


            // Récupération premier tweet -> user
            JSONObject userObj = tweetObj.getJSONObject("user");
            tempName = userObj.getString("name");
            tempScreen_name = userObj.getString("screen_name");
            tempAvatar = userObj.getString("profile_image_url");

            Tweet tweet = new Tweet(tempDate, tempText, tempName, tempScreen_name, tempAvatar);
            this.tweets.add(tweet);
        }
    }


    /*
     * GETTERS & SETTERS
     *
     */
    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }
}
