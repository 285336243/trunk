package com.socialtv.shop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.github.kevinsawicki.wishlist.ViewFinder;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.Intents;
import com.socialtv.core.ToastUtils;
import com.socialtv.home.WebViewActivity;
import com.socialtv.home.entity.Banner;
import com.socialtv.home.entity.Refer;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.shop.entity.AdVert;
import com.socialtv.shop.entity.Goods;
import com.socialtv.shop.entity.Item;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.LinkedList;
import java.util.List;

import roboguice.inject.InjectView;


/**
 * 商城列表
 */
public class ShopActivity extends DialogFragmentActivity {

    private final static String ADV = "ADV";
    private final static String SHOP = "SHOP";
    private final static String WEB_VIEW = "WEB_VIEW";

    private static int PAGE = 1;
    private static int COUNT = 20;
    @Inject
    ShopGetService service;
    @Inject
    Context context;

    @InjectView(R.id.shopphoto_list_grid)
    private PullToRefreshHeadGridViewGong defineGridView;
    @InjectView(R.id.pb_loading)
    private View pbLoading;

    @Inject
    private BannerAdapter bannerAdapter;

    private int width;
    private ShopAdapter shopAdapter;
    private ViewPager pager;
    private TextView bannerTextTitle;
    private CirclePageIndicator indicator;
    private TextView bannerGoodsPrice;
    private int currentItem = 0;
    private int id;

