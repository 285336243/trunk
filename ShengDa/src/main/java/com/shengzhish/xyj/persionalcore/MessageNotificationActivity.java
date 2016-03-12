package com.shengzhish.xyj.persionalcore;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.FragmentProvider;
import com.shengzhish.xyj.core.SingleListActivity;
import com.shengzhish.xyj.persionalcore.entity.MessageNotification;

/**
 * Created by wlanjie on 14-6-18.
 */
public class MessageNotificationActivity extends SingleListActivity<MessageNotification> {

    @Inject
    private MessageNotificationServices services;

    private boolean hasMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEmptyText(R.string.message_notification_empty);
        ((TextView) findViewById(R.id.tv_title)).setText("消息通知");
        listView.setDivider(null);
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        return new MessageNotificationAdapter(this);
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
     * Create Request
     *
     * @return
     */
    @Override
    protected Request<MessageNotification> createRequest() {
        return services.createMessageNotificationRequest(requestPage, requestCount);
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.message_notification_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.message_notification_loading;
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
    public void onLoadFinished(Loader<MessageNotification> loader, MessageNotification data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (data.getMessages() != null && data.getMessages().size() > 0) {
                    this.items = data.getMessages();
                    getListAdapter().getWrappedAdapter().setItems(data.getMessages());
                    if (data.getMessages().size() < requestCount) {
                        loadingIndicator.setVisible(false);
                    }
                } else {
                    hasMore = true;
                    if (!this.items.isEmpty()) {
                        loadingIndicator.loadingAllFinish();
                    } else {
                        loadingIndicator.setVisible(false);
                    }
                }
            }
        }
        showList();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        requestPage = 1;
        refresh();
    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        if (hasMore)
            return;
        if (getSupportLoaderManager().hasRunningLoaders())
            return;
        requestPage++;
        refresh();
    }
}
