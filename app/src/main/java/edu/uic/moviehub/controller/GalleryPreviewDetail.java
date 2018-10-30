package edu.uic.moviehub.controller;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import edu.uic.moviehub.activities.MainActivity;
import edu.uic.moviehub.R;

/**
 * This fragment is the gallery preview for every image.
 */
public class GalleryPreviewDetail extends Fragment {
    private MainActivity activity;
    private String currImg;
    private ImageView mImageView;
    private ProgressBar progressBar;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener imageLoadingListener;
    private int mUIFlag = View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;



    public static GalleryPreviewDetail newInstance(String imageUrl) {
        final GalleryPreviewDetail f = new GalleryPreviewDetail();

        final Bundle args = new Bundle();
        args.putString("currImg", imageUrl);
        f.setArguments(args);

        return f;
    }

    public GalleryPreviewDetail() {

    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currImg = getArguments() != null ? getArguments().getString("currImg") : null;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(false)
                .showImageOnLoading(null)
                .showImageForEmptyUri(null)
                .showImageOnFail(null)
                .cacheOnDisk(true)
                .build();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        imageLoadingListener = new ImageLoadingListener();
        OnImageClick onImageClick = new OnImageClick();

        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.gallerypreviewdetail, container, false);
        activity = ((MainActivity) getActivity());
        mImageView = (ImageView) v.findViewById(R.id.galleryPreviewImgHolder);
        mImageView.setOnClickListener(onImageClick);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        activity.getMDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (activity.getSupportActionBar() != null && activity.getSupportActionBar().isShowing())
            activity.getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= 19) {
            mUIFlag ^= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }

        if (this.isVisible()) {
            // Check orientation and lock to portrait if we are on phone
            if (getResources().getBoolean(R.bool.portrait_only)) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }
        }
        imageLoader.displayImage(currImg, mImageView, options, imageLoadingListener);
    }


    private class ImageLoadingListener extends SimpleImageLoadingListener {

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            progressBar.setVisibility(View.GONE);
        }
    }


    public class OnImageClick implements View.OnClickListener {
        public OnImageClick() {
            // keep references for your onClick logic
        }

        @Override
        public void onClick(View v) {
            int uiOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (uiOptions == mUIFlag) {
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if (Build.VERSION.SDK_INT >= 19) {
                        Window w = activity.getWindow();
                        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                        w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    }
                } else
                    activity.getWindow().getDecorView().setSystemUiVisibility(mUIFlag);

            }
        }
    }


    public void onDestroyView() {
        super.onDestroyView();
        mImageView.setImageDrawable(null);
    }


}


