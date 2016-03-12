package com.mzs.guaji.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.GsonUtils;
import com.android.volley.PagedRequest;
import com.android.volley.Response;
import com.android.volley.SynchronizationHttpRequest;
import com.android.volley.VolleyError;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.MessageAdapter;
import com.mzs.guaji.core.AbstractRoboAsyncTask;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ProgressDialogTask;
import com.mzs.guaji.core.RequestUtils;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.core.SingleTypeRefreshListFragment;
import com.mzs.guaji.entity.Badges;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.Message;
import com.mzs.guaji.entity.MessageActivityTopicPost;
import com.mzs.guaji.entity.MessageCelebrityTopicPost;
import com.mzs.guaji.entity.MessageList;
import com.mzs.guaji.entity.MessageTopicPost;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.ui.MessageActivity;
import com.mzs.guaji.ui.TopicReplyActivity;
import com.mzs.guaji.util.SkipPersonalCenterUtil;
import com.mzs.guaji.util.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wlanjie on 14-1-20.
 */
public class MessageFragment extends SingleTypeRefreshListFragment<Message> {

    private MessageAdapter adapter;

    private List<Long> selects = new ArrayList<Long>();

    public RelativeLayout deleteMessageParent;

    private LinearLayout deleteMessage;

    private long position = 0;

    private boolean isFirst = true;

    private List<MessageList> messageLists;

    private int requestCount = 20;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(R.string.message_empty);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deleteMessageParent = (RelativeLayout) view.findViewById(R.id.rl_delete_message_parent);
        deleteMessage = (LinearLayout) view.findViewById(R.id.ll_msg_del);
        refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @Override
    protected ResourcePager createPager() {
        return new ResourcePager() {
            @Override
            protected Object getId(Object resource) {
                return null;
            }

            @Override
            public PageIterator createIterator(int page, int size) {
                PagedRequest request = new PagedRequest();
                request.setUri(getMessageRequest(position, requestCount));
                request.setClazz(Message.class);
                return new PageIterator(getActivity(), request);
            }
        };
    }

