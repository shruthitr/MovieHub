
package edu.uic.moviehub.adapter;

import edu.uic.moviehub.model.MovieModel;
import edu.uic.moviehub.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/**
 * Movie adapter. Used to load movies information in the movies list.
 */
public class MovieAdapter extends ArrayAdapter<MovieModel> {
    private ArrayList<MovieModel> moviesList;
    private LayoutInflater vi;
    private int Resource;
    private ViewHolder holder;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public MovieAdapter(Context context, int resource, ArrayList<MovieModel> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        moviesList = objects;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .showImageOnLoading(R.drawable.placeholder_default)
                .showImageForEmptyUri(R.drawable.placeholder_default)
                .showImageOnFail(R.drawable.placeholder_default)
                .cacheOnDisk(true)
                .build();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convert view = design
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.title = (TextView) v.findViewById(R.id.title);
            holder.posterPath = (ImageView) v.findViewById(R.id.posterPath);
            holder.character = (TextView) v.findViewById(R.id.character);
            holder.department = (TextView) v.findViewById(R.id.department);
            holder.releaseDate = (TextView) v.findViewById(R.id.releaseDate);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }


        holder.title.setText(moviesList.get(position).getTitle());


        if (moviesList.get(position).getReleaseDate() != null) {
            holder.releaseDate.setText("(" + moviesList.get(position).getReleaseDate() + ")");
            holder.releaseDate.setVisibility(View.VISIBLE);
        } else
            holder.releaseDate.setVisibility(View.GONE);


        if (moviesList.get(position).getCharacter() != null) {
            holder.character.setText(moviesList.get(position).getCharacter());
            holder.character.setVisibility(View.VISIBLE);
        } else
            holder.character.setVisibility(View.GONE);


        if (moviesList.get(position).getDepartmentAndJob() != null) {
            holder.department.setText(moviesList.get(position).getDepartmentAndJob());
            holder.department.setVisibility(View.VISIBLE);
        } else
            holder.department.setVisibility(View.GONE);

        // if getPosterPath returns null imageLoader automatically sets default image
        imageLoader.displayImage(moviesList.get(position).getPosterPath(), holder.posterPath, options);


        return v;

    }

    /**
     * Defines movie list row elements.
     */
    static class ViewHolder {
        public TextView title;
        public ImageView posterPath;
        public TextView character;
        public TextView department;
        public TextView releaseDate;
    }


}