    private final Handler timerHandler = new Handler();
    private boolean isRefresh = false;
    private boolean isRefreshShop = false;
    private boolean onLastFresh = false;
    private String isLoadFinish;
    private boolean isShowToast;
    private PullToRefreshBase.OnRefreshListener<GridView> mRefreshListener = new PullToRefreshBase.OnRefreshListener<GridView>() {
        @Override
        public void onRefresh(PullToRefreshBase<GridView> refreshView) {
            currentItem = pager.getCurrentItem();
            timerHandler.removeCallbacks(timerRunnable);
            isRefresh = true;
            isLoadFinish = null;
            isRefreshShop = true;
            isShowToast = false;
            PAGE = 1;
            downLoadBannerInfoemation();
            downLoadGoodsInfoemation();

        }
    };
    private AdapterView.OnItemClickListener mGridItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Item item = listItem.get(position);
            Refer refer = item.getRefer();
            if (item != null && refer != null) {
                if (ADV.equals(item.getType())) {
                    if (WEB_VIEW.equals(refer.getOpenType())) {
                        if (refer.getRequireLogin() == 1 && !LoginUtil.isLogin(context)) {
                            startActivity(new Intents(context, LoginActivity.class).toIntent());
                        } else {
                            startActivity(new Intents(context, WebViewActivity.class).add(IConstant.URL, refer.getUrl())
                                    .add(IConstant.TITLE, refer.getTitle())
                                    .add(IConstant.HIDE_STATUS, refer.getHideStatus())
                                    .add(IConstant.HIDE_TITLE, refer.getHideTitle()).toIntent());
                        }
                    } else {
                        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(refer.getUrl()));
                        startActivity(mIntent);
                    }
                } else if (SHOP.equals(item.getType())) {
                    ShopActivity.this.startActivity(new Intent(context, ShopDetailsActivity.class).putExtra(IConstant.SHOP_ID,
                            String.valueOf(listItem.get(position).getRefer().getId())));
                }
            }
        }
    };
    private List<Item> listItem = new LinkedList<Item>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_layout);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bdg_darktitbar_tomato));
        getSupportActionBar().setLogo(R.drawable.btn_roundback_tomato);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示标题
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);

        addBannerView();
        shopAdapter = new ShopAdapter(ShopActivity.this);
        defineGridView.setAdapter(shopAdapter);
        defineGridView.setOnItemClickListener(mGridItemClickListener);
        defineGridView.setOnRefreshListener(mRefreshListener);
        defineGridView.setOnLastItemVisibleListener(mLastItemVisibleListener);

        downLoadBannerInfoemation();
        downLoadGoodsInfoemation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
        isRefresh = false;
        isRefreshShop = false;
        onLastFresh = false;
        isLoadFinish = null;
        isShowToast = false;
        PAGE = 1;
    }

    private void addBannerView() {
        View headerview = View.inflate(this, R.layout.shop_header, null);

        ViewFinder finder = new ViewFinder(headerview);
        pager = finder.find(R.id.shop_header_pager);
        pager.setAdapter(bannerAdapter);
        bannerTextTitle = finder.textView(R.id.banner_goods_title);
        bannerGoodsPrice = finder.textView(R.id.banner_goods_price);
        indicator = finder.find(R.id.banner_indicator);
        indicator.setViewPager(pager);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, (int) (width * 0.7));
        pager.setLayoutParams(params);
        defineGridView.getGridView().addHeaderView(headerview);
    }

    private void downLoadBannerInfoemation() {
        new AbstractRoboAsyncTask<AdVert>(context) {
            @Override
            protected AdVert run(Object data) throws Exception {
                return (AdVert) HttpUtils.doRequest(service.createAdVertRequest()).result;
            }

            @Override
            protected void onSuccess(AdVert advert) throws Exception {
                super.onSuccess(advert);
                if (advert != null) {
                    if (advert.getResponseCode() == 0) {
                        if (isRefresh) {
                            isRefresh = false;
                        }
                        if (advert.getBanners() != null && !advert.getBanners().isEmpty()) {
                            bannerTextTitle.setText(advert.getBanners().get(0).getName());
                            bannerGoodsPrice.setText(advert.getBanners().get(0).getRefer().getPrice());
                            setBannerConent(advert.getBanners());
                        }
                    } else {
                        ToastUtils.show(ShopActivity.this, advert.getResponseMessage());
                    }
                }
            }
        }.execute();

    }


    private void setBannerConent(final List<Banner> banners) {
        bannerAdapter.setItems(banners);
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.postDelayed(timerRunnable, 4000);
        indicator.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bannerTextTitle.setText(banners.get(position).getName());
                String type = banners.get(position).getType();
                if (type.equals("SHOP")) {
                    bannerGoodsPrice.setVisibility(View.VISIBLE);
                } else {
                    bannerGoodsPrice.setVisibility(View.INVISIBLE);
                }
                bannerGoodsPrice.setText(banners.get(position).getRefer().getPrice());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pager.setCurrentItem(currentItem);
    }

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            currentItem = (pager.getCurrentItem() + 1) % pager.getAdapter().getCount();
            handler.sendEmptyMessage(currentItem);
            timerHandler.postDelayed(this, 4000);
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pager.setCurrentItem(msg.what, true);
        }
    };

    private void downLoadGoodsInfoemation() {
        new AbstractRoboAsyncTask<Goods>(context) {
            @Override
            protected Goods run(Object data) throws Exception {
                return (Goods) HttpUtils.doRequest(service.createGoodsRequest(PAGE, COUNT)).result;
            }

            @Override
            protected void onSuccess(Goods goods) throws Exception {
                super.onSuccess(goods);
                if (goods != null) {
                    if (View.VISIBLE == pbLoading.getVisibility()) {
                        pbLoading.setVisibility(View.GONE);
                    }
                    if (goods.getResponseCode() == 0) {
                        if (isRefreshShop) {
                            defineGridView.onRefreshComplete();
                            isRefreshShop = false;
                            listItem.clear();
                            if (!shopAdapter.isEmpty()) {
                                shopAdapter.clear();
                            }

                        }
                        if (onLastFresh) {
                            if (goods.getItems().size() < COUNT) {
                                isLoadFinish = IConstant.HASLOADFINISH;
                            }
                            onLastFresh = false;
                        }
                        if (goods.getItems().size() > 0) {

                            listItem.addAll(goods.getItems());
                        }
                        for (Item item : goods.getItems()) {
                            shopAdapter.addItemObject(item);
                        }
                    } else {
                        ToastUtils.show(ShopActivity.this, goods.getResponseMessage());
                    }
                }
            }
        }.execute();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    PullToRefreshBase.OnLastItemVisibleListener mLastItemVisibleListener = new PullToRefreshBase.OnLastItemVisibleListener() {
        @Override
        public void onLastItemVisible() {
            onLastFresh = true;
            PAGE = PAGE + 1;
            if (IConstant.HASLOADFINISH.equals(isLoadFinish)) {
                if (!isShowToast) {
                    isShowToast = true;
                }
                return;
            }
            downLoadGoodsInfoemation();
        }
    };
}
