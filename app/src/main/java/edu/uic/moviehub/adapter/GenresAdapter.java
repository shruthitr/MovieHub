
package edu.uic.moviehub.adapter;

import edu.uic.moviehub.model.GenresModel;
import edu.uic.moviehub.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Genres adapter. Used to load genres information in the genres list.
 */
public class GenresAdapter extends ArrayAdapter<GenresModel> {
    private ArrayList<GenresModel> genresList;
    private LayoutInflater vi;
    private int Resource;
    private ViewHolder holder;

    public GenresAdapter(Context context, int resource, ArrayList<GenresModel> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        genresList = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convert view = design
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.name = (TextView) v.findViewById(R.id.name);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.name.setText(genresList.get(position).getName());


        return v;

    }

    /**
     * Defines genres list row elements.
     */
    static class ViewHolder {
        public TextView name;
    }

}