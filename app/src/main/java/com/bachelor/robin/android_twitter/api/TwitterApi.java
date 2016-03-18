package com.bachelor.robin.android_twitter.api;

import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TwitterApi {

    //Declaration ours token
    protected static String oauthConsumerKey = "rniH8tfTgS9iLOScQ4DGU3lRl";
    protected static String oauthConsumerSecret = "0jWE9FVCkT0o2xwYmY27X4YE2C2fC4UEyNW4bUeBLGqZAFBIcD";
    protected static String oauthToken = "1515743089-iqYfFgptRfSCbsQsTmY9knb8Ox2gVYtZ4YsImgH";
    protected static String oauthSecretToken = "PB5e1a9Gq0V2Oe3dMjs6X3ny12BiyiXtAc3wJESTXwn7x";
    protected static String oauthVersion = "1.0";
    protected static String oauthSignatureMethod = "HMAC-SHA1";
    protected String oauthSignature = "";
    protected String compositeKey = "";
    protected String finalTimestamp = "";
    protected String finalNonce = "";
    protected String finalSignatureMethod = "";
    protected String finalSignature = "";
    protected String finalToken = "";
    protected String finalVersion = "";
    protected String finalConsumerKey = "";
    protected String headerFormat = "";
    protected JSONArray searchArray = null;

    public String uriEscape(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch(Exception e){
            return e.getMessage();
        }
    }

    public String createNonce()
    {
        // Creation oauth_nonce
        String uuid_string = UUID.randomUUID().toString();
        uuid_string = uuid_string.replaceAll("-", "");
        return uuid_string;
    }

    public String createTimestamp()
    {
        // Creation oauth_timestamp
        Long timestamp = System.currentTimeMillis();
        timestamp = timestamp/1000;
        return Long.toString(timestamp);
    }

    public String createSignature(String baseString) throws NoSuchAlgorithmException, UnsupportedEncodingException,InvalidKeyException
    {
        //Creation oauth_signature
        String compositeKeyTemp = "";
        compositeKey = compositeKeyTemp.concat(uriEscape(oauthConsumerSecret)).concat("&").concat(uriEscape(oauthSecretToken));
        Mac m = Mac.getInstance("HmacSHA1");
        SecretKeySpec signinKey = new SecretKeySpec(compositeKey.getBytes("UTF-8"), "HmacSHA1");
        m.init(signinKey);
        byte[] signature = m.doFinal(baseString.getBytes("UTF-8"));
        return new String(Base64.encode(signature, Base64.DEFAULT)).trim();
    }

    public String createHeader(String oauthTimeStamp, String oauthNonce)
    {
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

        return String.format(headerFormat,
                finalNonce,
                finalSignatureMethod,
                finalTimestamp,
                finalConsumerKey,
                finalToken,
                finalSignature,
                finalVersion);
    }

}	