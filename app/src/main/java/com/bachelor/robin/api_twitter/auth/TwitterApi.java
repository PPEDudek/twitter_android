package com.bachelor.robin.api_twitter.auth;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

public class TwitterApi {

    //Declaration ours token
    private static String oauthConsumerKey = "DoZ2bSvA5pAniS8kDxJJ8KGsu";
    private static String oauthConsumerSecret = "fDQon1kUoffYBzls52iOGeeSUKY6USBcSN0CMbLjCk4CGc7djs";
    private static String oauthToken = "706818055054233600-axPLau20pgEjl752BmJRsVx0Fzaa6ON";
    private static String oauthSecretToken = "TLQ5uK4KK2ZTWnpMA1I8lA6m5IyuoPdCiJWBiB16wNKpz";
    private static String oauthVersion = "1.0";
    private static String oauthSignatureMethod = "HMAC-SHA1";
    private String oauthSignature = "";
    private String compositeKey = "";
    private String resourceUrl = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    private String searchUrl = "https://api.twitter.com/1.1/search/tweets.json";
    private String finalTimestamp = "";
    private String finalNonce = "";
    private String finalSignatureMethod = "";
    private String finalSignature = "";
    private String finalToken = "";
    private String finalVersion = "";
    private String finalConsumerKey = "";
    private String headerFormat = "";
    private JSONArray searchArray = null;

    public String uriEscape(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch(Exception e){
            return e.getMessage();
        }
    }

