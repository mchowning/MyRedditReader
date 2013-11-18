package co.grandcircus.myredditreader;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import co.grandcircus.myredditreader.Models.RedditEntry;

public class EntryDisplayActivity extends ActionBarActivity {

    // FIXME Changing orientation on this breaks the activity.  Why???

//    public EntryDisplayActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_display);

        if (savedInstanceState == null) {
            RedditEntry redditEntry = getIntent().getParcelableExtra("entry");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(redditEntry))
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entry_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private RedditEntry entry;

        public PlaceholderFragment() {}

        public PlaceholderFragment(RedditEntry entry) {
            this.entry = entry;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putParcelable("entry", entry);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (savedInstanceState != null) {
                entry = savedInstanceState.getParcelable("entry");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_entry_display, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

//            if (entry != null) {                                                                    // FIXME This avoids a null pointer exception on rotate, but I have still lost the entry variable.
                TextView textView =
                        (TextView) getActivity().findViewById(R.id.textview_reddit_entry_title);
                textView.setText(entry.getTitle());

                String url = entry.getThumbnailURL();
                if (!url.equals("")) {
                    ImageView imageView =
                            (ImageView) getActivity().findViewById(R.id.imageview_reddit_title_image);
                    Picasso.with(getActivity()).load(url).into(imageView);
                }
//            }
        }
    }

}
