package co.grandcircus.myredditreader;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import co.grandcircus.myredditreader.Models.SubReddit;

/**
 * Created by Matt on 11/10/13.
 */
public class SubRedditListingFragment extends Fragment {                                            // TODO Make it so that the layout changes with the different orientations
                                                                                                    // TODO Give spinner infinite scrolling?
    private ArrayAdapter<SubReddit> subredditAdapter;

    public SubRedditListingFragment() {
        super();
//        setRetainInstance(true);                                                                  // Can't tell what different this makes
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listing_of_subreddits, container, false);
//        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Spinner spinner = (Spinner) getView().findViewById(R.id.spinner_with_subreddits);

        subredditAdapter = new ArrayAdapter<SubReddit>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(subredditAdapter);

        // Add main reddit page as initial "subreddit" listing in spinner
        SubReddit top = new SubReddit();
        top.setName("top");
        top.setUrl("");
        subredditAdapter.add(top);

        // Fill in rest of subReddits
        new GetListOfSubReddits().execute(null);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SubReddit selectedSubReddit = (SubReddit) adapterView.getSelectedItem();

                SubRedditActivity activity = (SubRedditActivity)getActivity();
                activity.setSelectedSubReddit(selectedSubReddit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private class GetListOfSubReddits extends AsyncTask<Void, Void, ArrayList<SubReddit>> {

        private static final String SUB_REDDIT_JSON = "http://www.reddit.com/reddits.json";

        @Override
        protected ArrayList<SubReddit> doInBackground(Void... voids) {

            // Use an ArrayList that is passed to onPostExecute because updating the adapter
            // directly updates the UI, which cannot be done in a separate thread.
            ArrayList<SubReddit> subReddits = new ArrayList<SubReddit>();

            // Get subreddits from json string and add to ArrayList
            String source = JSONParser.getJSONString(SUB_REDDIT_JSON);
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
}
