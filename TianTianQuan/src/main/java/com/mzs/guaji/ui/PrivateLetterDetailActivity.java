package com.mzs.guaji.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.PagedRequest;
import com.android.volley.SynchronizationHttpRequest;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.PrivateLetterDetailAdapter;
import com.mzs.guaji.core.SingleListActivity;
import com.mzs.guaji.core.FragmentProvider;
import com.mzs.guaji.core.Intents;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ProgressDialogTask;
import com.mzs.guaji.core.RequestUtils;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.PrivateLetterDetail;
import com.mzs.guaji.entity.PrivatePost;
import com.mzs.guaji.entity.PrivatePostList;
import com.mzs.guaji.util.GiveUpEditingDialog;
import com.mzs.guaji.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-3-3.
 */
public class PrivateLetterDetailActivity extends SingleListActivity<PrivateLetterDetail> implements PullToRefreshBase.OnRefreshListener<ListView> {

    @Inject
    private Context context;

    @InjectView (R.id.private_letter_back)
    private TextView backTextView;

    @InjectView (R.id.tv_private_letter_title)
    private TextView titleTextView;

    @InjectView (R.id.et_send_private_letter_content)
    private EditText contentEditText;

    @InjectView (R.id.rl_send_private_letter)
    private RelativeLayout sendLayout;

    private PrivatePostList postList;

    private boolean isFirst = true;

    private long position = 0;

    private PrivateLetterDetailAdapter adapter;

    private List<PrivatePost> postItems;

    private long userId;

    private String titleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postList = (PrivatePostList) getSerializableExtra(Intents.INTENT_EXTRA_PRIVATE_LETTER);
        if (postList != null) {
            titleName = postList.getContactUserNickname();
            titleTextView.setText(titleName);
        } else {
            titleName = getStringExtra(Intents.INTENT_EXTRA_PERSON_NICKNAME);
            userId = getLongExtra(Intents.INTENT_EXTRA_PERSON_USERID);
            if (!TextUtils.isEmpty(titleName)) {
                titleTextView.setText(titleName);
            }
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        listView.setDivider(null);
        listView.setDividerHeight(0);
        refreshListView.getLoadingLayoutProxy().setPullLabel("");
        refreshListView.getLoadingLayoutProxy().setRefreshingLabel("");
        refreshListView.getLoadingLayoutProxy().setReleaseLabel("");
        refreshListView.getLoadingLayoutProxy().setLoadingDrawable(getResources().getDrawable(R.drawable.spinner_white));
        refreshListView.setOnRefreshListener(this);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendPrivateLetterListener();
    }

    private void sendPrivateLetterListener() {
        View.OnClickListener sendClickLstener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = contentEditText.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    postPrivateLetter(content);
                } else {
                    ToastUtil.showToast(context, "信息不能为空");
                }
            }
        };
        sendLayout.setOnClickListener(sendClickLstener);
        contentEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        });
    }

    private void postPrivateLetter(final String content) {
        new ProgressDialogTask<DefaultReponse>(context){
            @Override
            protected DefaultReponse run(Object data) throws Exception {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("msg", content);
                if (postList != null) {
                    headers.put("uid", postList.getContactUserId()+"");
                } else {
                    headers.put("uid", userId+"");
                }

                SynchronizationHttpRequest<DefaultReponse> request = RequestUtils.getInstance().createPost(PrivateLetterDetailActivity.this, "privatePost/new.json", headers, null);
                request.setClazz(DefaultReponse.class);
                return request.getResponse();
            }

            @Override
            protected void onSuccess(DefaultReponse defaultReponse) throws Exception {
                super.onSuccess(defaultReponse);
                if (defaultReponse.getResponseCode() == 0) {
                    contentEditText.setText("");
                    isFirst = true;
                    position = 0;
                    forceRefresh();
                }
            }

        }.start("正在发布私信");
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.private_letter_error;
    }

    @Override
    protected int getLoadingMessage() {
        return R.string.private_letter_loading;
    }

    @Override
    protected SingleTypeAdapter createAdapter() {
        adapter = new PrivateLetterDetailAdapter(this, getLayoutInflater(), items);
        return adapter;
    }

    @Override
    protected boolean isAddAdapterHeader() {
        return false;
    }

    @Override
    protected View adapterHeaderView() {
        return null;
    }

    @Override
    protected FragmentProvider getProvider() {
        return null;
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
                PagedRequest<PrivateLetterDetail> request = new PagedRequest<PrivateLetterDetail>(page, size);
                request.setClazz(PrivateLetterDetail.class);
                if (postList != null) {
                    request.setUri(getPrivateLetterDetailRequest(postList.getContactUserId(), position, requestCount));
                } else {
                    request.setUri(getPrivateLetterDetailRequest(userId, position, requestCount));
                }
                return new PageIterator(context, request);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader loader, final PrivateLetterDetail data) {
        super.onLoadFinished(loader, data);
        refreshListView.onRefreshComplete();
        loadingIndicator.setVisible(false);
        if (data != null && data.getList() != null && data.getList().size() > 0 && data.getList().get(0) != null) {
            position = data.getList().get(0).getId();
            listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            if (isFirst) {
                this.items = data.getList();
                this.postItems = data.getList();
                getListAdapter().getWrappedAdapter().setItems(data.getList());
                listView.setSelection(getListAdapter().getWrappedAdapter().getCount() - 1);
                setLoadingIndicatorGone(data.getList());
            } else {
                List<PrivatePost> privatePosts = new ArrayList<PrivatePost>();
                privatePosts.addAll(data.getList());
                privatePosts.addAll(this.postItems);
                this.postItems = privatePosts;
                getListAdapter().getWrappedAdapter().setItems(this.postItems);
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setSelection(data.getList().size());
                    }
                });
                setLoadingIndicatorGone(privatePosts);
            }
        } else {
            List<PrivatePost> privatePosts = new ArrayList<PrivatePost>();
            if (data.getList() != null) {
                privatePosts.addAll(data.getList());
                privatePosts.addAll(this.postItems);
                this.postItems = privatePosts;
                getListAdapter().getWrappedAdapter().setItems(this.postItems);
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        listView.setSelection(data.getList().size());
                    }
                });
                setLoadingIndicatorGone(privatePosts);
            }
        }
        showList();
    }

    private void setLoadingIndicatorGone(List<PrivatePost> items) {
        if (items.size() > requestCount) {
            loadingIndicator.setVisible(true);
        }
    }

    private String getPrivateLetterDetailRequest(long id, long page, long count) {
        return "privatePost/list.json?uid=" + id + "&p=" + page + "&cnt=" + count;
    }

    @Override
    protected int getContentView() {
        return R.layout.private_letter_detail;
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        isFirst = false;
        forceRefresh();
    }

    @Override
    public boolean onListItemLongClick(ListView l, View v, int position, long id) {
        PrivatePost privatePost = (PrivatePost) l.getItemAtPosition(position);

        GiveUpEditingDialog.showCopyDialog(this, titleName, privatePost.getPost(), "");
        return false;
    }
}
