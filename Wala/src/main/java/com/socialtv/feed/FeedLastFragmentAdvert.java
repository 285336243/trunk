package com.socialtv.feed;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mobisage.android.MobiSageAdBanner;
import com.mobisage.android.MobiSageAdBannerListener;
import com.socialtv.R;
import com.socialtv.core.Log;
import com.socialtv.feed.advert.AdvertFeedAdapter;

/**
 * 增加广告的最新
 */
public class FeedLastFragmentAdvert extends FeedLastestFragment {
    private MobiSageAdBanner mBanner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter=new AdvertFeedAdapter(this);
    }

    @Override
    protected boolean isAddAdapterHeader() {
        return true;
    }

    @Override
    protected View adapterHeaderView() {
        RelativeLayout headerView = (RelativeLayout) getLayoutInflater(null).inflate(R.layout.horizonta_advertse, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        // 创建一个自适应屏幕的横幅广告
        mBanner = new MobiSageAdBanner(getActivity(), "QkNCkS3Vz1hytnMYxVxZf85k");
//        // 设置广告监听
        mBanner.setMobiSageAdBannerListener(mListener);
        headerView.addView(mBanner.getAdView(), layoutParams);

        return headerView;
    }

    private MobiSageAdBannerListener mListener = new MobiSageAdBannerListener() {

        @Override
        public void onMobiSageBannerShow(MobiSageAdBanner adView) {
            Log.i("MobisageSample", "onMobiSageBannerShow");
        }

        @Override
        public void onMobiSageBannerError(MobiSageAdBanner adView, String msg) {
            Log.i("MobisageSample", "onMobiSageBannerError:" + msg);
        }

        @Override
        public void onMobiSageBannerClick(MobiSageAdBanner adView) {
            Log.i("MobisageSample", "onMobiSageBannerClick");
        }

        @Override
        public void onMobiSageBannerPopupWindow(MobiSageAdBanner adView) {
            Log.i("MobisageSample", "onMobiSageBannerPopupWindow");
        }

        @Override
        public void onMobiSageBannerHideWindow(MobiSageAdBanner adView) {
            Log.i("MobisageSample", "onMobiSageBannerHideWindow");
        }

    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 销毁广告
        if (mBanner != null) {
            mBanner.destroyAdView();
        }
    }


}
