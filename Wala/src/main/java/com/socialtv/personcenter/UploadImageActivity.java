package com.socialtv.personcenter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;

import java.io.File;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by wlanjie on 14-7-5.
 */
@ContentView(R.layout.upload_image_layout)
public class UploadImageActivity extends DialogFragmentActivity {

    @Inject
    private Activity activity;

    @InjectExtra(value = IConstant.IMAGE_PATH, optional = true)
    private String imagePath;

    @InjectView(R.id.upload_back)
    private View backView;

    @InjectView(R.id.upload_complete)
    private View completeView;

    @InjectView(R.id.upload_image)
    private PhotoView photoView;

    @Inject
    private PersonalUrlService services;

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        file = new File(imagePath);

        ImageLoader.getInstance().displayImage(Uri.fromFile(file).toString(), photoView, ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
            }
        });

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        completeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUploadImage();
            }
        });
    }

    private void startUploadImage() {
        new ProgressDialogTask<Response>(activity){
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createUploadPhotoAlbumRequest(file)).result;
            }

            /**
             * Sub-classes may override but should always call super to ensure the
             * progress dialog is dismissed
             *
             * @param response
             */
            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastUtils.show(activity, response.getResponseMessage());
                    }
                }
            }
        }.start("正在上传图片");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (file != null && file.exists()) {
            file.delete();
        }
    }
}
