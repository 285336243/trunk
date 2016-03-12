package com.socialtv.message;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.FragmentProvider;
import com.socialtv.core.MultiListActivity;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.http.HttpUtils;
import com.socialtv.message.entity.PrivateLetterDetail;
import com.socialtv.message.entity.PrivateLetterDetailList;
import com.socialtv.util.IConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-3.
 * 私信详情的页面
 */
public class PrivateLetterDetailActivity extends MultiListActivity<PrivateLetterDetail> {

    @Inject
    private MessageServices services;

    @Inject
    private Activity activity;

    private String cusorId = "0";

    @InjectExtra(value = IConstant.USER_ID, optional = true)
    private String userId;

    @InjectExtra(value = IConstant.USER_NAME, optional = true)
    private String nickname;

    @InjectView(R.id.private_letter_detail_list_edit)
    private EditText editText;

    @InjectView(R.id.private_letter_detail_list_publish)
    private View publishView;

    private boolean isPublishMessage = false;

    private boolean isLoadFinish = false;

    private List<PrivateLetterDetailList> detailLists = new ArrayList<PrivateLetterDetailList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
        getSupportActionBar().setTitle(nickname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadingIndicator.setVisible(false);
        getListView().setDividerHeight(Utils.dip2px(this, 1));
        hasMore = true;
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        publishView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = editText.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                final Map<String, String> bodys = new HashMap<String, String>();
                bodys.put("userid", userId);
                bodys.put("msg", message);
                new ProgressDialogTask<Response>(activity){

                    @Override
                    protected Response run(Object data) throws Exception {
                        return (Response) HttpUtils.doRequest(services.createPublishPrivateLetterRequest(bodys)).result;
                    }

                    @Override
                    protected void onSuccess(Response response) throws Exception {
                        super.onSuccess(response);
                        if (response != null) {
                            if (response.getResponseCode() == 0) {
                                isPublishMessage = true;
                                editText.setText("");
                                cusorId = "0";
                                refresh();
                            } else {
                                ToastUtils.show(activity, response.getResponseMessage());
                            }
                        }
                    }
                }.start("正在发送私信");
            }
        });
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        isRefresh = true;
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isPublishMessage = false;
        detailLists.clear();
        detailLists = null;
    }

    @Override
    protected int getContentView() {
        return R.layout.private_letter_detail_list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected MultiTypeAdapter createAdapter() {
        return new PrivateLetterDetailAdapter(this);
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
    protected Request<PrivateLetterDetail> createRequest() {
        return services.createPrivateLetterDetailRequest(cusorId, requestCount, userId);
    }

    @Override
    public void onLoadFinished(Loader<PrivateLetterDetail> loader, final PrivateLetterDetail data) {
        super.onLoadFinished(loader, data);
        final PrivateLetterDetailAdapter adapter  = (PrivateLetterDetailAdapter) getListAdapter().getWrappedAdapter();
        if (data != null) {
            if (!TextUtils.isEmpty(data.getCusorId())) {
                cusorId = data.getCusorId();
            }
            if (data.getResponseCode() == 0) {
                if (isPublishMessage) {
                    adapter.clear();
                    adapter.addItems(data.getList());
                    this.items = data.getList();
                    this.detailLists = data.getList();
                    listView.post(new Runnable() {
                        @Override
                        public void run() {
                            listView.setSelection(adapter.getCount() - 1);
                        }
                    });
                    isPublishMessage = false;
                } else {
                    isPublishMessage = false;
                    if (data.getList() != null && !data.getList().isEmpty()) {
//                        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                        List<PrivateLetterDetailList> detailLists = new ArrayList<PrivateLetterDetailList>();
                        this.items = data.getList();
                        detailLists.addAll(data.getList());
                        detailLists.addAll(this.detailLists);
                        this.detailLists = detailLists;
                        adapter.clear();
                        adapter.addItems(this.detailLists);

                        listView.post(new Runnable() {
                            @Override
                            public void run() {
                                listView.setSelection(data.getList().size());
                            }
                        });
                    } else {
                        adapter.clear();
                        adapter.addItems(this.detailLists);
                    }
                }
            } else {
                ToastUtils.show(this, data.getResponseMessage());
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
        return R.string.private_letter_detail_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.private_letter_detail_loading;
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
