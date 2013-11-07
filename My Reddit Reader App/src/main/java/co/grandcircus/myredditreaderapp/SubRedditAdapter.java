package co.grandcircus.myredditreaderapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Matt on 11/6/13.
 */
public class SubRedditAdapter extends ArrayAdapter<SubReddit> {

    public SubRedditAdapter(Context context, int textViewResourceId, List<SubReddit> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item);
        }





        return super.getView(position, convertView, parent);

    }

}
