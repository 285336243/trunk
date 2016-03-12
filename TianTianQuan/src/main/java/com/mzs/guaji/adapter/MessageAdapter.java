package com.mzs.guaji.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.GsonUtils;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.MessageActivityTopicPost;
import com.mzs.guaji.entity.MessageCelebrityTopicPost;
import com.mzs.guaji.entity.MessageList;
import com.mzs.guaji.entity.MessageTopicPost;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by wlanjie on 14-3-6.
 */
public class MessageAdapter extends SingleTypeAdapter<MessageList> {

    private final Gson gson = GsonUtils.createGson();
    private final Context context;
    private int position = -1;
    private boolean isSelect;
    private MessageList listItem;
    private boolean isHideAllBadge = false;

    public MessageAdapter(Context context, List<?> items) {
        super(context, R.layout.message_list_item);
        this.context = context;
        setItems(items);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{R.id.iv_message_item_avatar,
                        R.id.tv_message_item_nickname,
                        R.id.tv_message_item_content,
                        R.id.tv_message_item_create_time,
                        R.id.tv_message_item_title_text,
                        R.id.iv_message_badge,
                        R.id.message_list_parent};
    }

    @Override
    protected void update(int position, MessageList item) {
        if (this.position == position) {
            imageView(5).setVisibility(View.GONE);
        }
        if (isHideAllBadge) {
            item.setStatus(1);
//            imageView(5).setVisibility(View.GONE);
        }
        if(item.isChecked()) {
            view(6).setBackgroundColor(context.getResources().getColor(R.color.message_item_selector));
        }else{
            view(6).setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        if (item != null) {
            if (item.getStatus() == 1) {
                imageView(5).setVisibility(View.GONE);
            } else {
                imageView(5).setVisibility(View.VISIBLE);
            }
            JsonElement element = item.getMessage();
            if ("TOPIC_POST".equals(item.getType())) {
                if (element != null) {
                    MessageTopicPost topicPost = gson.fromJson(element, MessageTopicPost.class);
                    if (!TextUtils.isEmpty(topicPost.getUserAvatar())) {
                        ImageLoader.getInstance().displayImage(topicPost.getUserAvatar(), imageView(0), ImageUtils.imageLoader(context, 4));
                    }

                    textView(2).setText(topicPost.getMessage());
                    textView(3).setText(topicPost.getCreateTime());
                    String type = topicPost.getType();

                    if (!TextUtils.isEmpty(type)) {
                        if ("TOPIC".equals(topicPost.getType()) && topicPost.getTopic() != null) {
                            if (topicPost.getPost() != null) {
                                if (LoginUtil.getUserId(context) == topicPost.getPost().getUserId()) {
                                    textView(4).setText("回复了你的评论\""+topicPost.getTopic().getTitle()+"\"");
                                } else {
                                    textView(1).setText(topicPost.getUserNickname() + " 回复 " + topicPost.getPost().getUserNickname());
                                    textView(4).setText("评论了你的话题\""+topicPost.getTopic().getTitle()+"\"");
                                }
                            } else {
                                textView(1).setText(topicPost.getUserNickname());
                                textView(4).setText("评论了你的话题\""+topicPost.getTopic().getTitle()+"\"");
                            }
                        } else if ("GROUP".equals(topicPost.getType())) {
//                            textView(4).setText(topicPost.getPost().get);
                        }
                    }
                }
            } else if ("ACTIVITY_TOPIC_POST".equals(item.getType())) {
                if (element != null) {
                    MessageActivityTopicPost topicPost = gson.fromJson(element, MessageActivityTopicPost.class);
                    if (!TextUtils.isEmpty(topicPost.getUserAvatar())) {
                        ImageLoader.getInstance().displayImage(topicPost.getUserAvatar(), imageView(0), ImageUtils.imageLoader(context, 4));
                    }

                    textView(2).setText(topicPost.getMessage());
                    textView(3).setText(topicPost.getCreateTime());
                    String type = topicPost.getType();

                    if (!TextUtils.isEmpty(type)) {
                        if ("ACTIVITY_TOPIC".equals(topicPost.getType()) && topicPost.getTopic() != null) {
                            if (topicPost.getPost() != null) {
                                if (LoginUtil.getUserId(context) == topicPost.getPost().getUserId()) {
                                    textView(4).setText("回复了你的评论\""+topicPost.getTopic().getTitle()+"\"");
                                } else {
                                    textView(1).setText(topicPost.getUserNickname() + " 回复 " + topicPost.getPost().getUserNickname());
                                    textView(4).setText("评论了你的话题\""+topicPost.getTopic().getTitle()+"\"");
                                }
                            } else {
                                textView(1).setText(topicPost.getUserNickname());
                                textView(4).setText("评论了你的话题\""+topicPost.getTopic().getTitle()+"\"");
                            }
                        } else if ("GROUP".equals(topicPost.getType())) {
//                            textView(4).setText(topicPost.getPost().get);
                        }
                    }
                }
            } else if ("Celebrity_TOPIC_POST".equals(item.getType())) {
                if (element != null) {
                    MessageCelebrityTopicPost topicPost = gson.fromJson(element, MessageCelebrityTopicPost.class);
                    if (!TextUtils.isEmpty(topicPost.getUserAvatar())) {
                        ImageLoader.getInstance().displayImage(topicPost.getUserAvatar(), imageView(0), ImageUtils.imageLoader(context, 4));
                    }

                    textView(2).setText(topicPost.getMessage());
                    textView(3).setText(topicPost.getCreateTime());
                    String type = topicPost.getType();
                    if (!TextUtils.isEmpty(type)) {
                        if ("CELEBRITY_TOPIC".equals(topicPost.getType()) && topicPost.getTopic() != null) {
                            if (topicPost.getPost() != null) {
                                if (LoginUtil.getUserId(context) == topicPost.getPost().getUserId()) {
                                    textView(4).setText("回复了你的评论\""+topicPost.getTopic().getTitle()+"\"");
                                } else {
                                    textView(1).setText(topicPost.getUserNickname() + " 回复 " + topicPost.getPost().getUserNickname());
                                    textView(4).setText("评论了你的话题\""+topicPost.getTopic().getTitle()+"\"");
                                }
                            } else {
                                textView(1).setText(topicPost.getUserNickname());
                                textView(4).setText("评论了你的话题\""+topicPost.getTopic().getTitle()+"\"");
                            }
                        } else if ("GROUP".equals(topicPost.getType())) {
//                            textView(4).setText(topicPost.getPost().get);
                        }
                    }
                }
            }
        }
    }

    public void hideAllBadgeImageView(boolean isHideAllBadge) {
        this.isHideAllBadge = isHideAllBadge;
        notifyDataSetChanged();
    }

    public void hideBadgeImageView(int position) {
        this.position = position;
        notifyDataSetChanged();
    }

    public void setItemBackground(boolean isSelect, MessageList listItem, View v) {
        this.isSelect = isSelect;
        this.listItem = listItem;
        listItem.setChecked(isSelect);
        if(v!=null){
            if(listItem.isChecked()) {
                ((View[]) (v.getTag()))[6].setBackgroundColor(context.getResources().getColor(R.color.message_item_selector));
            }else{
                ((View[]) (v.getTag()))[6].setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        }

    }
}
