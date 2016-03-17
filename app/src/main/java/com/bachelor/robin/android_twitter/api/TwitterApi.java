package com.bachelor.robin.android_twitter.api;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
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
    private static String oauthConsumerKey = "rniH8tfTgS9iLOScQ4DGU3lRl";
    private static String oauthConsumerSecret = "0jWE9FVCkT0o2xwYmY27X4YE2C2fC4UEyNW4bUeBLGqZAFBIcD";
    private static String oauthToken = "1515743089-iqYfFgptRfSCbsQsTmY9knb8Ox2gVYtZ4YsImgH";
    private static String oauthSecretToken = "PB5e1a9Gq0V2Oe3dMjs6X3ny12BiyiXtAc3wJESTXwn7x";
    private static String oauthVersion = "1.0";
    private static String oauthSignatureMethod = "HMAC-SHA1";
    private String oauthSignature = "";
    private String compositeKey = "";
    private String resourceUrl = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    private String searchUrl = "https://api.twitter.com/1.1/search/tweets.json";
    private String userUrl = "https://api.twitter.com/1.1/account/verify_credentials.json";
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

    public String getTwitterAuth() throws URISyntaxException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        JSONObject jsonresponse = new JSONObject();
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

        //Notre code pour la connection à l'api pour l'appli java ne passe pas sur Android du coup
        // on prend le code copier coller des autres groupes pour tester
        BasicHttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpCore/1.1");
        HttpProtocolParams.setUseExpectContinue(params, false);

        HttpProcessor httpProc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                // Required protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
                // Recommended protocol interceptors
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});

        HttpRequestExecutor httpExecutor = new HttpRequestExecutor();
        HttpContext context = new BasicHttpContext(null);
        HttpHost host = new HttpHost("api.twitter.com",443);
        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);
        try {
            try {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, null, null);
                SSLSocketFactory ssf = sslcontext.getSocketFactory();
                Socket socket = ssf.createSocket();
                socket.connect(
                        new InetSocketAddress(host.getHostName(), host.getPort()), 0);
                conn.bind(socket, params);

                // the following line adds 3 params to the request just as the parameter string did above. They must match up or the request will fail.
                BasicHttpEntityEnclosingRequest request2 = new BasicHttpEntityEnclosingRequest("GET", "/1.1/statuses/home_timeline.json" );
                request2.setParams(params);
                request2.addHeader("Authorization", tempHeaderFormat); // always add the Authorization header
                httpExecutor.preProcess(request2, httpProc, context);
                HttpResponse response2 = httpExecutor.execute(request2, conn, context);
                response2.setParams(params);
                httpExecutor.postProcess(response2, httpProc, context);

                if(response2.getStatusLine().toString().indexOf("500") != -1)
                {
                    jsonresponse.put("response_status", "error");
                    jsonresponse.put("message", "Twitter auth error.");
                }
                else
                {
                    System.out.println("CoucouLol");
                    // if successful, the response should be a JSONObject of tweets
                    JSONArray jo = new JSONArray(EntityUtils.toString(response2.getEntity()));
                    jsonresponse.put("twitter_jo", jo);
                    /*if(jo.has("errors"))
                    {
                        System.out.println("Erreur");
                        jsonresponse.put("response_status", "error");
                        String message_from_twitter = jo.getJSONArray("errors").getJSONObject(0).getString("message");
                        if(message_from_twitter.equals("Invalid or expired token") || message_from_twitter.equals("Could not authenticate you"))
                            jsonresponse.put("message", "Twitter auth error.");
                        else
                            jsonresponse.put("message", jo.getJSONArray("errors").getJSONObject(0).getString("message"));
                    }
                    else
                    {
                        System.out.println("Success");
                        jsonresponse.put("twitter_jo", jo); // this is the full result object from Twitter
                    }*/

                    conn.close();
                }
            }
            catch(HttpException he)
            {
                System.out.println(he.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "searchTweets HttpException message=" + he.getMessage());
            }
            catch(NoSuchAlgorithmException nsae)
            {
                System.out.println(nsae.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "searchTweets NoSuchAlgorithmException message=" + nsae.getMessage());
            }
            catch(KeyManagementException kme)
            {
                System.out.println(kme.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "searchTweets KeyManagementException message=" + kme.getMessage());
            }
            finally {
                conn.close();
            }
        }
        catch(JSONException jsone)
        {
            System.out.println("merdeJsonE");
        }
        catch(IOException ioe) {
            System.out.println("remerdeIOE");
        }

        String inputRequest = null;
        try {
            inputRequest = jsonresponse.get("twitter_jo").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*try {
            HttpsURLConnection request = (HttpsURLConnection)new URL(this.resourceUrl).openConnection();
            request.setDoInput(true);
            request.setDoOutput(true);
            request.setRequestProperty("Authorization", tempHeaderFormat);
            request.setRequestMethod("GET");
            request.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
            request.addRequestProperty("Content-type", "charset=UTF-8");
            request.addRequestProperty("Host", "api.twitter.com");

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
                    inputRequest = sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        System.out.println(inputRequest);
        return inputRequest;
    }

    public String searchTweet(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        JSONObject jsonresponse = new JSONObject();
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

        BasicHttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpCore/1.1");
        HttpProtocolParams.setUseExpectContinue(params, false);

        HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                // Required protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
                // Recommended protocol interceptors
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});

        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
        HttpContext context = new BasicHttpContext(null);
        HttpHost host = new HttpHost("api.twitter.com",443);
        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

        try {
            try {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, null, null);
                SSLSocketFactory ssf = sslcontext.getSocketFactory();
                Socket socket = ssf.createSocket();
                socket.connect(
                        new InetSocketAddress(host.getHostName(), host.getPort()), 0);
                conn.bind(socket, params);

                // the following line adds 3 params to the request just as the parameter string did above. They must match up or the request will fail.
                BasicHttpEntityEnclosingRequest request2 = new BasicHttpEntityEnclosingRequest("GET", "/1.1/search/tweets.json" + "?lang=en&result_type=mixed&q=" + uriEscape(s));
                request2.setParams(params);
                request2.addHeader("Authorization", tempHeaderFormat); // always add the Authorization header
                httpexecutor.preProcess(request2, httpproc, context);
                HttpResponse response2 = httpexecutor.execute(request2, conn, context);
                response2.setParams(params);
                httpexecutor.postProcess(response2, httpproc, context);

                if(response2.getStatusLine().toString().indexOf("500") != -1)
                {
                    jsonresponse.put("response_status", "error");
                    jsonresponse.put("message", "Twitter auth error.");
                }
                else
                {
                    // if successful, the response should be a JSONObject of tweets
                    JSONObject jo = new JSONObject(EntityUtils.toString(response2.getEntity()));

                    jsonresponse.put("twitter_jo", jo); // this is the full result object from Twitter


                    conn.close();
                }
            }
            catch(HttpException he)
            {
                System.out.println(he.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "searchTweets HttpException message=" + he.getMessage());
            }
            catch(NoSuchAlgorithmException nsae)
            {
                System.out.println(nsae.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "searchTweets NoSuchAlgorithmException message=" + nsae.getMessage());
            }
            catch(KeyManagementException kme)
            {
                System.out.println(kme.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "searchTweets KeyManagementException message=" + kme.getMessage());
            }
            finally {
                conn.close();
            }
        }
        catch(JSONException jsone)
        {

        }
        catch(IOException ioe)
        {

        }
        String searchRequest = null;
        try {
            JSONObject statuses = jsonresponse.getJSONObject("twitter_jo");
            searchRequest = statuses.get("statuses").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*try {
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
        }*/
        return searchRequest;
    }

    public JSONObject userInfo() throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        JSONObject jsonresponse = new JSONObject();
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
        String baseString = baseStringTemp.concat(uriEscape("GET")).concat("&").concat(uriEscape(userUrl)).concat("&").concat(uriEscape((tempBaseFormat)));


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
        headerFormat = "OAuth oauth_nonce=%s, oauth_signature_method=%s, oauth_timestamp=%s, oauth_consumer_key=%s, oauth_token=%s, oauth_signature=%s, oauth_version=%s";

        String tempHeaderFormat = String.format(headerFormat,
                finalNonce,
                finalSignatureMethod,
                finalTimestamp,
                finalConsumerKey,
                finalToken,
                finalSignature,
                finalVersion);

        StringBuilder inputRequest = null;
        /*try {
            HttpsURLConnection request = (HttpsURLConnection)new URL(this.userUrl).openConnection();
            request.setDoInput(true);
            request.setDoOutput(true);
            request.setRequestProperty("Authorization", tempHeaderFormat);
            request.setRequestMethod("GET");
            request.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
            request.addRequestProperty("Content-type", "charset=UTF-8");
            request.addRequestProperty("Host", "https://apit.twitter.com");

            //DEBUG
            System.out.println();
            System.out.println("Twitter user request :");
            System.out.println(request.getResponseCode());
            System.out.println(request.getResponseMessage());


            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));

            String line;
            sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            jo = new JSONObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        BasicHttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpCore/1.1");
        HttpProtocolParams.setUseExpectContinue(params, false);

        HttpProcessor httpproc = new ImmutableHttpProcessor(new HttpRequestInterceptor[] {
                // Required protocol interceptors
                new RequestContent(),
                new RequestTargetHost(),
                // Recommended protocol interceptors
                new RequestConnControl(),
                new RequestUserAgent(),
                new RequestExpectContinue()});

        HttpRequestExecutor httpexecutor = new HttpRequestExecutor();
        HttpContext context = new BasicHttpContext(null);
        HttpHost host = new HttpHost("api.twitter.com",443);
        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();

        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

        try {
            try {
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, null, null);
                SSLSocketFactory ssf = sslcontext.getSocketFactory();
                Socket socket = ssf.createSocket();
                socket.connect(
                        new InetSocketAddress(host.getHostName(), host.getPort()), 0);
                conn.bind(socket, params);

                // the following line adds 3 params to the request just as the parameter string did above. They must match up or the request will fail.
                BasicHttpEntityEnclosingRequest request2 = new BasicHttpEntityEnclosingRequest("GET", "/1.1/account/verify_credentials.json");
                request2.setParams(params);
                request2.addHeader("Authorization", tempHeaderFormat); // always add the Authorization header
                httpexecutor.preProcess(request2, httpproc, context);
                HttpResponse response2 = httpexecutor.execute(request2, conn, context);
                response2.setParams(params);
                httpexecutor.postProcess(response2, httpproc, context);

                if(response2.getStatusLine().toString().indexOf("500") != -1)
                {
                    jsonresponse.put("response_status", "error");
                    jsonresponse.put("message", "Twitter auth error.");
                }
                else
                {
                    // if successful, the response should be a JSONObject of tweets
                    JSONObject jo = new JSONObject(EntityUtils.toString(response2.getEntity()));
                    /*if(jo.has("errors"))
                    {
                        jsonresponse.put("response_status", "error");
                        String message_from_twitter = jo.getJSONArray("errors").getJSONObject(0).getString("message");
                        if(message_from_twitter.equals("Invalid or expired token") || message_from_twitter.equals("Could not authenticate you"))
                            jsonresponse.put("message", "Twitter auth error.");
                        else
                            jsonresponse.put("message", jo.getJSONArray("errors").getJSONObject(0).getString("message"));
                    }
                    else
                    {*/
                        jsonresponse.put("twitter_jo", jo); // this is the full result object from Twitter
                    //}

                    conn.close();
                }
            }
            catch(HttpException he)
            {
                System.out.println(he.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "verifyCredentials HttpException message=" + he.getMessage());
            }
            catch(NoSuchAlgorithmException nsae)
            {
                System.out.println(nsae.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "verifyCredentials NoSuchAlgorithmException message=" + nsae.getMessage());
            }
            catch(KeyManagementException kme)
            {
                System.out.println(kme.getMessage());
                jsonresponse.put("response_status", "error");
                jsonresponse.put("message", "verifyCredentials KeyManagementException message=" + kme.getMessage());
            }
            finally {
                conn.close();
            }
        }
        catch(JSONException jsone)
        {

        }
        catch(IOException ioe)
        {

        }
        try {
            jsonresponse = (JSONObject) jsonresponse.get("twitter_jo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonresponse;
    }
}	