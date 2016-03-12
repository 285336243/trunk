package com.socialtv.feed.advert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobisage.android.MobiSageAdNative;
import com.mobisage.android.MobiSageAdNativeFactory;
import com.mobisage.android.MobiSageAdNativeFactoryListener;
import com.mobisage.android.MobiSageAdStatusListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.Log;
import com.socialtv.feed.FeedAdapter;
import com.socialtv.feed.FeedItemFragment;
import com.socialtv.util.ImageUtils;

import java.util.HashMap;

/**
 * 广告的adapter
 */
public class AdvertFeedAdapter extends FeedAdapter {
    private ImageLoader imageLoader;
    private MobiSageAdNativeFactory mMobiSageAdNativeFactory;
    private int mAdWidth;
    private float mDensity;
    private MobiSageAdNativeFactoryListener listener = new MobiSageAdNativeFactoryListener() {
        @Override
        public void onMobiSageNativeGroupError(MobiSageAdNativeFactory mobiSageAdNativeFactory, String s) {
            notifyDataSetChanged();
        }

        @Override
        public void onMobiSageNativeGroupSuccess(MobiSageAdNativeFactory mobiSageAdNativeFactory) {

        }
    };


    /**
     * Create adapter
     *
     * @param fragment
     */
    public AdvertFeedAdapter(FeedItemFragment fragment) {
        super(fragment);
        initAd();
        initView();
    }

    private void initView() {
        mDensity = activity.getResources().getDisplayMetrics().density;
        imageLoader = ImageLoader.getInstance();
    }

    private void initAd() {
        mAdWidth = (int) (activity.getResources().getDisplayMetrics().widthPixels
                - activity.getResources().getDisplayMetrics().density * 20);
        HashMap<String, Object> hashmap = new HashMap<String, Object>();
        hashmap.put("disableToLoad", true);
        // 实例化成组信息流广告,hashmap为高级选项,模板不下载资源,开发者自行拼装
        mMobiSageAdNativeFactory = new MobiSageAdNativeFactory(activity, "4uPiMY11b/jSFtO4Zf35327M",
                5, mAdWidth, mAdWidth * 9 / 16, hashmap);
        mMobiSageAdNativeFactory.setMobiSageAdNativeGroupListener(listener);

    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = 1;
        if ((position + 1) % 5 == 0 && position != 0) {
            type = 2;
        }
        if (mMobiSageAdNativeFactory.getAdNatives().size() == 0 || type == 1) {
            convertView = initialize(LayoutInflater.from(activity).inflate(R.layout.feed_item, null));
            return super.getView(position, convertView, parent);
        } else {
            convertView = activity.getLayoutInflater().inflate(R.layout.infor_diyione_advertse, null);
            setconvertView(convertView, position);
            Log.v("kan", "广告 position =" + position + ", convertView = " + convertView);
            return convertView;
        }

    }

    private void setconvertView(final View subView, int position) {
        final MobiSageAdNative nativead;
        if (mMobiSageAdNativeFactory.getAdNatives().size() < 5) {
            nativead =mMobiSageAdNativeFactory.getAdNatives().get(1);
        } else {
            int i = (int) (Math.random() * 5);     //     i 为0到5的随机整数
            nativead =mMobiSageAdNativeFactory.getAdNatives().get(i);
        }
        final HashMap<String, Object> hash = nativead.getContent();
        ImageView logo = (ImageView) subView.findViewById(R.id.native_sample_item_lgo);
        imageLoader.displayImage((String) hash.get("logo"), logo, ImageUtils.imageLoader(activity, 5));
        ImageView mainImage = (ImageView) subView.findViewById(R.id.native_sample_item_image);
        imageLoader.displayImage((String) hash.get("image"), mainImage, ImageUtils.imageLoader(activity, 5));
        TextView title = (TextView) subView.findViewById(R.id.native_sample_item_title);
        title.setText((String) hash.get("title"));
        double stars = Double.parseDouble(hash.get("star").toString());
        int zheng = (int) stars;
        stars = Double.parseDouble(hash.get("star").toString());
        ImageView one;
        ImageView half = new ImageView(activity);
        ImageView none;
        LayoutParams lay = new LayoutParams((int) (10 * mDensity), (int) (10 *
                mDensity));
        half.setLayoutParams(lay);
        half.setBackgroundResource(R.drawable.star_2);
        LinearLayout starLayout = (LinearLayout) subView
                .findViewById(R.id.native_sample_item_stars);
        for (int i = 0; i < zheng; i++) {
            one = new ImageView(activity);
            one.setLayoutParams(lay);
            one.setBackgroundResource(R.drawable.star);
            starLayout.addView(one);
        }
        if (zheng != stars) {
            starLayout.addView(half);

        }
        if (zheng == 0) {
            for (int i = 0; i < 5; i++) {
                none = new ImageView(activity);
                none.setLayoutParams(lay);
                none.setBackgroundResource(R.drawable.star_none);
                starLayout.addView(none);
            }
        } else {
            for (int i = 0; i < 4 - zheng; i++) {
                none = new ImageView(activity);
                none.setLayoutParams(lay);
                none.setBackgroundResource(R.drawable.star_2);
                starLayout.addView(none);
            }
        }

        TextView numberPerson = (TextView) subView.findViewById(R.id.native_sample_item_people);
        numberPerson.setText("有" + (Integer) hash.get("person") + "人正在玩");
        Button download = (Button) subView.findViewById(R.id.native_sample_item_downlaod_btn);
        download.setText(convertStatus((Integer) hash.get("adStatus")));
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nativead.handleClick();
                Log.e("TZC", "click");

            }
        });
        nativead.setMobiSageAdStatusListener(new MobiSageAdStatusListener() {
            @Override
            public void updateAdStatus(int id, int status) {
                if (((Integer) hash.get("id")) == id) {
//               ad.id(R.id.native_sample_item_downlaod_btn).text( convertStatus(status));
                    TextView statusInfo = (TextView) subView.findViewById(R.id.native_sample_item_downlaod_btn);
                    statusInfo.setText(convertStatus(status));


                }
            }
        });
        nativead.attachToView((ViewGroup) subView);
    }

    private String convertStatus(int status) {
        String strStatus = "";
        switch (status) {
            case 0:
                strStatus = "点击重试";
                break;
            case 2:
                strStatus = "下载中";
                break;
            case 3:
                strStatus = "点击安装";
                break;
            case 4:
                strStatus = "点击启动";
                break;
            case 1:
            default:
                strStatus = "下载";
        }
        return strStatus;
    }
}
