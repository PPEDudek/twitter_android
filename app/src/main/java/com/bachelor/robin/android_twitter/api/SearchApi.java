package com.bachelor.robin.android_twitter.api;

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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by rorod on 18/03/2016.
 */
public class SearchApi extends TwitterApi{

    private String searchUrl = "https://api.twitter.com/1.1/search/tweets.json";

    public String searchTweet(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        JSONObject jsonresponse = new JSONObject();

        String oauthNonce = createNonce();

        String oauthTimeStamp = createTimestamp();
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

        oauthSignature = createSignature(baseString);

        String tempHeaderFormat = createHeader(oauthTimeStamp, oauthNonce);

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
                BasicHttpEntityEnclosingRequest request2 = new BasicHttpEntityEnclosingRequest("GET", "/1.1/search/tweets.json" + "?q=" + uriEscape(s));
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
            JSONArray jarray = statuses.getJSONArray("statuses");
            searchRequest = jarray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return searchRequest;
    }
}