    @Override
    protected int getLoadingMessage() {
        return  R.string.message_loading;
    }

    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        adapter = new MessageAdapter(getActivity(), items);
        return adapter;
    }

    @Override
    protected int getContentView() {
        return R.layout.message_list;
    }

    @Override
    public void onLoadFinished(Loader<Message> loader, Message es) {
        super.onLoadFinished(loader, es);
        if (es != null && es.getMessages() != null) {
            if (es.getMessages().size() == 0) {
                loadingIndicator.setVisible(false);
                return;
            }
            if (es.getMessages().size() < requestCount) {
                loadingIndicator.setVisible(false);
            }
            if (es.getMessages().size() > 0) {
                MessageList messageList = es.getMessages().get(es.getMessages().size() - 1);
                if (messageList != null) {
                    this.position = messageList.getId();
                }
                if (isFirst) {
                    this.items = es.getMessages();
                    this.messageLists = es.getMessages();
                    getListAdapter().getWrappedAdapter().setItems(es.getMessages());
                } else {
                    List<MessageList> messages = new ArrayList<MessageList>();
                    messages.addAll(this.messageLists);
                    messages.addAll(es.getMessages());
                    this.messageLists = messages;
                    this.items = messages;
                    getListAdapter().getWrappedAdapter().setItems(this.messageLists);
                }
            }
        } else {
            this.items = Collections.emptyList();
        }
        showList();
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.message_error;
    }

    private String getMessageRequest(long page, long count) {
        return "message/list.json?p=" + page + "&cnt=" + count;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (selectDeleteMessage(position - 1, v)) {
            return;
        }
        final MessageList messageList = (MessageList) items.get(position-1);
        messageRead(messageList, position - 1);
        if (messageList != null) {
            if ("TOPIC_POST".equals(messageList.getType())) {
                MessageTopicPost topicPost = GsonUtils.createGson().fromJson(messageList.getMessage(), MessageTopicPost.class);
                startMessageTopicPost(topicPost);
            } else if ("ACTIVITY_TOPIC_POST".equals(messageList.getType())) {
                MessageActivityTopicPost topicPost = GsonUtils.createGson().fromJson(messageList.getMessage(), MessageActivityTopicPost.class);
                startActivityTopicPost(topicPost);
            } else if ("Celebrity_TOPIC_POST".equals(messageList.getType())) {
                MessageCelebrityTopicPost topicPost = GsonUtils.createGson().fromJson(messageList.getMessage(), MessageCelebrityTopicPost.class);
                startCelebrityTopicPost(topicPost);
            }
        }
    }

    private void startCelebrityTopicPost(final MessageCelebrityTopicPost topicPost) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.message_dialog);
        TextView replyText = (TextView) dialog.findViewById(R.id.tv_message_dialog_reply);
        if (topicPost != null) {
            replyText.setText("回复 "+topicPost.getUserNickname());
        }
        replyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (topicPost != null) {
                    Intent intent = new Intent(getActivity(), TopicReplyActivity.class);
                    intent.putExtra("nickname", topicPost.getUserNickname());
                    intent.putExtra("commentId", topicPost.getId());
                    intent.putExtra("type", "group");
                    startActivity(intent);
                }
            }
        });
        TextView topicDetailText = (TextView) dialog.findViewById(R.id.tv_message_dialog_topic_detail);
        topicDetailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (topicPost != null && topicPost.getTopic() != null) {
                    Intent intent = new Intent(getActivity(), TopicDetailsActivity.class);
                    intent.putExtra("topicId", topicPost.getTopic().getId());
                    intent.putExtra("type", "celebrity");
                    startActivity(intent);
                }
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void startMessageTopicPost(final MessageTopicPost topicPost) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.message_dialog);
        TextView replyText = (TextView) dialog.findViewById(R.id.tv_message_dialog_reply);
        if (topicPost != null) {
            replyText.setText("回复 "+topicPost.getUserNickname());
        }
        replyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (topicPost != null) {
                    Intent intent = new Intent(getActivity(), TopicReplyActivity.class);
                    intent.putExtra("nickname", topicPost.getUserNickname());
                    intent.putExtra("commentId", topicPost.getId());
                    intent.putExtra("type", "group");
                    startActivity(intent);
                }
            }
        });
        TextView topicDetailText = (TextView) dialog.findViewById(R.id.tv_message_dialog_topic_detail);
        topicDetailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (topicPost != null && topicPost.getTopic() != null) {
                    Intent intent = new Intent(getActivity(), TopicDetailsActivity.class);
                    intent.putExtra("topicId", topicPost.getTopic().getId());
                    intent.putExtra("type", "group");
                    startActivity(intent);
                }
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void startActivityTopicPost(final MessageActivityTopicPost topicPost) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.message_dialog);
        TextView replyText = (TextView) dialog.findViewById(R.id.tv_message_dialog_reply);
        if (topicPost != null) {
            replyText.setText("回复 "+topicPost.getUserNickname());
        }
        replyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (topicPost != null) {
                    Intent intent = new Intent(getActivity(), TopicReplyActivity.class);
                    intent.putExtra("nickname", topicPost.getUserNickname());
                    intent.putExtra("commentId", topicPost.getId());
                    intent.putExtra("type", "activity");
                    startActivity(intent);
                }
            }
        });
        TextView topicDetailText = (TextView) dialog.findViewById(R.id.tv_message_dialog_topic_detail);
        topicDetailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (topicPost != null && topicPost.getTopic() != null) {
                    Intent intent = new Intent(getActivity(), TopicDetailsActivity.class);
                    intent.putExtra("topicId", topicPost.getTopic().getId());
                    intent.putExtra("type", "activity");
                    startActivity(intent);
                }
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private boolean selectDeleteMessage(int position, View v) {
        if (deleteMessageParent.getVisibility() == View.VISIBLE) {
            if (items != null && !items.isEmpty()) {
                MessageList messageList = (MessageList) items.get(position);
                if (selects.contains(messageList.getId())) {
                    selects.remove(messageList.getId());
                    adapter.setItemBackground(false, messageList,v);
                } else {
                    selects.add(messageList.getId());
                    adapter.setItemBackground(true, messageList,v);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public void clearAllDelete() {
        selects.clear();
        for (int i=0; items!= null && i<items.size(); i++) {
            MessageList list = (MessageList) items.get(i);
            list.setChecked(false);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onListItemLongClick(ListView l, View v, final int position, long id) {
        deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMessage();
            }
        });
        if (deleteMessageParent.getVisibility() != View.VISIBLE) {
            deleteMessageParent.setVisibility(View.VISIBLE);
            if (items != null && !items.isEmpty()) {
                MessageList messageList = (MessageList) items.get(position-1);
                if (selects.contains(messageList.getId())) {
                    selects.remove(messageList.getId());
                    adapter.setItemBackground(false, messageList,v);
                } else {
                    selects.add(messageList.getId());
                    adapter.setItemBackground(true, messageList,v);
                }
            }
            return true;
        }
        return false;
    }

    private void deleteMessage() {
        new ProgressDialogTask<DefaultReponse>(getActivity()){
            @Override
            protected DefaultReponse run(Object data) throws Exception {
                StringBuilder builder = new StringBuilder();
                builder.append("[");
                for (int i=0; i<selects.size(); i++) {
                    builder.append(selects.get(i));
                    if (i!=selects.size()-1) {
                        builder.append(",");
                    }
                 }
                builder.append("]");
                Map<String, String> bodys = new HashMap<String, String>();
                bodys.put("ids", builder.toString());
                SynchronizationHttpRequest<DefaultReponse> request = RequestUtils.getInstance().createPost(getActivity(), getMessageDel(), bodys, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ToastUtil.showToast(getActivity(), "提交出现错误");
                    }
                });
                request.setClazz(DefaultReponse.class);
                return request.getResponse();
            }

            @Override
            protected void onSuccess(DefaultReponse defaultReponse) throws Exception {
                super.onSuccess(defaultReponse);
                if (defaultReponse != null && defaultReponse.getResponseCode() == 0) {
                    deleteMessageParent.setVisibility(View.GONE);
                    for (int i =0; i<selects.size(); i++) {
                        MessageList message = new MessageList();
                        message.setId(selects.get(i));
                        items.remove(message);
                    }
                    selects.clear();
                    requestBadgeCount();
                    getListAdapter().getWrappedAdapter().setItems(items);
                }
            }
        }.start("正在删除消息");
    }

    private void requestBadgeCount() {
        new AbstractRoboAsyncTask<Badges>(getActivity()) {
            @Override
            protected Badges run(Object data) throws Exception {
                SynchronizationHttpRequest<Badges> request = RequestUtils.getInstance().createGet(getActivity(), getBadgesCount(), null);
                request.setClazz(Badges.class);
                return request.getResponse();
            }

            @Override
            protected void onSuccess(Badges badges) throws Exception {
                super.onSuccess(badges);
                if (badges != null) {
                    MessageActivity activity = (MessageActivity) getActivity();
                    Map<String, Integer> badgesCounts = badges.getBages();
                    if (badgesCounts != null) {
                        activity.msgCount = badgesCounts.get("msg");
                        activity.letterCount = badgesCounts.get("ppl");
                        if (activity.msgCount > 99) {
                            activity.showMessageBadge("99+");
                        }else {
                            if (activity.msgCount > 0) {
                                activity.showMessageBadge(activity.msgCount + "");
                            }
                        }
                        if (activity.letterCount > 99) {
                            activity.showLetterBadge("99+");
                        } else {
                            if (activity.letterCount > 0) {
                                activity.showLetterBadge(activity.letterCount + "");
                            }
                        }
                    }
                }
            }
        }.execute();
    }

    /**
     * 设为消息以读
     * @param position
     */
    private void messageRead(final MessageList messageList, final int position) {
        if (messageList.getStatus() == 2) {
            new AbstractRoboAsyncTask<DefaultReponse>(getActivity()) {
                @Override
                protected DefaultReponse run(Object data) throws Exception {
                    SynchronizationHttpRequest<DefaultReponse> request = RequestUtils.getInstance().createGet(getActivity(), getMessageReadRequest(messageList.getId()), null);
                    request.setClazz(DefaultReponse.class);
                    return request.getResponse();
                }

                @Override
                protected void onSuccess(DefaultReponse defaultReponse) throws Exception {
                    super.onSuccess(defaultReponse);
                    messageList.setStatus(1);
                    if (defaultReponse != null && defaultReponse.getResponseCode() == 0) {
                        MessageActivity activity = (MessageActivity) getActivity();
                        if (activity.msgCount != -1) {
                            activity.msgCount = activity.msgCount - 1;
                            if (activity.msgCount > 0) {
                                activity.showMessageBadge(activity.msgCount + "");
                            } else {
                                activity.hideMessageBadge();
                            }
                        }
                        adapter.hideBadgeImageView(position);
                    }
                }
            }.execute();
        }
    }

    @Override
    protected boolean isAddAdapterHeader() {
        return true;
    }

    @Override
    protected View adapterHeaderView() {
        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 96));
        textView.setText("忽略全部未读消息");
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(14);
        textView.setTextColor(getResources().getColor(R.color.search_tab_color));
        textView.setBackgroundColor(getResources().getColor(R.color.background));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showToast(getActivity(), "已忽略全部未读消息");
                if (items != null && !items.isEmpty()) {
                    final MessageList messageList = (MessageList) items.get(0);
                    if (messageList != null) {
                        messageAllRead(messageList.getId());
                    }
                }
            }
        });
        return textView;
    }

    private void messageAllRead(final long maxMessageId) {
        new AbstractRoboAsyncTask<DefaultReponse>(getActivity()){
            @Override
            protected DefaultReponse run(Object data) throws Exception {
                SynchronizationHttpRequest<DefaultReponse> request = RequestUtils.getInstance().createGet(getActivity(), getMessageReadAllRequest(maxMessageId), null);
                request.setClazz(DefaultReponse.class);
                return request.getResponse();
            }

            @Override
            protected void onSuccess(DefaultReponse defaultReponse) throws Exception {
                super.onSuccess(defaultReponse);
                if (defaultReponse != null && defaultReponse.getResponseCode() == 0) {
                    adapter.hideAllBadgeImageView(true);
                    ((MessageActivity) getActivity()).hideMessageBadge();
                }
            }
        }.execute();
    }

    private String getMessageReadRequest(long messageId) {
        return "message/read.json?id=" + messageId;
    }

    private String getMessageReadAllRequest(long maxMessageId) {
        return "message/readall.json?id=" + maxMessageId;
    }

    private String getMessageDel() {
        return "message/del.json";
    }

    private String getBadgesCount() {
        return "system/badges.json";
    }

    /**
     * Called when the user has scrolled to the end of the list
     */
    @Override
    public void onLastItemVisible() {
        isFirst = false;
        forceRefresh();
    }

    /**
     * onRefresh will be called for both a Pull from start, and Pull from
     * end
     *
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {

    }
}
