package com.mzs.guaji.fragment;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.PagedRequest;
import com.android.volley.SynchronizationHttpRequest;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.PrivateLetterListAdapter;
import com.mzs.guaji.core.AbstractRoboAsyncTask;
import com.mzs.guaji.core.Intents;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ProgressDialogTask;
import com.mzs.guaji.core.RequestUtils;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.core.SingleTypeRefreshListFragment;
import com.mzs.guaji.entity.Badges;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.PrivateLetter;
import com.mzs.guaji.entity.PrivatePostList;
import com.mzs.guaji.ui.MessageActivity;
import com.mzs.guaji.ui.PrivateLetterDetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wlanjie on 14-1-20.
 */
public class PrivateLetterFragment extends SingleTypeRefreshListFragment<PrivateLetter> {

    private PrivateLetterListAdapter adapter;

    public RelativeLayout deleteMessageParent;

    private LinearLayout deleteMessage;

    private List<String> selects = new ArrayList<String>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(R.string.private_letter_empty);
        refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.private_letter_error;
    }

    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        adapter = new PrivateLetterListAdapter(getActivity());
        return adapter;
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
                request.setClazz(PrivateLetter.class);
                request.setUri(getPrivateLetterRequest(1, 100));
                return new PageIterator(getActivity(), request);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<PrivateLetter> loader, PrivateLetter es) {
        super.onLoadFinished(loader, es);
        loadingIndicator.setVisible(false);
        if (es != null && es.getList() != null) {
            this.items = es.getList();
            getListAdapter().getWrappedAdapter().setItems(es.getList());
        } else {
            this.items = Collections.emptyList();
        }
        showList();
    }

    @Override
    protected int getLoadingMessage() {
        return R.string.private_letter_loading;
    }

    @Override
    protected int getContentView() {
        return R.layout.private_letter_list;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        deleteMessageParent = (RelativeLayout) view.findViewById(R.id.rl_delete_private_letter_parent);
        deleteMessage = (LinearLayout) view.findViewById(R.id.ll_private_letter_del);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (selectDeleteMessage(position, v)) {
            return;
        }
        if (adapter != null) {
            PrivatePostList itemList = (PrivatePostList) l.getItemAtPosition(position);
            itemList.setStatus(1);
            adapter.setPosition(position);
        }

        hideLetterBadge();

        PrivatePostList privatePostList = (PrivatePostList) l.getItemAtPosition(position);
        startActivity(new Intents(getActivity(), PrivateLetterDetailActivity.class).add(Intents.INTENT_EXTRA_PRIVATE_LETTER, privatePostList).toIntent());
    }

    private void hideLetterBadge() {
        MessageActivity activity = (MessageActivity) getActivity();
        if (activity.letterCount != -1) {
            activity.letterCount = activity.letterCount - 1;
            if (activity.letterCount > 0) {
                activity.showLetterBadge(activity.letterCount + "");
            } else {
                activity.hideLetterBadge();
            }
        }
    }

    @Override
    public boolean onListItemLongClick(ListView l, View v, int position, long id) {
        deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMessage();
            }
        });
        if (deleteMessageParent.getVisibility() != View.VISIBLE) {
            deleteMessageParent.setVisibility(View.VISIBLE);
            if (items != null && !items.isEmpty()) {
                PrivatePostList postList = (PrivatePostList) items.get(position);
                if (selects.contains(postList.getId())) {
                    selects.remove(postList.getId());
                    adapter.setItemBackground(false, postList,v);
                } else {
                    selects.add(postList.getId());
                    adapter.setItemBackground(true, postList,v);
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
                    builder.append("\""+selects.get(i)+"\"");
                    if (i!=selects.size()-1) {
                        builder.append(",");
                    }
                }
                builder.append("]");
                Map<String, String> bodys = new HashMap<String, String>();
                bodys.put("ids", builder.toString());
                SynchronizationHttpRequest<DefaultReponse> request = RequestUtils.getInstance().createPost(getActivity(), getDeletePrivateLetterRequest(), bodys, null);
                request.setClazz(DefaultReponse.class);
                return request.getResponse();
            }

            @Override
            protected void onSuccess(DefaultReponse defaultReponse) throws Exception {
                super.onSuccess(defaultReponse);
                if (defaultReponse != null && defaultReponse.getResponseCode() == 0) {
                    deleteMessageParent.setVisibility(View.GONE);
                    requestBadgeCount();
                    forceRefresh();
                }
            }
        }.start("正在删除私信");
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
                            } else {
                                activity.hideLetterBadge();
                            }
                        }
                    }
                }
            }
        }.execute();
    }

    private boolean selectDeleteMessage(int position, View v) {
        if (deleteMessageParent.getVisibility() == View.VISIBLE) {
            if (items != null && !items.isEmpty()) {
                PrivatePostList postList = (PrivatePostList) items.get(position);
                if (selects.contains(postList.getId())) {
                    selects.remove(postList.getId());
                    adapter.setItemBackground(false, postList,v);
                } else {
                    selects.add(postList.getId());
                    adapter.setItemBackground(true, postList,v);
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
            PrivatePostList list = (PrivatePostList) items.get(i);
            list.setChecked(false);
        }
        adapter.notifyDataSetChanged();
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
     * Called when the user has scrolled to the end of the list
     */
    @Override
    public void onLastItemVisible() {

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

    private String getPrivateLetterRequest(int page, int count) {
        return "privatePost/aggregation.json" + "?p=" + page + "&cnt=" + count;
    }

    private String getDeletePrivateLetterRequest() {
        return "privatePost/del.json";
    }

    private String getBadgesCount() {
        return "system/badges.json";
    }
}
