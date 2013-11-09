package co.grandcircus.myredditreaderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt on 11/6/13.
 */
public class RedditEntryAdapter extends ArrayAdapter<RedditEntry> {

    public RedditEntryAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row, null);
        }

        RedditEntry thisEntry = getItem(position);
        if (thisEntry != null) {
            ImageView imageView = (ImageView)convertView.findViewById(R.id.list_image);
            String url = thisEntry.getThumbnailURL();
            // Is the following line necessary to keep a fast scrolling listView from putting
            // an old, but not yet loaded image, onto a new ImageView?
//            TODO Picasso.with(context).cancelRequest(imageView);
            // TODO Not sure why I need the following
            if (url.equals("")) {
                // If there is no image, clear any old image from the ImageView so the previous
                // one doesn't show up again.
                imageView.setImageResource(android.R.color.transparent);
            } else {
                Picasso.with(getContext()).load(url).into(imageView);
                // Make it so that while the picture is loading it shows a little circular
                // progress circle instead of whatever it shows now?
            }

            TextView textView = (TextView) convertView.findViewById(R.id.list_title);
            textView.setText(thisEntry.getTitle());
        }

        return convertView;
//        return super.getView(position, convertView, parent);
    }
}
