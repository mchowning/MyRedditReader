package co.grandcircus.myredditreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.grandcircus.myredditreader.Models.RedditEntry;
import co.grandcircus.myredditreader.Models.SubReddit;

/**
 * Created by Matt on 11/10/13.
 */
public class SubRedditEntryListFragment extends ListFragment {

    // The form of the reddit address is REDDIT_BASE_EXTENSION + subredditExtension +
    // JSON_EXTENSION + PREFEX_FOR_NEXT ENTRIES_EXTENSION + nextEntriesExtension
    private static final String REDDIT_BASE_EXTENSION = "http://www.reddit.com/";
    private static final String JSON_EXTENSION = ".json";

    // This prefix can be added even when the next entries extension is blank.  It will just
    // load the initial page of entries.
    private static final String PREFIX_FOR_NEXT_ENTRIES_EXTENSION = "?after=";

    private String nextEntriesExtension = "";      // url extension for next page of entries

    private RedditEntryAdapter redditEntryAdapter;

    SubReddit subReddit;

    // Apparently this is required for calls by various android system methods.
    public SubRedditEntryListFragment() {
        super();
    }

    @Override
    public void onStart() {
        super.onStart();
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO Start new Activty
                RedditEntry redditEntry = (RedditEntry) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(), EntryDisplayActivity.class);
                intent.putExtra("entry", redditEntry);
                startActivity(intent);
            }
        });
    }


    public SubRedditEntryListFragment(SubReddit subReddit) {
        this.subReddit = subReddit;
        new GetSubredditStories().execute(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);                                // unnecessary?
//      setRetainInstance(true);                                                                    // Can't see that this is making any difference.
        redditEntryAdapter = new RedditEntryAdapter(getActivity(),
                android.R.layout.simple_spinner_item);
        setListAdapter(redditEntryAdapter);
        return inflater.inflate(R.layout.fragment_subreddit, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getListView().setOnScrollListener(new InfiniteScrollListener(20) {
            @Override
            public void loadAdditionalEntries() {
                new GetSubredditStories().execute(null);
            }
        });
    }

    private class GetSubredditStories extends AsyncTask<Void, Void, ArrayList<RedditEntry>> {

        @Override
        protected ArrayList<RedditEntry> doInBackground(Void... voids) {
            String url = REDDIT_BASE_EXTENSION + subReddit.getUrl() + JSON_EXTENSION +
                    PREFIX_FOR_NEXT_ENTRIES_EXTENSION + nextEntriesExtension;

            String source = JSONParser.getJSONString(url);

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
}
