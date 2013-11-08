package co.grandcircus.myredditreaderapp;

import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class SubRedditActivity extends ActionBarActivity {

    public static final String REDDIT_BASE = "http://www.reddit.com/";
    public static final String JSON_EXTENSION = ".json";
    private String subredditExtension;
    private ArrayList<SubReddit> subReddits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddit);
        new GetSubReddits().execute(null);
//        new GetRedditThread().execute(null);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        // Set up HttpResponseCache for caching of all http requests to improve performance
        // Requires API min of 14 (note the clearing of the cache in onStop as well)
//        try {
//            File httpCacheDir = new File(this.getCacheDir(), "http");
//            long httpCacheSize = 5 * 1024 * 1024; // 5 MiB
//            HttpResponseCache.install(httpCacheDir, httpCacheSize);
//        } catch (IOException e) {
//            Log.e("SubRedditActivity.onCreate", "HTTP response cache installation failed:" + e);
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.subreddit, menu);
        return true;
    }

    @Override
    protected void onStop() {
//        HttpResponseCache cache = HttpResponseCache.getInstalled();
//        if (cache != null) cache.flush();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_subreddit, container, false);
            return rootView;
        }
    }

    private class GetSubReddits extends AsyncTask<Void, Void, Void> {

        public static final String SUB_REDDIT_JSON = "http://www.reddit.com/reddits.json";

        @Override
        protected Void doInBackground(Void... voids) {

            subReddits = new ArrayList<SubReddit>();

            //Add initial "top" reddit feed as initial feed which will be the first one loaded
            SubReddit top = new SubReddit();
            top.setName("top");
            top.setUrl("");
            subReddits.add(top);

            String source = getJSONString(SUB_REDDIT_JSON);

            try {
                JSONObject root = new JSONObject(source);
                JSONObject data = root.getJSONObject("data");
                JSONArray items = data.getJSONArray("children");

                for (int i = 0; i < items.length(); i++) {
                    JSONObject entry = items.getJSONObject(i);
                    JSONObject entryData = entry.getJSONObject("data");

                    SubReddit subRedditEntry = new SubReddit();
                    String title = entryData.optString("title");
                    subRedditEntry.setName(title);
                    String url = entryData.optString("url");
                    subRedditEntry.setUrl(url);

                    subReddits.add(subRedditEntry);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayAdapter<SubReddit> adapter = new ArrayAdapter<SubReddit>(SubRedditActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, subReddits);
            Spinner spinner = (Spinner) findViewById(R.id.spinner_subreddit);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    SubReddit selectedSubReddit = (SubReddit) adapterView.getSelectedItem();
                    subredditExtension = selectedSubReddit.getUrl();
                    new GetRedditThread().execute(null);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    private class GetRedditThread extends AsyncTask<String, Void, ArrayList<RedditEntry>> {

        @Override
        protected ArrayList<RedditEntry> doInBackground(String... voids) {
            return getRedditStuff();
        }

        @Override
        protected void onPostExecute(ArrayList<RedditEntry> redditEntries) {
            // Fill in ListView with reddit entries
            ListView listView = (ListView) findViewById(R.id.list_reddit_entries);
            RedditEntryAdapter adapter = new RedditEntryAdapter(SubRedditActivity.this,
                    android.R.layout.simple_spinner_item, redditEntries);

            // clear listView before having it start loading new content so the old content
            // doesn't show while loading the new content.
            listView.setAdapter(null);
            listView.setAdapter(adapter);
        }

        private ArrayList<RedditEntry> getRedditStuff() {

            String result = getJSONString(REDDIT_BASE + subredditExtension + JSON_EXTENSION);

            JSONSubreddit jsr = new JSONSubreddit(result);
            return jsr.getEntries();

        }
    }

    private class JSONSubreddit {

        private ArrayList<RedditEntry> redditEntries;

        private JSONSubreddit(String source) {
            try {
                JSONObject root = new JSONObject(source);
                JSONObject data = root.getJSONObject("data"); // This contains the list that we want
                JSONArray items = data.getJSONArray("children");

                redditEntries = new ArrayList<RedditEntry>();

                for (int i = 0; i < items.length(); i++) {
                    JSONObject entry = items.getJSONObject(i);
                    JSONObject entryData = entry.getJSONObject("data");

                    RedditEntry redditEntry = new RedditEntry();

                    // Use "opt" with JSON objects so you don't get failure if you are
                    // unable to parse all of the data (i.e., one post doesn't have a picture)
                    String title = entryData.optString("title", "");
                    redditEntry.setTitle(title);

                    String thumbnailURL = entryData.optString("thumbnail", "");
                    redditEntry.setThumbnailURL(thumbnailURL);

                    redditEntries.add(redditEntry);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle error gracefully here.
            }
        }

        public ArrayList<RedditEntry> getEntries() {
            return redditEntries;
        }
    }

    private String getJSONString(String url) {
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

}
