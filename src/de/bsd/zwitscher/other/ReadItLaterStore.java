package de.bsd.zwitscher.other;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import twitter4j.Annotations;
import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

/**
 * Post a message to ReadItLater
 * @author Heiko W. Rupp
 */
public class ReadItLaterStore {

    private String user;
    private String password;

    public ReadItLaterStore(String user, String password) {
        this.user = user;
        this.password = password;
    }

    private static String baseUrl ="https://readitlaterlist.com/v2/send?";

    public String store(Status tweet,boolean isTwitter,String articleUrl) {

        // https://readitlaterlist.com/v2/add?username=name&password=123&apikey=yourapikey&url=http://google.com&title=Google

        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append("username=").append(user).append("&");
        sb.append("password=").append(password).append("&");
        sb.append("apikey=").append(ReaditLaterToken.token);
        String targetUrl = sb.toString();



        StringBuilder json = new StringBuilder();
        json.append("new={");
        json.append("  \"0\":{ ");
        json.append("      \"url\":\"").append(articleUrl).append("\",\n");
        json.append("      \"title\":\"").append(tweet.getText()).append("\"");
        if (isTwitter) {
            json.append(",\n");
            json.append("      \"ref_id\":\"").append(tweet.getId()).append("\"");
        }
        json.append("} } ");

        int code;
        String result="-no result-";

        try {
            URL url = new URL(targetUrl);
            System.out.println("Writing to " + targetUrl);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.connect();
            OutputStream out = conn.getOutputStream();
            out.write(json.toString().getBytes());
            out.flush();
            code = conn.getResponseCode();
            Map headers = conn.getHeaderFields();
            System.out.println(headers);

            InputStream inputStream;
            if (code != HttpURLConnection.HTTP_OK)
                inputStream = conn.getErrorStream();
            else
                inputStream = conn.getInputStream();
            if (inputStream!=null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    builder.append(line);
                }
                br.close();
                result = builder.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();  // TODO: Customise this generated block
            result = "Failed - are your credentials ok?";
        }

        return result;
    }

}
