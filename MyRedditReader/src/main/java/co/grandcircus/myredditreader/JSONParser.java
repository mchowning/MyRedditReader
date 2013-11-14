package co.grandcircus.myredditreader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Matt on 11/10/13.
 */
public class JSONParser {

    public static String getJSONString(String url) {                                                       // TODO Is this really the best place to implement this?
        String result = "";
        HttpURLConnection connection = null;
        try {
            URL redditUrl = new URL(url);
            connection = (HttpURLConnection) redditUrl.openConnection();
            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(reader);

            String read = br.readLine();

            while (read != null) {
                result += read /*+ "\n"*/;
                read = br.readLine();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e ) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();
        }
        return result;
    }

    // TODO This is only a test method for the purpose of doing a junit test
    public static int add1(int num) {
        return num++;
    }

}
