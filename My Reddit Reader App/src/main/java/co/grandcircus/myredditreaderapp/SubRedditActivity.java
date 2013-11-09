package co.grandcircus.myredditreaderapp;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
    // Stores the extension for getting the current entries and the next entries.
    private String currentAfterExtension;
    private String nextAfterExtension;

    private ArrayAdapter<SubReddit> subredditAdapter;
    private RedditEntryAdapter redditEntryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subreddit);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        subredditAdapter = new ArrayAdapter<SubReddit>(SubRedditActivity.this,
            android.R.layout.simple_spinner_dropdown_item);
        redditEntryAdapter = new RedditEntryAdapter(SubRedditActivity.this,
                android.R.layout.simple_spinner_item);

        new GetListOfSubReddits().execute(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.subreddit, menu);
        return true;
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

    private class GetListOfSubReddits extends AsyncTask<Void, Void, ArrayList<SubReddit>> {

        public static final String SUB_REDDIT_JSON = "http://www.reddit.com/reddits.json";

        @Override
        protected ArrayList<SubReddit> doInBackground(Void... voids) {

            ArrayList<SubReddit> subReddits = new ArrayList<SubReddit>();

            //Add initial "top" reddit feed as initial feed which will be the first one loaded
            SubReddit top = new SubReddit();
            top.setName("top");
            top.setUrl("");
            subReddits.add(top);
//            subredditAdapter.add(top);

            // Get subreddits from json string and add to subreddit arraylist
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
//                    subredditAdapter.add(subRedditEntry);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return subReddits;
        }

        @Override
        protected void onPostExecute(ArrayList<SubReddit> subReddits) {
            Spinner spinner = (Spinner) findViewById(R.id.spinner_subreddit);
            subredditAdapter.addAll(subReddits);
            spinner.setAdapter(subredditAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    // The nextAfterExtension will be swapped into the currentAfterPosition when
                    // the next 'after' extension is read from the .json file.  So initialize
                    // nextAfterExtension to "" so when it is copied to the currentAfterExtension
                    // for upon reading the first page of subreddits and that currentAfterExtension
                    // is added to the url string you just get the plain subredddit url by itself.
                    currentAfterExtension = null;
                    nextAfterExtension = "";

                    SubReddit selectedSubReddit = (SubReddit) adapterView.getSelectedItem();
                    subredditExtension = selectedSubReddit.getUrl();
                    redditEntryAdapter.clear();
                    new GetSubredditStories().execute(null);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
    }

    private class GetSubredditStories extends AsyncTask<Void, Void, ArrayList<RedditEntry>> {

        @Override
        protected ArrayList<RedditEntry> doInBackground(Void... voids) {
            String url = REDDIT_BASE + subredditExtension + JSON_EXTENSION;
            String source = getJSONString(url);

            ArrayList<RedditEntry> redditEntries = new ArrayList<RedditEntry>();

            // Get reddit entries from json string and add to reddit arraylist
            try {
                JSONObject root = new JSONObject(source);
                JSONObject data = root.getJSONObject("data"); // Contains the list that we want
                JSONArray items = data.getJSONArray("children");


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
            return redditEntries;
        }

        @Override
        protected void onPostExecute(ArrayList<RedditEntry> redditEntries) {
            // Fill in ListView with reddit entries
            ListView listView = (ListView) findViewById(R.id.list_reddit_entries);
            redditEntryAdapter.addAll(redditEntries);
            listView.setAdapter(redditEntryAdapter);
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
