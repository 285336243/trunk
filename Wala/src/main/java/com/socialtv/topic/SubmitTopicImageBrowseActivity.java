package com.socialtv.topic;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;

import java.io.File;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import uk.co.senab.photoview.PhotoView;

@ContentView(R.layout.submit_topic_browser_image_layout)
public class SubmitTopicImageBrowseActivity extends DialogFragmentActivity {

    @Inject
    private Activity activity;

    @InjectView(R.id.submit_topic_browser_image)
    private PhotoView photoView;

    @InjectView(R.id.submit_topic_browser_cancel)
    private View cancelView;

    @InjectView(R.id.submit_topic_browser_delete)
    private View deleteView;

    @InjectExtra(value = IConstant.IMAGE_PATH, optional = true)
    private String imagePath;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getSupportActionBar().hide();
        ImageLoader.getInstance().displayImage(Uri.fromFile(new File(imagePath)).toString(), photoView, ImageUtils.imageLoader(activity, 0));
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
