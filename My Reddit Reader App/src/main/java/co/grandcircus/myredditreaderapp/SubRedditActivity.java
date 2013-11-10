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
import android.widget.AbsListView;
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

    /* The form of the reddit address is REDDIT_BASE_EXTENSION + subredditExtension +
     * JSON_EXTENSION + PREFEX_FOR_NEXT ENTRIES_EXTENSION + nextEntriesExtension
     */
    public static final String REDDIT_BASE_EXTENSION = "http://www.reddit.com/";
    public static final String JSON_EXTENSION = ".json";
    // This prefix can be added even when the next entries extension is blank.  It will just
    // load the initial page of entries.
    public static final String PREFIX_FOR_NEXT_ENTRIES_EXTENSION = "?after=";
    private String subredditExtension;
    private String nextEntriesExtension;      // url extension for next page of entries
    private static final int LOAD_ADDITIONAL_ENTRIES_TOLERANCE = 20;

    private ArrayAdapter<SubReddit> subredditAdapter;
    private RedditEntryAdapter redditEntryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {                                            // TODO Need to figure out how to save application state for when the application is hidden
        super.onCreate(savedInstanceState);                                                         //      and then brought back from the background.
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

    }

    // Have this stuff in onStart() because I cannot access the Fragment's views from onCreate().
    // Could probably do this in the Fragment's onCreateView, but then I would have to pass the
    // adapters to the fragment.
    @Override
    protected void onStart() {
        super.onStart();

        Spinner spinner = (Spinner) findViewById(R.id.spinner_subreddit);
        spinner.setAdapter(subredditAdapter);

        // Add initial "top" reddit feed as initial feed. This will be the first feed loaded
        // on startup.
        SubReddit top = new SubReddit();
        top.setName("top");
        top.setUrl("");
        subredditAdapter.add(top);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // The nextEntriesExtension will be swapped into the currentAfterPosition when
                // the next 'after' extension is read from the .json file.  So initialize
                // nextEntriesExtension to "" so when it is copied to the subredditCurrentEntriesExtension
                // for upon reading the first page of subreddits and that subredditCurrentEntriesExtension
                // is added to the url string you just get the plain subredddit url by itself.
                nextEntriesExtension = "";

                SubReddit selectedSubReddit = (SubReddit) adapterView.getSelectedItem();
                subredditExtension = selectedSubReddit.getUrl();
                redditEntryAdapter.clear();
                new GetSubredditStories().execute(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        new GetListOfSubReddits().execute(null);
        ListView listView = (ListView)findViewById(R.id.list_reddit_entries);
        listView.setAdapter(redditEntryAdapter);
        resetRedditEntriesScrollListener();
    }

    private void resetRedditEntriesScrollListener() {
        ListView listView = (ListView)findViewById(R.id.list_reddit_entries);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private boolean loading = false;
            private int previousTotalItemCount = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {}

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                int visibleItemCount, int totalItemCount) {
                // Avoids this method from taking effect before the ListView is initialized
                if (totalItemCount == 0) return;
                /* Initializes the previousTotalItemCount variable if it is either the first time
                   that the funciton is being called (==0) or it is being called in connection with
                   the display of a new subreddit (>= totalItemCount) */
                if (previousTotalItemCount == 0 || previousTotalItemCount > totalItemCount) {
                    previousTotalItemCount = totalItemCount;
                }
                int endOfVisibleList = firstVisibleItem + visibleItemCount;
                int locationToLoadAdditionalEntries = totalItemCount - LOAD_ADDITIONAL_ENTRIES_TOLERANCE;
                if (loading) {
                    if (totalItemCount > previousTotalItemCount) {
                        loading = false;
                        previousTotalItemCount = totalItemCount;
                    }
                } else {
                    if (endOfVisibleList >= locationToLoadAdditionalEntries) {
                        loading = true;
                        new GetSubredditStories().execute(null);
                    }
                }
            }
        });
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

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_subreddit, container, false);
        }
    }

    private class GetListOfSubReddits extends AsyncTask<Void, Void, ArrayList<SubReddit>> {

        public static final String SUB_REDDIT_JSON = "http://www.reddit.com/reddits.json";

        @Override
        protected ArrayList<SubReddit> doInBackground(Void... voids) {

            // Use an ArrayList that is passed to onPostExecute because updating the adapter
            // directly updates the UI, which cannot be done in a separate thread.
            ArrayList<SubReddit> subReddits = new ArrayList<SubReddit>();

            // Get subreddits from json string and add to ArrayList
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
            return subReddits;
        }

        @Override
        protected void onPostExecute(ArrayList<SubReddit> subReddits) {
            subredditAdapter.addAll(subReddits);
        }
    }

    private class GetSubredditStories extends AsyncTask<Void, Void, ArrayList<RedditEntry>> {

        @Override
        protected ArrayList<RedditEntry> doInBackground(Void... voids) {
            String url = REDDIT_BASE_EXTENSION + subredditExtension + JSON_EXTENSION +
                    PREFIX_FOR_NEXT_ENTRIES_EXTENSION + nextEntriesExtension;
            String source = getJSONString(url);

            // Can't update the ListView's adapter directly because that is updating the UI
            // from a thread, which results in an error.
            ArrayList<RedditEntry> redditEntries = new ArrayList<RedditEntry>();

            // Get reddit entries from json string and add to reddit arraylist
            try {
                JSONObject root = new JSONObject(source);
                JSONObject data = root.getJSONObject("data"); // Contains the list that we want
                nextEntriesExtension = data.optString("after", "");  // Link to next page

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
            }
            return redditEntries;
        }

        @Override
        protected void onPostExecute(ArrayList<RedditEntry> redditEntries) {
            redditEntryAdapter.addAll(redditEntries);
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
