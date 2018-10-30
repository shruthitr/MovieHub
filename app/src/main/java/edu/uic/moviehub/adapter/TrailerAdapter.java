

package edu.uic.moviehub.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;

import edu.uic.moviehub.activities.MainActivity;
import edu.uic.moviehub.MovieHub;
import edu.uic.moviehub.R;
import edu.uic.moviehub.model.TrailerModel;

/**
 * Trailer adapter. Used to load trailer information in the trailer list.
 */
public class TrailerAdapter extends ArrayAdapter<TrailerModel> {
    private ArrayList<TrailerModel> trailerList;
    private LayoutInflater vi;
    private int Resource;
    private ViewHolder holder;
    private Context mContext;
    private ImageLoader imageLoader;

    public TrailerAdapter(Context context, int resource, ArrayList<TrailerModel> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        trailerList = objects;
        mContext = context;
        imageLoader = ImageLoader.getInstance();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // convert view = design
        View v = convertView;
        if (v == null) {
            holder = new ViewHolder();
            v = vi.inflate(Resource, null);
            holder.filePath = (ImageView) v.findViewById(R.id.filePath);


            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        if (imageLoader.getDiskCache().get(MovieHub.trailerImageUrl + trailerList.get(position).getFilePath() + "/hqdefault.jpg").exists())
            imageLoader.displayImage(MovieHub.trailerImageUrl + trailerList.get(position).getFilePath() + "/hqdefault.jpg", holder.filePath, ((MainActivity) mContext).getOptionsWithoutFade());
        else
            imageLoader.displayImage(MovieHub.trailerImageUrl + trailerList.get(position).getFilePath() + "/hqdefault.jpg", holder.filePath, ((MainActivity) mContext).getOptionsWithFade());


        return v;

    }

    /**
     * Defines trailer list row elements.
     */
    static class ViewHolder {
        public ImageView filePath;
    }


}