package com.shengzhish.xyj.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.DialogFragment;
import com.shengzhish.xyj.util.ImageUtils;

/**
 * Created by wlanjie on 14-5-30.
 */
public class BannerImageFragment extends DialogFragment {

    public static BannerImageFragment newInstance(String imageUrl) {
        BannerImageFragment fragment = new BannerImageFragment();
        Bundle args = new Bundle();
        args.putString("imageUrl", imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.banner_image, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = (ImageView) view.findViewById(R.id.banner_image);
        Bundle args = getArguments();
        if (args != null)
            ImageLoader.getInstance().displayImage(args.getString("imageUrl"), imageView, ImageUtils.imageLoader(getActivity(), 0));
    }
}
