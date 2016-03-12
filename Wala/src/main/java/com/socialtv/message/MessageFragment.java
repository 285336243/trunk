package com.socialtv.message;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.Intents;
import com.socialtv.core.MultiTypeRefreshListFragment;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.SingleTypeRefreshListFragment;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.http.HttpUtils;
import com.socialtv.message.entity.Message;
import com.socialtv.message.entity.MessageItem;
import com.socialtv.topic.ReplyActivity;
import com.socialtv.topic.TopicDetailActivity;
import com.socialtv.util.IConstant;
import com.socialtv.util.NetWorkUtil;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-2.
 * 消息的Fragment
 */
public class MessageFragment extends MultiTypeRefreshListFragment<Message> implements HomeMessageFragment.OnVisibleRefreshListener {

    @Inject
    private MessageServices services;

    private String cusorId = "0";

    private Dialog replyDialog;

    private TextView replyView;

    private View lookOverTopicView;

    private Dialog deleteDialog;

    private TextView deleteTextView;

    @Inject
    private MessageAdapter adapter;

    private boolean isDelete = false;

    private List<MessageItem> messageItemList = new ArrayList<MessageItem>();

    private final List<MessageItem> messageItems = new ArrayList<MessageItem>();

    private LoginBroadcastReceiver receiver;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setDividerHeight(Utils.dip2px(getActivity(), 1));
        loadingIndicator.setVisible(false);
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
        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, Utils.dip2px(getActivity(), 48)));
        textView.setText("忽略全部未读消息");
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(R.color.grey));
        textView.setBackgroundColor(getResources().getColor(R.color.transparent));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.show(getActivity(), "已忽略全部未读消息");
                int count = adapter.getCount();
                for (int i = 0; i < count; i++) {
                    MessageItem item = (MessageItem) adapter.getItem(i);
                    item.setStatus(1);
                }
                adapter.notifyDataSetChanged();

                new AbstractRoboAsyncTask<Response>(getActivity()){

                    @Override
                    protected Response run(Object data) throws Exception {
                        MessageItem item = (MessageItem) adapter.getItem(0);
                        return (Response) HttpUtils.doRequest(services.createReadAllMessageRequest(item.getId())).result;
                    }
                }.execute();
            }
        });
        return textView;
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected MultiTypeAdapter createAdapter() {
        return adapter;
    }

    /**
     * Create Request
     *
     * @return
     */
    @Override
    protected Request<Message> createRequest() {
        return services.createMessageRequest(cusorId, requestCount);
    }

    @Override
    public void onLoadFinished(Loader<Message> loader, Message data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (isRefresh) {
                    this.items.clear();
                    adapter.clear();
                    isRefresh = false;
                }
                if (isDelete) {
                    this.items.clear();
                    adapter.clear();
                    isDelete = false;
                }
                cusorId = data.getCusorId();
                if (data.getMessages() != null && !data.getMessages().isEmpty()) {
                    isDelete = false;
                    this.items = data.getMessages();
//                    messageItemList.addAll(data.getMessages());
//                    getListAdapter().getWrappedAdapter().setItems(messageItemList);
                    adapter.addItems(data.getMessages(), "messages");
                } else {
                    hasMore = true;
                    if (!this.items.isEmpty() && !isDelete) {
                        loadingIndicator.loadingAllFinish();
                    } else {
                        MessageItem item = new MessageItem();
                        if (messageItems.size() < 1)
                            messageItems.add(item);
                        this.items = messageItems;
                        adapter.addItems(messageItems, "empty");
                    }
                }
            }
        }
        showList();
    }

    /**
     * 当选中tab的消息页面是刷新调用的
     */
    public void selectedMessageRefresh() {
        isRefresh = true;
        requestPage = 1;
        refresh();
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.message_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.message_loading;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (!isUsable())
            return;
        try {
            final MessageItem item = (MessageItem) l.getItemAtPosition(position);
            if (replyDialog == null) {
                replyDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                replyDialog.setContentView(R.layout.message_reply_dialog);
                replyView = (TextView) replyDialog.findViewById(R.id.message_reply_reply_topic);
                lookOverTopicView = replyDialog.findViewById(R.id.message_reply_look_over_topic);
            }
            replyView.setText("回复 " + item.getRefer().getPost().getCreateUser().getNickname());
            replyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (replyDialog.isShowing())
                        replyDialog.dismiss();
                    startActivity(new Intents(getActivity(), ReplyActivity.class).add(IConstant.TOPIC_ID, item.getRefer().getPost().getId())
                            .add(IConstant.USER_NAME, item.getRefer().getPost().getCreateUser().getNickname()).toIntent());
                }
            });
            lookOverTopicView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (replyDialog.isShowing())
                        replyDialog.dismiss();
                    if (item != null && item.getRefer() != null && item.getRefer().getTopic() != null && !TextUtils.isEmpty(item.getRefer().getTopic().getId()))
                        startActivity(new Intents(getActivity(), TopicDetailActivity.class).add(IConstant.TOPIC_ID, item.getRefer().getTopic().getId()).toIntent());
                }
            });
            if (!replyDialog.isShowing())
                replyDialog.show();
            item.setStatus(1);
            getListAdapter().getWrappedAdapter().notifyDataSetChanged();

            new AbstractRoboAsyncTask<Response>(getActivity()){
                @Override
                protected Response run(Object data) throws Exception {
                    return (Response) HttpUtils.doRequest(services.createSingleReadMessageRequest(item.getId())).result;
                }
            }.execute();
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onListItemLongClick(ListView l, View v, final int position, long id) {
        if (!isUsable())
            return false;
        try {
            final MessageItem item = (MessageItem) l.getItemAtPosition(position);
            if (deleteDialog == null) {
                deleteDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                deleteDialog.setContentView(R.layout.delete_message_dialog);
                deleteTextView = (TextView) deleteDialog.findViewById(R.id.delete_message_dialog_text);
                deleteTextView.setText("删除该条消息");
            }
            if (!deleteDialog.isShowing())
                deleteDialog.show();
            deleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteDialog.isShowing())
                        deleteDialog.dismiss();
                    new ProgressDialogTask<Response>(getActivity()) {
                        @Override
                        protected Response run(Object data) throws Exception {
                            return (Response) HttpUtils.doRequest(services.createDeleteMessageRequest(item.getId())).result;
                        }

                        @Override
                        protected void onSuccess(Response response) throws Exception {
                            super.onSuccess(response);
                            if (response != null) {
                                if (response.getResponseCode() == 0) {
                                    isDelete = true;
                                    cusorId = "0";
                                    refresh();
                                }
                            }
                        }
                    }.start("正在删除该消息");
                }
            });
        } catch (Throwable e) {

        }
        return true;
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        if (NetWorkUtil.isNetworkConnected(getActivity())) {
            this.items.clear();
            this.messageItemList.clear();
        }
        cusorId = "0";
        isRefresh = true;
        hasMore = false;
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.messageItemList.clear();
        this.messageItemList = null;
        if (receiver != null)
            getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        HomeMessageFragment messageFragment = (HomeMessageFragment) getParentFragment();
        if (messageFragment != null) {
            messageFragment.setOnVisibleRefreshMessageListener(this);
        }
        receiver = new LoginBroadcastReceiver();
        activity.registerReceiver(receiver, new IntentFilter(IConstant.USER_LOGIN));
    }

    @Override
    public void onVisibleRefresh() {
        cusorId = "0";
        isRefresh = true;
        hasMore = false;
        refresh();
    }

    private class LoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            isDelete = true;
            isRefresh = false;
            hasMore = false;
            requestPage = 1;
            messageItemList.clear();
            messageItems.clear();
            items.clear();
            refresh();
        }
    }
}
