package feipai.qiangdan.order;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import feipai.qiangdan.R;
import uk.co.senab.photoview.PhotoView;


public class BrowseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_picture);
        String photoAddress = getIntent().getStringExtra("PICTURE_ADDRESS");
        PhotoView photoView = (PhotoView) findViewById(R.id.iv_photo);

        ImageLoader.getInstance().displayImage(Uri.fromFile(new File(photoAddress)).toString(), photoView);

//        ImageLoader.getInstance().displayImage("http://static.open-open.com/lib/uploadImg/20130417/20130417145200_549.png", photoView);
    }


    protected static void launch(Activity activity, String address) {
        activity.startActivity(new Intent(activity, BrowseActivity.class).putExtra("PICTURE_ADDRESS", address));
    }


}
