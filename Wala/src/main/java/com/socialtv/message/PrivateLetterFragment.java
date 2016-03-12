package com.socialtv.message;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.Intents;
import com.socialtv.core.MultiTypeRefreshListFragment;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.SingleTypeRefreshListFragment;
import com.socialtv.core.Utils;
import com.socialtv.http.HttpUtils;
import com.socialtv.message.entity.PrivateLetter;
import com.socialtv.message.entity.PrivateLetterItem;
import com.socialtv.util.IConstant;
import com.socialtv.util.NetWorkUtil;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-2.
 * 私信Fragment
 */
public class PrivateLetterFragment extends MultiTypeRefreshListFragment<PrivateLetter> implements HomeMessageFragment.OnVisibleRefreshListener {

    @Inject
    private MessageServices services;

    private Dialog deleteDialog;

    private TextView deleteTextView;

    private boolean isDelete = false;

    @Inject
    private PrivateLetterAdapter adapter;

    private LoginBroadcastReceiver receiver;

    private final List<PrivateLetterItem> letterItems = new ArrayList<PrivateLetterItem>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setDividerHeight(Utils.dip2px(getActivity(), 1));
    }

    @Override
    protected int getContentView() {
        return R.layout.private_letter_list;
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
    protected Request<PrivateLetter> createRequest() {
        return services.createPrivateLetterRequest(requestPage, requestCount);
    }

    @Override
    public void onLoadFinished(Loader<PrivateLetter> loader, PrivateLetter data) {
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
                if (data.getAggregations() != null && !data.getAggregations().isEmpty()) {
                    this.items = data.getAggregations();
//                    getListAdapter().getWrappedAdapter().setItems(data.getAggregations());
                    adapter.addItems(data.getAggregations(), "letter");
                    if (data.getAggregations().size() < requestCount)
                        loadingIndicator.setVisible(false);
                } else {
                    hasMore = true;
                    if (!this.items.isEmpty() && !isDelete) {
                        loadingIndicator.loadingAllFinish();
                    } else {
                        loadingIndicator.setVisible(false);
                        PrivateLetterItem item = new PrivateLetterItem();
                        item.setEmptyText("暂无私信");
                        if (letterItems.size() < 1)
                            letterItems.add(item);
                        this.items = letterItems;
                        adapter.addItems(letterItems, "empty");
                    }
                }
            }
        }
        showList();
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.private_letter_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.private_letter_loading;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        try {
            PrivateLetterItem item = (PrivateLetterItem) l.getItemAtPosition(position);
            item.setStatus(1);
            getListAdapter().getWrappedAdapter().notifyDataSetChanged();
            startActivity(new Intents(getActivity(), PrivateLetterDetailActivity.class).add(IConstant.USER_ID, item.getContactUser().getUserId()).add(IConstant.USER_NAME, item.getContactUser().getNickname()).toIntent());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onListItemLongClick(ListView l, View v, int position, long id) {
        if (!isUsable())
            return false;
        try {
            final PrivateLetterItem item = (PrivateLetterItem) l.getItemAtPosition(position);
            if (deleteDialog == null) {
                deleteDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                deleteDialog.setContentView(R.layout.delete_message_dialog);
                deleteTextView = (TextView) deleteDialog.findViewById(R.id.delete_message_dialog_text);
                deleteTextView.setText("删除私信对话");
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
                            return (Response) HttpUtils.doRequest(services.createDeletePrivateLetterRequest(item.getId())).result;
                        }

                        @Override
                        protected void onSuccess(Response response) throws Exception {
                            super.onSuccess(response);
                            if (response != null) {
                                if (response.getResponseCode() == 0) {
                                    isDelete = true;
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        HomeMessageFragment messageFragment = (HomeMessageFragment) getParentFragment();
        if (messageFragment != null) {
            messageFragment.setOnVisibleRefreshPrivateLetterListener(this);
        }
        if (receiver == null) {
            receiver = new LoginBroadcastReceiver();
            getActivity().registerReceiver(receiver, new IntentFilter(IConstant.USER_LOGIN));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null)
            getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onVisibleRefresh() {
        requestPage = 1;
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
            if (NetWorkUtil.isNetworkConnected(getActivity())) {
                items.clear();
                letterItems.clear();
            }
            refresh();
        }
    }
}
