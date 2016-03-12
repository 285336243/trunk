package com.mzs.guaji.offical;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.mzs.guaji.R;
import com.mzs.guaji.core.FragmentProvider;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.core.SingleListActivity;
import com.mzs.guaji.offical.entity.OfficialTvCircleTopic;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.ui.LoginActivity;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.IConstant;
import com.mzs.guaji.util.LoginUtil;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 13-12-27.
 * 官方圈子话题UI
 */
public class OfficialTvCircleTopicActivity extends SingleListActivity<OfficialTvCircleTopic> {

    @Inject
    Context context;

    @InjectView(R.id.official_topic_back)
    private View backView;

    @InjectView(R.id.official_new_topic)
    private View newTopicView;

    @InjectExtra("groupName")
    private String groupName;

    @InjectExtra("tvcircleId")
    private long tvCircleId;

    @Inject
    private OfficialTopicService service;

    private boolean hasMore = false;

    private List<Topic> topics = new ArrayList<Topic>();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        newTopicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtil.isLogin(context)) {
                    Intent mIntent = new Intent(context, OfficialTvCircleSubmitTopicActivity.class);
                    mIntent.putExtra("groupName", groupName);
                    mIntent.putExtra("tvcircleId", tvCircleId);
                    startActivityForResult(mIntent, 0);
                } else {
                    Intent mIntent = new Intent(context, LoginActivity.class);
                    startActivity(mIntent);
                }
            }
        });
        listView.setDivider(null);
        setEmptyText(R.string.topic_empty);
        IntentFilter filter = new IntentFilter(BroadcastActionUtil.DELETE_TOPIC);
        registerReceiver(deleteTopicReceiver, filter);

        final SharedPreferences preferences = getSharedPreferences(IConstant.GUIDE, Context.MODE_PRIVATE);
        final int guide = preferences.getInt(IConstant.TOPIC_GUIDE, 0);
        if (guide == 0) {
            final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.topic_guide);
            View v = dialog.findViewById(R.id.topic_guide_root);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    final SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(IConstant.TOPIC_GUIDE, 1).commit();
                }
            });
            if (!dialog.isShowing())
                dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            forceRefresh();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.official_circle_topic_layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(deleteTopicReceiver);
        topics.clear();
        topics = null;
    }

    /**
     * Create ResourcePager
     *
     * @return
     */
    @Override
    protected ResourcePager<OfficialTvCircleTopic> createPager() {
        return new ResourcePager<OfficialTvCircleTopic>() {
            @Override
            public PageIterator<OfficialTvCircleTopic> createIterator(int page, int count) {
                return service.pageTopic(tvCircleId, requestPage, requestCount);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<OfficialTvCircleTopic> loader, OfficialTvCircleTopic data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getTopics() != null && data.getTopics().size() > 0) {
                this.items = data.getTopics();
                topics.addAll(data.getTopics());
                getListAdapter().getWrappedAdapter().setItems(topics);
            } else {
                hasMore = true;
                if (this.items.isEmpty())
                    loadingIndicator.setVisible(false);
                else
                    loadingIndicator.loadingAllFinish();
            }
        }
        showList();
    }

    BroadcastReceiver deleteTopicReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(BroadcastActionUtil.DELETE_TOPIC)) {
                forceRefresh();
            }
        }
    };

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        topics.clear();
        requestPage = 1;
        forceRefresh();
    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        if (hasMore)
            return;
        if (getSupportLoaderManager().hasRunningLoaders())
            return;
        requestPage++;
        forceRefresh();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Topic topic = (Topic) l.getItemAtPosition(position);
        Intent mIntent = new Intent(this, TopicDetailsActivity.class);
        if (topic != null) {
            mIntent.putExtra("topicId", topic.getId());
        }
        startActivity(mIntent);
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.topic_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.loading_topic;
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        return new OfficialTvCircleTopicAdapter(this);
    }

    /**
     * Is add adapter header
     *
     * @return
     */
    @Override
    protected boolean isAddAdapterHeader() {
        return false;
    }

    /**
     * Adapter header view
     *
     * @return
     */
    @Override
    protected View adapterHeaderView() {
        return null;
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
}
