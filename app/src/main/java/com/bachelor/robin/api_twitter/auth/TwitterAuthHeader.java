package com.bachelor.robin.api_twitter.auth;

/**
 * Created by rorod on 14/03/2016.
 */

import android.util.Base64;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

public class TwitterAuthHeader {

    //Declaration ours token
    private static String oauthConsumerKey = "DoZ2bSvA5pAniS8kDxJJ8KGsu";
    private static String oauthConsumerSecret = "fDQon1kUoffYBzls52iOGeeSUKY6USBcSN0CMbLjCk4CGc7djs";
    private static String oauthToken = "706818055054233600-axPLau20pgEjl752BmJRsVx0Fzaa6ON";
    private static String oauthSecretToken = "TLQ5uK4KK2ZTWnpMA1I8lA6m5IyuoPdCiJWBiB16wNKpz";
    private static String oauthVersion = "1.0";
    private static String oauthSignatureMethod = "HMAC-SHA1";
    private String oauthSignature = "";
    private String resourceUrl = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    private String updateUrl = "https://api.twitter.com/1.1/statuses/home_timeline.json";

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
        String compositeKey = compositeKeyTemp.concat(uriEscape(oauthConsumerSecret)).concat("&").concat(uriEscape(oauthSecretToken));

        /*SecretKey secretKey = null;
        byte[] keyBytes = compositeKey.getBytes();
        secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(secretKey);

        byte[] text = baseString.getBytes();

        oauthSignature = new String(Base64.encode(mac.doFinal(text), Base64.DEFAULT)).trim();*/


        Mac m = Mac.getInstance("HmacSHA1");
        SecretKeySpec signinKey = new SecretKeySpec(compositeKey.getBytes("UTF-8"), "HmacSHA1");
        m.init(signinKey);
        byte[] signature = m.doFinal(baseString.getBytes("UTF-8"));
        oauthSignature = new String(Base64.encodeToString(signature, Base64.DEFAULT));

        // Encoding variable
        String finalTimestamp = uriEscape(oauthTimeStamp);
        String finalNonce = uriEscape(oauthNonce);
        String finalSignatureMethod = uriEscape(oauthSignatureMethod);
        String finalSignature = uriEscape(oauthSignature);
        String finalToken = uriEscape(oauthToken);
        String finalVersion = uriEscape(oauthVersion);
        String finalConsumerKey = uriEscape(oauthConsumerKey);

        // Creation headerFormat
        String headerFormat = "OAuth oauth_nonce=%s, oauth_signature_method=%s, oauth_timestamp=%s, oauth_consumer_key=%s, oauth_token=%s, oauth_signature=%s, oauth_version=%s";

        String tempHeaderFormat = String.format(headerFormat,
                finalNonce,
                finalSignatureMethod,
                finalTimestamp,
                finalConsumerKey,
                finalToken,
                finalSignature,
                finalVersion);
        StringBuilder inputRequest = null;
        BufferedReader in = null;
        try {
            HttpsURLConnection request = (HttpsURLConnection)new URL(this.resourceUrl).openConnection();
            request.setDoInput(true);
            request.setDoOutput(true);
            request.setRequestProperty("Authorization", tempHeaderFormat);
            request.setRequestMethod("GET");
            request.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
            request.addRequestProperty("Host", "https://apit.twitter.com");


            /*System.out.println(request.getResponseCode());
            System.out.println(request.getResponseMessage());*/
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
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputRequest;
    }

    public StringBuilder TwitterRefresh() {

        return null;
    }
}