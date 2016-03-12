package com.mzs.guaji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.kevinsawicki.wishlist.ViewFinder;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.TopicHomeAdapter;
import com.mzs.guaji.core.SingleListActivity;
import com.mzs.guaji.core.FragmentProvider;
import com.mzs.guaji.core.Intents;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.core.ThrowableLoader;
import com.mzs.guaji.engine.TopicHomeService;
import com.mzs.guaji.entity.ActivityTopic;
import com.mzs.guaji.entity.ActivityTopicItem;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class TopicHomeActivity extends SingleListActivity<ActivityTopic> implements TabHost.OnTabChangeListener, TabHost.TabContentFactory {

    @InjectExtra(value = "topic_home_image", optional = true)
    private String imageUrl;

    @InjectExtra("activityId")
    private long activityId;

    private TabHost host;

    @Inject
    private TopicHomeAdapter adapter;

    @Inject
    private TopicHomeService service;

    @InjectView(R.id.topic_home_back)
    private LinearLayout backLayout;

    private int newPage = 1;

    private int hotPage = 1;

    private ArrayList<ActivityTopicItem> list = new ArrayList<ActivityTopicItem>();

    private ArrayList<ActivityTopicItem> hotList = new ArrayList<ActivityTopicItem>();

    private boolean emptyPage = false;

    private boolean hotEmptyPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getListView().setDivider(null);
        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (host.getCurrentTab() == 0) {
                    emptyPage = false;
                    newPage = 1;
                    list.clear();
                    forceRefresh();
                } else {
                    hotEmptyPage = false;
                    hotPage = 1;
                    hotList.clear();
                    getSupportLoaderManager().restartLoader(1, null, callbacks);
                }
            }
        });
        refreshListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (getSupportLoaderManager().hasRunningLoaders()) {
                    return;
                }

                if (host.getCurrentTab() == 0) {
                    if (emptyPage) {
                        return;
                    }
                    loadingIndicator.setVisible(true);
                    newPage = newPage + 1;
                    forceRefresh();
                } else {
                    if (hotEmptyPage) {
                        return;
                    }
                    loadingIndicator.setVisible(true);
                    hotPage = hotPage + 1;
                    getSupportLoaderManager().restartLoader(1, null, callbacks);
                }
            }
        });
    }

    /**
     * Create ResourcePager
     *
     * @return
     */
    @Override
    protected ResourcePager<ActivityTopic> createPager() {
        return new ResourcePager<ActivityTopic>() {
            @Override
            protected Object getId(ActivityTopic resource) {
                return resource.getPage();
            }

            @Override
            public PageIterator<ActivityTopic> createIterator(int page, int size) {
                return service.createTopicHome(activityId, newPage, requestCount);
            }
        };
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.loading_empty;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.loading_empty;
    }

    /**
     * Create adapter to display items
     *
     * @param items@return adapter
     */
    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        return adapter;
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
        DisplayMetrics mMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        int width = mMetrics.widthPixels;
        View v = getLayoutInflater().inflate(R.layout.topic_home_head_layout, null);
        ViewFinder finder = new ViewFinder(v);
        host = finder.find(R.id.th_tabs);
        host.setup();
        host.setOnTabChangedListener(this);
        createTabs();
        RelativeLayout bannerLayout = finder.find(R.id.rl_topic_home_banner_layout);
        bannerLayout.setLayoutParams(new LinearLayout.LayoutParams(width, width / 2));
        ImageView bannerImage = finder.find(R.id.iv_topic_home_banner);
        ImageLoader.getInstance().displayImage(imageUrl, bannerImage, ImageUtils.imageLoader(this, 0));
        RelativeLayout participationLayout = finder.find(R.id.rl_topic_home_participation_layout);
        participationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtil.isLogin(TopicHomeActivity.this)) {
                    startActivityForResult(new Intents(TopicHomeActivity.this, TopicHomeSubmitActivity.class).add("activity_id", activityId).toIntent(), 0);
                } else {
                    startActivity(new Intents(TopicHomeActivity.this, LoginActivity.class).toIntent());
                }
            }
        });
        return v;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            list.clear();
            forceRefresh();
        }
    }

    @Override
    public void onLoadFinished(Loader<ActivityTopic> loader, ActivityTopic data) {
        super.onLoadFinished(loader, data);
        refreshListView.onRefreshComplete();
        loadingIndicator.setVisible(false);
        hide(progressBar);
        if (data != null) {
            if (data.getTopics() != null && data.getTopics().size() > 0) {
                this.items = data.getTopics();
                list.addAll(data.getTopics());
                getListAdapter().getWrappedAdapter().setItems(list);
            } else {
                if (list.isEmpty()) {
                    emptyPage = true;
                    if (list.isEmpty()) {
                        List<ActivityTopicItem> emptyList = new ArrayList<ActivityTopicItem>();
                        emptyList.add(new ActivityTopicItem().setEmptyText("暂无任何内容"));
                        this.items = emptyList;
                        getListAdapter().getWrappedAdapter().setItems(emptyList);
                    }
                } else {
                    getListAdapter().getWrappedAdapter().setItems(list);
                }
            }
        }
        showList();
    }

    /**
     * Get provider of the currently selected fragment
     *
     * @return fragment provider
     */
    @Override
    protected FragmentProvider getProvider() {
        return null;
    }

    @Override
    protected int getContentView() {
        return R.layout.topic_home_layout;
    }

    /**
     * Create tab using information from current adapter
     * <p>
     * This can be called when the tabs changed but must be called after an
     */
    protected void createTabs() {
        if (host.getTabWidget().getTabCount() > 0) {
            host.setCurrentTab(0);
            host.clearAllTabs();
        }

        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < 2; i++) {
            TabHost.TabSpec spec = host.newTabSpec("tab" + i);
            spec.setContent(this);
            View view = inflater.inflate(R.layout.topic_home_tab, null);
            ((TextView) view.findViewById(R.id.topic_home_tab_text)).setText(getTitle(i));

            spec.setIndicator(view);
            host.addTab(spec);

            int background;
            if (i == 0)
                background = R.drawable.topic_home_selector;
            else if (i == 1)
                background = R.drawable.topic_home_hot_selector;
            else
                background = R.drawable.topic_home_hot_selector;
            ((ImageView) view.findViewById(R.id.topic_home_tab_image))
                    .setImageResource(background);
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        host.setCurrentTab(host.getCurrentTab());
//        show(progressBar);
        if (host.getCurrentTab() == 0) {
            adapter.setItems(list);
//                forceRefresh();
        } else {
            if (hotList.isEmpty()) {
                getSupportLoaderManager().initLoader(1, null, callbacks);
            } else {
//                getSupportLoaderManager().restartLoader(1, null, callbacks);
                adapter.setItems(hotList);
            }
        }
    }

    LoaderManager.LoaderCallbacks<ActivityTopic> callbacks = new LoaderManager.LoaderCallbacks<ActivityTopic>() {
        @Override
        public Loader<ActivityTopic> onCreateLoader(int id, Bundle args) {
            return new ThrowableLoader<ActivityTopic>(TopicHomeActivity.this, null) {
                @Override
                public ActivityTopic loadData() throws Exception {
                    ResourcePager<ActivityTopic> pager = new ResourcePager<ActivityTopic>() {

                        @Override
                        protected Object getId(ActivityTopic resource) {
                            return null;
                        }

                        @Override
                        public PageIterator<ActivityTopic> createIterator(int page, int size) {
                            return service.createTopicHomeHot(activityId, hotPage, requestCount);
                        }
                    };
                    pager.next();
                    return pager.get();
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ActivityTopic> loader, ActivityTopic data) {
            refreshListView.onRefreshComplete();
            loadingIndicator.setVisible(false);
            hide(progressBar);
            if (data != null) {
                if (data.getTopics() != null && data.getTopics().size() > 0) {
                    hotList.addAll(data.getTopics());
                    adapter.setItems(hotList);
                } else {
                    if (hotList.isEmpty()) {
                        hotEmptyPage = true;
                        if (hotList.isEmpty()) {
                            List<ActivityTopicItem> emptyList = new ArrayList<ActivityTopicItem>();
                            emptyList.add(new ActivityTopicItem().setEmptyText("暂无任何内容"));
                            adapter.setItems(emptyList);
                        }
                    } else {
                        adapter.setItems(hotList);
                    }
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<ActivityTopic> loader) {

        }
    };

    /**
     * Callback to make the tab contents
     *
     * @param tag Which tab was selected.
     * @return The view to display the contents of the selected tab.
     */
    @Override
    public View createTabContent(String tag) {
        return ViewUtils.setGone(new View(getApplication()), true);
    }

    private String getTitle(final int position) {
        if (position == 0) {
            return "最新";
        } else {
            return "热门";
        }
    }

    protected String getIcon(final int position) {
        return null;
    }

    protected int getTabIcon(final int position) {
        return 0;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (!list.isEmpty() && host.getCurrentTab() == 0) {
            ActivityTopicItem item = (ActivityTopicItem) l.getItemAtPosition(position);
            startActivity(new Intents(this, TopicDetailsActivity.class).add("topicId", item.getId()).add("type", "activity").toIntent());
        }

        if (!hotList.isEmpty() && host.getCurrentTab() == 1) {
            ActivityTopicItem item = (ActivityTopicItem) l.getItemAtPosition(position);
            startActivity(new Intents(this, TopicDetailsActivity.class).add("topicId", item.getId()).add("type", "activity").toIntent());
        }
    }

}
