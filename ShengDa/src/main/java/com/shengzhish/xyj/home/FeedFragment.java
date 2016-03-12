package com.shengzhish.xyj.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.Intents;
import com.shengzhish.xyj.core.Log;
import com.shengzhish.xyj.core.SingleTypeRefreshListFragment;
import com.shengzhish.xyj.core.ViewPager;
import com.shengzhish.xyj.gallery.GalleryActivity;
import com.shengzhish.xyj.home.entity.Feed;
import com.shengzhish.xyj.home.entity.FeedItem;
import com.shengzhish.xyj.news.NewsActivity;
import com.shengzhish.xyj.util.CacheDataKeeper;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.NetWorkConnect;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wlanjie on 14-5-30.
 */
public class FeedFragment extends SingleTypeRefreshListFragment<Feed> {

    private int width;

    private TextView bannerText;

    private ViewPager viewPager;

    private CirclePageIndicator indicator;

    private final Handler timerHandler = new Handler();

    private int currentItem = 0;

    private BannerAdapter bannerAdapter;

    @Inject
    private HomeServices services;

    private boolean hasMore = false;
    private Gson gson;
    private boolean isCatchData;
    private boolean isFirstData = true;
    private List<FeedItem> listItem=new LinkedList<FeedItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        titleView.setText("动态");
        listView.setDivider(null);
        gson = new Gson();
        isCatchData = CacheDataKeeper.isCatchDynamicResponse(getActivity());
        if (isCatchData) {
            String json = CacheDataKeeper.getDynamicResponse(getActivity());
//            com.shengzhish.xyj.core.Log.v("person", "取出 json ==" + json);
            Feed data = gson.fromJson(json, Feed.class);
            setDataContent(data,true);
        }
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        return new FeedAdapter(getActivity());
    }

    /**
     * Is add adapter header
     *
     * @return
     */
    @Override
    protected boolean isAddAdapterHeader() {
        return true;
    }

    /**
     * Adapter header view
     *
     * @return
     */
    @Override
    protected View adapterHeaderView() {
        View headerView = getLayoutInflater(null).inflate(R.layout.home_header, null);
        double height = width * 0.56;
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, (int) height);
        headerView.setLayoutParams(params);
        viewPager = (ViewPager) headerView.findViewById(R.id.header_pager);
        bannerAdapter = new BannerAdapter(getActivity());
        viewPager.setAdapter(bannerAdapter);
        bannerText = (TextView) headerView.findViewById(R.id.header_title);
        indicator = (CirclePageIndicator) headerView.findViewById(R.id.cp_home_banner_indicator);
        indicator.setViewPager(viewPager);
        return headerView;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(msg.what, true);
        }
    };

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            currentItem = (viewPager.getCurrentItem() + 1) % viewPager.getAdapter().getCount();
            handler.sendEmptyMessage(currentItem);
            timerHandler.postDelayed(this, 4000);
        }
    };

    private void setBannerConent(final List<FeedItem> banners) {
        bannerAdapter.setItems(banners);
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.postDelayed(timerRunnable, 4000);
        indicator.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bannerText.setText(banners.get(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(currentItem);
    }

    @Override
    public void onLoadFinished(Loader<Feed> listLoader, Feed data) {
        super.onLoadFinished(listLoader, data);
        if (data != null && isFirstData) {
            if (CacheDataKeeper.isCatchDynamicResponse(getActivity())) {
                CacheDataKeeper.removeDynamicCacheData(getActivity());
            }
            String json = gson.toJson(data);
            CacheDataKeeper.saveDynamicResponse(getActivity(), json);
//            com.shengzhish.xyj.core.Log.v("person", "存入 json ==" + json);
        }
        if (NetWorkConnect.isConnect(getActivity()) && data != null) {
            setDataContent(data,false);
        }
    }

    private void setDataContent(Feed data, boolean isCatch) {
//        Log.v("person","data = "+data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (data.getBanners() != null && data.getBanners().size() > 0) {
                    bannerText.setText(data.getBanners().get(0).getTitle());
                    setBannerConent(data.getBanners());
                }
                if (data.getFeeds() != null && data.getFeeds().size() > 0) {
                    this.items = data.getFeeds();
                    if(isCatch){
                        getListAdapter().getWrappedAdapter().setItems(data.getFeeds());
                    }else {
                        listItem.addAll(data.getFeeds());
                        getListAdapter().getWrappedAdapter().setItems(listItem);
                    }
                    if (data.getFeeds().size() < requestCount) {
                        loadingIndicator.setVisible(false);
                    }
                } else {
                    hasMore = true;
                    if (!this.items.isEmpty())
                        loadingIndicator.loadingAllFinish();
                    else
                        loadingIndicator.setVisible(false);
                }
            }
        }
        showList();
    }


    protected Request<Feed> createRequest() {
    /*    if (isCatchData&&isFirstData)
            return null;
        else*/
        return services.pageFeed(requestPage, requestCount);
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        if (isCatchData)
            return IConstant.CACHE;
        else
            return R.string.feed_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.feed_loading;
    }

    /**
     * Called when the user has scrolled to the end of the list
     */
    @Override
    public void onLastItemVisible() {
        if (hasMore)
            return;
        if (getLoaderManager().hasRunningLoaders())
            return;
        requestPage++;
        isFirstData = false;
        refresh();
    }

    /**
     * onRefresh will be called for both a Pull from start, and Pull from
     * end
     *
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        currentItem = viewPager.getCurrentItem();
        timerHandler.removeCallbacks(timerRunnable);
        isFirstData = true;
        if(listItem!=null&&!listItem.isEmpty()){
            listItem.clear();
        }
        refresh();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FeedItem item = (FeedItem) l.getItemAtPosition(position);
        if ("gallery".equals(item.getType())) {
            startActivity(new Intents(getActivity(), GalleryActivity.class).add(IConstant.GALLERY_ID, item.getReferId()).toIntent());
        } else {
            startActivity(new Intents(getActivity(), NewsActivity.class).add(IConstant.NEWS_EXTRA, String.format(IConstant.NEWS_URL, item.getReferId())).add(IConstant.NEWS_ID, item.getReferId()).toIntent());
        }
    }
}
