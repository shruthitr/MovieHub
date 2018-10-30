
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
import edu.uic.moviehub.R;
import edu.uic.moviehub.model.GalleryModel;


/**
 * Gallery adapter. Used to load gallery images in the gallery list.
 */
public class GalleryAdapter extends ArrayAdapter<GalleryModel> {
    private ArrayList<GalleryModel> galleryList;
    private LayoutInflater vi;
    private int Resource;
    private ViewHolder holder;
    private Context mContext;
    private ImageLoader imageLoader;

    public GalleryAdapter(Context context, int resource, ArrayList<GalleryModel> objects) {
        super(context, resource, objects);
        vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Resource = resource;
        galleryList = objects;
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
        //used to load images from gallery

        if (imageLoader.getDiskCache().get(galleryList.get(position).getFilePath()).exists())
            imageLoader.displayImage(galleryList.get(position).getFilePath(), holder.filePath, ((MainActivity) mContext).getOptionsWithoutFade());
        else
            imageLoader.displayImage(galleryList.get(position).getFilePath(), holder.filePath, ((MainActivity) mContext).getOptionsWithFade());


        return v;

    }

    /**
     * Defines gallery list row elements.
     */
    static class ViewHolder {
        public ImageView filePath;
    }


}