    public StringBuilder getTwitterAuth() throws URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, IOException {

        // Creation oauth_nonce
        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        String oauthNonce = uuid_string;

        // Creation oauth_timestamp
        Long timestamp = System.currentTimeMillis();
        timestamp = timestamp/1000;
        String oauthTimeStamp = Long.toString(timestamp);

        // Creation baseFormat
        String baseFormat = "oauth_consumer_key=%s&oauth_nonce=%s&oauth_signature_method=%s&oauth_timestamp=%s&oauth_token=%s&oauth_version=%s";
        String tempBaseFormat = String.format(baseFormat,
                oauthConsumerKey,
                oauthNonce,
                oauthSignatureMethod,
                oauthTimeStamp,
                oauthToken,
                oauthVersion);

        String baseStringTemp = "";
        String baseString = baseStringTemp.concat(uriEscape("GET")).concat("&").concat(uriEscape(resourceUrl)).concat("&").concat(uriEscape((tempBaseFormat)));


        //Creation oauth_signature
        String compositeKeyTemp = "";
        compositeKey = compositeKeyTemp.concat(uriEscape(oauthConsumerSecret)).concat("&").concat(uriEscape(oauthSecretToken));
        Mac m = Mac.getInstance("HmacSHA1");
        SecretKeySpec signinKey = new SecretKeySpec(compositeKey.getBytes("UTF-8"), "HmacSHA1");
        m.init(signinKey);
        byte[] signature = m.doFinal(baseString.getBytes("UTF-8"));
        oauthSignature = new String(Base64.encode(signature, Base64.DEFAULT)).trim();

        // Encoding variable
        finalTimestamp = uriEscape(oauthTimeStamp);
        finalNonce = uriEscape(oauthNonce);
        finalSignatureMethod = uriEscape(oauthSignatureMethod);
        finalSignature = uriEscape(oauthSignature);
        finalToken = uriEscape(oauthToken);
        finalVersion = uriEscape(oauthVersion);
        finalConsumerKey = uriEscape(oauthConsumerKey);

        // Creation headerFormat
        headerFormat = "OAuth oauth_nonce=\"%s\", oauth_signature_method=\"%s\", oauth_timestamp=\"%s\", oauth_consumer_key=\"%s\", oauth_token=\"%s\", oauth_signature=\"%s\", oauth_version=\"%s\"";

        String tempHeaderFormat = String.format(headerFormat,
                finalNonce,
                finalSignatureMethod,
                finalTimestamp,
                finalConsumerKey,
                finalToken,
                finalSignature,
                finalVersion);

        StringBuilder inputRequest = null;
        try {
            HttpsURLConnection request = (HttpsURLConnection)new URL(this.resourceUrl).openConnection();
            request.setDoInput(true);
            request.setRequestProperty("Authorization", tempHeaderFormat);
            request.setRequestMethod("GET");
            request.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
            request.addRequestProperty("Content-type", "charset=UTF-8");
            request.addRequestProperty("Host", "api.twitter.com");
            request.setDoOutput(true);

            //DEBUG
            System.out.println(tempHeaderFormat);
            System.out.println("Twitter request :");
            System.out.println(request.getResponseCode());
            System.out.println(request.getResponseMessage());
            System.out.println(request.getPermission());

            int status = request.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    inputRequest = sb;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputRequest;
    }

    public StringBuilder searchTweet(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {

        // Creation oauth_nonce
        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        String oauthNonce = uuid_string;

        // Creation oauth_timestamp
        Long timestamp = System.currentTimeMillis();
        timestamp = timestamp/1000;
        String oauthTimeStamp = Long.toString(timestamp);

        // Creation baseFormat
        String baseFormat = "oauth_consumer_key=%s&oauth_nonce=%s&oauth_signature_method=%s&oauth_timestamp=%s&oauth_token=%s&oauth_version=%s&q=%s";
        String tempBaseFormat = String.format(baseFormat,
                oauthConsumerKey,
                oauthNonce,
                oauthSignatureMethod,
                oauthTimeStamp,
                oauthToken,
                oauthVersion,
                s);

        String baseStringTemp = "";
        String baseString = baseStringTemp.concat(uriEscape("GET")).concat("&").concat(uriEscape(this.searchUrl)).concat("&").concat(uriEscape((tempBaseFormat)));

        //Creation oauth_signature
        String compositeKeyTemp = "";
        this.compositeKey = compositeKeyTemp.concat(uriEscape(oauthConsumerSecret)).concat("&").concat(uriEscape(oauthSecretToken));
        Mac m = Mac.getInstance("HmacSHA1");
        SecretKeySpec signinKey = new SecretKeySpec(compositeKey.getBytes("UTF-8"), "HmacSHA1");
        m.init(signinKey);
        byte[] signature = m.doFinal(baseString.getBytes("UTF-8"));
        oauthSignature = new String(Base64.encode(signature, Base64.DEFAULT)).trim();

        // Encoding variable
        finalTimestamp = uriEscape(oauthTimeStamp);
        finalNonce = uriEscape(oauthNonce);
        finalSignatureMethod = uriEscape(oauthSignatureMethod);
        finalSignature = uriEscape(oauthSignature);
        finalToken = uriEscape(oauthToken);
        finalVersion = uriEscape(oauthVersion);
        finalConsumerKey = uriEscape(oauthConsumerKey);

        // Creation headerFormat
        headerFormat = "OAuth oauth_nonce=%s, oauth_signature_method=%s, oauth_timestamp=%s, oauth_consumer_key=%s, oauth_token=%s, oauth_signature=%s, oauth_version=%s";

        String tempHeaderFormat = String.format(headerFormat,
                finalNonce,
                finalSignatureMethod,
                finalTimestamp,
                finalConsumerKey,
                finalToken,
                finalSignature,
                finalVersion);

        StringBuilder searchRequest = null;
        try {
            HttpsURLConnection request = (HttpsURLConnection)new URL(this.searchUrl + "?q=" + s).openConnection();
            request.setDoInput(true);
            request.setDoOutput(true);
            request.setRequestProperty("Authorization", tempHeaderFormat);
            request.setRequestMethod("GET");
            request.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
            request.addRequestProperty("Content-type", "charset=UTF-8");
            request.addRequestProperty("Host", "api.twitter.com");


            //Debug
            System.out.println(tempHeaderFormat);
            System.out.println("Search auth :");
            System.out.println(request.getResponseCode());
            System.out.println(request.getResponseMessage());

            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
            JSONObject jo = new JSONObject(buffer.toString());
            this.searchArray = jo.getJSONArray("statuses");
            searchRequest = new StringBuilder();
            searchRequest.append(searchArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return searchRequest;
    }
}	