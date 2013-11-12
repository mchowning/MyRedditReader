package co.grandcircus.myredditreader;

import android.widget.AbsListView;

/**
 * Created by Matt on 11/11/13.
 */
public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {

    private boolean loading = false;
    private int previousTotalItemCount = 0;
    private int tolerance;

    public InfiniteScrollListener(int toleranceForReload) {
        super();
        tolerance = toleranceForReload;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {}

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem,
            int visibleItemCount, int totalItemCount)
    {
        // Avoids this method from taking effect before the ListView is initialized
        if (totalItemCount == 0) return;
                /* Initializes the previousTotalItemCount variable if it is either the first time
                   that the funciton is being called (==0) or it is being called in connection with
                   the display of a new subreddit (>= totalItemCount) */
        if (previousTotalItemCount == 0 || previousTotalItemCount > totalItemCount) {
            previousTotalItemCount = totalItemCount;
        }
        int endOfVisibleList = firstVisibleItem + visibleItemCount;
        int locationToLoadAdditionalEntries = totalItemCount - tolerance;
        if (loading) {
            if (totalItemCount > previousTotalItemCount) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }
        } else {
            if (endOfVisibleList >= locationToLoadAdditionalEntries) {
                loading = true;
                loadAdditionalEntries();
            }
        }
    }

    public abstract void loadAdditionalEntries();
}
