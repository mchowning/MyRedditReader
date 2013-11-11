package co.grandcircus.myredditreaderapp;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SubRedditActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {                                            // FIXME Need to figure out how to save application state for when the application is hidden
        super.onCreate(savedInstanceState);                                                         //      and then brought back from the background.
        setContentView(R.layout.activity_subreddit);

//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.frame_subreddit_entry_fragment, new SubRedditEntryListFragment())
//                    .commit();
//        }

        // Fill in frame for subReddits
        if (savedInstanceState == null) {
            SubRedditListingFragment frag = new SubRedditListingFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.add(R.id.frame_list_of_subreddits_fragment, frag);
            transaction.commit();
        }
    }

    /*
     * When called, this method fills in the subreddit entry frame with a new listfragment
     * containing the relevant subreddit's entries.
     */
    public void setSelectedSubReddit(SubReddit subReddit) {
        SubRedditEntryListFragment frag = (SubRedditEntryListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frame_subreddit_entry_fragment);
        SubRedditEntryListFragment newFrag = new SubRedditEntryListFragment(subReddit);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);                                                           // FIXME Can't seem to go back to a previous SubRedditEntryListFragment
        transaction.replace(R.id.frame_subreddit_entry_fragment, newFrag);
        transaction.commit();
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
}
