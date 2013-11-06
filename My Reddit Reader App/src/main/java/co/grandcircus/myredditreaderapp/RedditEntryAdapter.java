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

    private ArrayList<RedditEntry> objects;
    private Context context;

    public RedditEntryAdapter(Context context, int textViewResourceId, ArrayList<RedditEntry> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row, null);
        }

        RedditEntry thisEntry = objects.get(position);
        if (thisEntry != null) {
            ImageView imageView = (ImageView)convertView.findViewById(R.id.list_image);
            // TODO Need to fill in ImageView

            String url = thisEntry.getThumbnailURL();
            // Is the following line necessary to keep a fast scrolling listView from putting an
            // old, but not yet loaded image, onto a new ImageView?
//            Picasso.with(context).cancelRequest(imageView);
            // TODO Not sure why I need this
            if (url.equals("")) {
                // If there is no image, clear any old image from the ImageView so the previous
                // one doesn't show up again.
                imageView.setImageResource(android.R.color.transparent);
            } else {
                Picasso.with(context).load(url).into(imageView);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.list_title);
            textView.setText(thisEntry.getTitle());
        }

        return convertView;
//        return super.getView(position, convertView, parent);
    }
}
