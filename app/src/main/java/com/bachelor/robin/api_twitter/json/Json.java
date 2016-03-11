package com.bachelor.robin.api_twitter.json;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bachelor.robin.api_twitter.tweet.Tweet;

public class Json {

    static JSONArray jobj = null;
    static String json = "";

    private ArrayList<Tweet> tweets = new ArrayList<>();

    public Json() {
        try {
            jobj = ReadJson();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            ParseJson();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public JSONArray ReadJson() throws IOException, JSONException {
        // Read Json file for test

        BufferedReader reader = new BufferedReader(new FileReader("C:/Users/rorod/Downloads/home.json"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine())!= null) {
                sb.append(line+"\n");
            }
            json = sb.toString();
            try {
                jobj = new JSONArray(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader.close();
        return jobj;
    }

    public void ParseJson() throws JSONException {
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
	 * GETTERS
	 *
	 */




    public ArrayList<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }
}
