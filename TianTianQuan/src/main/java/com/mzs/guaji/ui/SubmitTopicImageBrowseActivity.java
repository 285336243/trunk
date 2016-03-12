package com.mzs.guaji.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.Activity;
import com.mzs.guaji.util.ImageUtils;

import java.io.File;

/**
 * Created by wlanjie on 13-12-30.
 * 发布话题里的图片设置,删除
 */
public class SubmitTopicImageBrowseActivity extends GuaJiActivity {

    private Context context = SubmitTopicImageBrowseActivity.this;
    private LinearLayout mCancelLayout;
    private LinearLayout mDeleteLayout;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.submit_topic_browser_image_layout);
        String mImagePath = getIntent().getStringExtra("imagePath");

        ImageView mImageView = (ImageView) findViewById(R.id.submit_topic_browser_image);
        mImageLoader.displayImage(Uri.fromFile(new File(mImagePath)).toString(), mImageView, ImageUtils.imageLoader(context, 0));
        mCancelLayout = (LinearLayout) findViewById(R.id.submit_topic_browser_cancel);
        mCancelLayout.setOnClickListener(mBackClickListener);
        mDeleteLayout = (LinearLayout) findViewById(R.id.submit_topic_browser_delete);
        mDeleteLayout.setOnClickListener(mDeleteClickListener);
    }

    /**
     * 删除点击事件
     */
    View.OnClickListener mDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(RESULT_OK);
            finish();
        }
    };
}
