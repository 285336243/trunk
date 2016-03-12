package com.socialtv;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.Intents;
import com.socialtv.core.Utils;
import com.socialtv.home.HomeActivity;
import com.socialtv.personcenter.RegisterActivity;
import com.socialtv.util.IConstant;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-9-10.
 *
 * 引导页面
 */
@ContentView(R.layout.guide)
public class GuideActivity extends DialogFragmentActivity {

    @Inject
    private Activity activity;

    @InjectView(R.id.guide_pager)
    private ViewPager pager;

    @InjectView(R.id.guide_indicator)
    private CirclePageIndicator indicator;

    @InjectView(R.id.guide_register_skip)
    private View registerSkipView;

    @InjectView(R.id.guide_register)
    private View registerView;

    @InjectView(R.id.guide_skip)
    private View skipView;

    private GuideBroadcastReceiver receiver;

    private final List<Guide> items = new ArrayList<Guide>();

    private int width;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isHideTitle = getIntent().getBooleanExtra(IConstant.IS_HIDE_TITLE, true);
        if (isHideTitle) {
            getSupportActionBar().hide();
        } else {
            getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("使用帮助");
            setSupportProgressBarIndeterminateVisibility(false);
        }
        boolean isHideButton = getIntent().getBooleanExtra(IConstant.IS_GUIDE_BUTTON_HIDE, false);
        if (isHideButton) {
            registerSkipView.setVisibility(View.GONE);
        } else {
            registerSkipView.setVisibility(View.VISIBLE);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;

        skipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGuideState();
                startActivity(new Intents(activity, HomeActivity.class).toIntent());
                finish();
            }
        });

        registerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intents(activity, RegisterActivity.class).toIntent());
                saveGuideState();
            }
        });

        receiver = new GuideBroadcastReceiver();
        registerReceiver(receiver, new IntentFilter(IConstant.GUIDE));

        Guide guide1 = new Guide();
        guide1.setDrawableRes(R.drawable.guide1);
        guide1.setText1("积分系统回归升级");
        guide1.setText2("番茄币全新登场 更多渠道可获取");
        guide1.setText3("成为富翁不是梦 商城好礼随心换");

        Guide guide2 = new Guide();
        guide2.setDrawableRes(R.drawable.guide2);
        guide2.setText1("截 图");
        guide2.setText2("吐槽利器一键截屏");
        guide2.setText3("灌水刷屏盖高楼有图有真相");

        Guide guide3 = new Guide();
        guide3.setDrawableRes(R.drawable.guide3);
        guide3.setText1("更多分享");
        guide3.setText2("独乐乐不如众乐乐");
        guide3.setText3("好友闺蜜变粉丝");
        guide3.setText4("一起创造属于你的娱乐圈");

        Guide guide4 = new Guide();
        guide4.setDrawableRes(R.drawable.guide4);
        guide4.setText1("约票神器");
        guide4.setText2("抢梦之声现场票");
        guide4.setText3("为我的偶像投票");
        guide4.setText4("这里是唯一渠道");

        items.add(guide1);
        items.add(guide2);
        items.add(guide3);
        items.add(guide4);
        pager.setAdapter(new GuideAdapter());
        indicator.setViewPager(pager);
    }

    /**
     * 设置是否进入过引导页,如果进入过下次则直接进入主页
     */
    private void saveGuideState() {
        final SharedPreferences preferences = getSharedPreferences(IConstant.GUIDE, MODE_PRIVATE);
        preferences.edit().putLong(IConstant.GUIDE, 1).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * 引导页的广播,收到广播跳转到主页
     */
    private class GuideBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (IConstant.GUIDE.equals(intent.getAction())) {
                startActivity(new Intents(activity, HomeActivity.class).toIntent());
                finish();
            }
        }
    }

    /**
     * 引导页Viewpager的Adapter
     */
    private class GuideAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final View view = getLayoutInflater().inflate(R.layout.guide_item, null);
            container.addView(view);
            ImageView imageView = (ImageView) view.findViewById(R.id.guide_item_image);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) ((width - Utils.dip2px(activity, 16) * 2) * 0.875));
            params.leftMargin = Utils.dip2px(activity, 16);
            params.rightMargin = Utils.dip2px(activity, 16);
            imageView.setLayoutParams(params);
            imageView.setImageResource(items.get(position).getDrawableRes());
            TextView textView1 = (TextView) view.findViewById(R.id.guide_item_text_1);
            textView1.setText(items.get(position).getText1());
            TextView textView2 = (TextView) view.findViewById(R.id.guide_item_text_2);
            textView2.setText(items.get(position).getText2());
            TextView textView3 = (TextView) view.findViewById(R.id.guide_item_text_3);
            textView3.setText(items.get(position).getText3());
            TextView textView4 = (TextView) view.findViewById(R.id.guide_item_text_4);
            if (TextUtils.isEmpty(items.get(position).getText4())) {
                textView4.setVisibility(View.GONE);
            } else {
                textView4.setVisibility(View.VISIBLE);
                textView4.setText(items.get(position).getText4());
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
             container.removeView((View) object);
        }
    }
}
