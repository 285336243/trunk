package com.socialtv.program;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.Intents;
import com.socialtv.core.MultiTypeRefreshListFragment;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.program.entity.GroupChat;
import com.socialtv.program.entity.UpScreen;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;
import com.socialtv.view.TranslateView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-25.
 * 节目组中聊天室的Fragment
 */
public class ProgramChatFragment extends MultiTypeRefreshListFragment<GroupChat> {

    private final static int MAX_ITEM = 20;

    private final static int WHAT = 0;

    private final static int UPSCREEN = 1;

    private final static int UPSCREEN_SHOW = 2;

    private final static int UPSCREEN_DISMISS = 3;

    private final static int UPSCREEN_DISMISS_TIME = 3 * 1000;

    @Inject
    private ProgramServices services;

    @Inject
    private Activity activity;

    @InjectView(R.id.group_chat_list_edit)
    private EditText editText;

    @InjectView(R.id.group_chat_list_publish)
    private View publishView;

    @InjectView(R.id.group_chat_more)
    private TextView moreView;

    @InjectView(R.id.group_chat_up_screen)
    private TextView upScreenText;

    @InjectView(R.id.list_empty_layout)
    private View emptyListView;

    @InjectView(R.id.list_empty_text)
    private TextView emptyTextView;

    @InjectView(R.id.group_chat_more_empty_view)
    private View moreEmptyView;

    @InjectView(R.id.group_chat_translate_view)
    private TranslateView moreTranslateView;

    @InjectView(R.id.group_chat_list_publish_layout)
    private View publishLayout;

    private String programId;

    private BannerView bannerView;

    private String position = "0";

    private String newPosition = null;

    private View messageEmptyView;

    private final Handler timerHandler = new Handler();

    private boolean isPublishMessage = false;

    private View chatEmptyView;

    private View headerView;

    private boolean isEditTextClick = false;

    private String upScreenCursorId = "0";

    private DisplayMetrics metrics;

    private final List<String> msgs = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            programId = bundle.getString(IConstant.PROGRAM_ID);
        }
    }

    public void setBannerView(final BannerView bannerView) {
        this.bannerView = bannerView;
    }

    private int getScrollY() {
        View v = listView.getChildAt(0);
        if (v == null)
            return 0;
        int positon = listView.getFirstVisiblePosition();
        int top = v.getTop();
        int y = 0;
        if (positon - 1 >= 1) {
            y = bannerView.getHeight();
        }
        return y + (-top + (positon - 1) * v.getHeight());
    }

    public void scrollView(final int y) {
        if (listView != null && bannerView != null) {
            listView.setSelectionFromTop(0, y);

            int maxHeight = listView.getMeasuredHeight();
            int showHeight = 0;
            int firstPosition = listView.getFirstVisiblePosition();
            int lastPostion = listView.getLastVisiblePosition();
            int count = lastPostion - firstPosition + 1;
            for(int i=0; i<count; i++){
                View v = listView.getChildAt(i);
                if(v != null){
                    showHeight +=v.getMeasuredHeight();
                }
            }
            if(getListAdapter().isEmpty()){
                bannerView.setTranslationValue(0, y);
            }else if(showHeight>=maxHeight) {
                bannerView.setTranslationValue(0, y);
                listView.setSelectionFromTop(0, y);
            }else{
                bannerView.setTranslationValue(0,0);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isEditTextClick = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (bannerView != null && ProgramChatFragment.this.getUserVisibleHint() && !isEditTextClick) {
                    int mMinHeaderTranslation = -bannerView.getMeasuredHeight() + Utils.dip2px(getActivity(), 52);
                    int y = getScrollY();
                    bannerView.setTranslationValue(0, Math.max(-y, mMinHeaderTranslation));
                    moreTranslateView.setTranslationValue(0, Math.max(-y, mMinHeaderTranslation));
                }
            }
        });

        loadingIndicator.setVisible(false);

        emptyTextView.setText("暂无任何内容");
        getListView().setDividerHeight(Utils.dip2px(activity, 1));

        publishView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtil.isLogin(activity)) {
                    publishMessageRequest();
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    boolean isOpen = imm.isActive();
                    if (isOpen) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                } else {
                    startActivity(new Intents(activity, LoginActivity.class).toIntent());
                }
            }
        });

        moreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (upScreenText.getVisibility() != View.VISIBLE) {
                    hide(messageEmptyView);
                }
                isPublishMessage = true;
                hide(moreView);
                items.clear();
                position = "0";
                refresh();
            }
        });

        //52是tab的高度
        metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int moreEmptyHeight = metrics.widthPixels / 2 + Utils.dip2px(activity, 52);
        moreEmptyView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, moreEmptyHeight));

        FrameLayout.LayoutParams moreParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Utils.dip2px(activity, 42));
        moreParams.topMargin = moreEmptyHeight;
        moreView.setLayoutParams(moreParams);
        final int y = metrics.widthPixels / 2;

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    isEditTextClick = true;
                    bannerView.setTranslationValue(0, -y);
                    moreTranslateView.setTranslationValue(0, -y);
                    int location = headerView.getHeight() - Utils.dip2px(activity, 52) - messageEmptyView.getHeight();
                    int currentY = location + headerView.getTop();
                    if (currentY > 0) {
                        listView.smoothScrollBy(currentY, 0);
                    }
                }
                return false;
            }
        });
    }

    /**
     * 发送消息的请求
     */
    private void publishMessageRequest() {
        final String message = editText.getText().toString();
        if (TextUtils.isEmpty(message)) {
            ToastUtils.show(activity, "信息不能为空");
            return;
        }
        final Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("id", programId);
        bodys.put("msg", message);
        new ProgressDialogTask<Response>(activity){
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createPublishMessageRequest(bodys)).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        isPublishMessage = true;
                        editText.setText("");
                        position = "0";
                        refresh();
                    } else {
                        ToastUtils.show(activity, response.getResponseMessage());
                    }
                }
            }
        }.start("请稍候");
    }

    @Override
    public void onResume() {
        super.onResume();
        timerHandler.postDelayed(runnable, 10 * 1000);
        timerHandler.postDelayed(upScreenRunnable, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(runnable);
        timerHandler.removeCallbacks(upScreenRunnable);
    }

    private Handler handler = new Handler(){
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT) {
                //请求有几条新消息
                requestMessageNumber();
            } else if (msg.what == UPSCREEN) {
                //请求上屏内容
                requestUpScreenContent(this);
            } else if (msg.what == UPSCREEN_SHOW) {
                //显示上屏内容,3秒后隐藏
                List<String> msgs = (List<String>) msg.obj;
                int topMargin = 0;
                if (moreView.getVisibility() == View.GONE) {
                    topMargin = metrics.widthPixels / 2 + Utils.dip2px(activity, 52);
                } else {
                    //52是tab的高度, 42是显示未读消息的高度
                    topMargin = metrics.widthPixels / 2 + Utils.dip2px(activity, 52) + Utils.dip2px(activity, 42);
                }
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, Utils.dip2px(activity, 42));
                params.topMargin = topMargin;
                upScreenText.setLayoutParams(params);
                show(upScreenText);
                int messageEmptyHeight = 0;
                if (moreView.getVisibility() == View.VISIBLE) {
                    //显示新消息的高度
                    messageEmptyHeight += Utils.dip2px(activity, 42);
                }
                //上屏的高度
                messageEmptyHeight += Utils.dip2px(activity, 42);
                messageEmptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, messageEmptyHeight));
                show(messageEmptyView);
                ListIterator<String> iterator = msgs.listIterator();
                if (iterator.hasNext()) {
                    upScreenText.setText(iterator.next());
                    iterator.remove();
                }
                if (!msgs.isEmpty()) {
                    Message loopMsg = Message.obtain();
                    loopMsg.what = UPSCREEN_SHOW;
                    loopMsg.obj = msgs;
                    sendMessageDelayed(loopMsg, UPSCREEN_DISMISS_TIME);
                }else {
                    sendEmptyMessageDelayed(UPSCREEN_DISMISS, UPSCREEN_DISMISS_TIME);
                }
            } else if (msg.what == UPSCREEN_DISMISS) {
                //隐藏上屏内容
                if (moreView.getVisibility() != View.VISIBLE) {
                    hide(messageEmptyView);
                } else {
                    messageEmptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Utils.dip2px(activity, 42)));
                }
                hide(upScreenText);
            }
        }
    };

    /**
     * 上屏的Runnable
     */
    Runnable upScreenRunnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(UPSCREEN);
            timerHandler.postDelayed(this, 15 * 1000);
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.sendEmptyMessage(WHAT);
            timerHandler.postDelayed(this, 60 * 1000);
        }
    };

    /**
     * 请求上屏的内容
     * @param mHandler
     */
    private void requestUpScreenContent(final Handler mHandler) {
        new AbstractRoboAsyncTask<UpScreen>(activity){

            @Override
            protected UpScreen run(Object data) throws Exception {
                return (UpScreen) HttpUtils.doRequest(services.createUpScreenRequest(upScreenCursorId)).result;
            }

            @Override
            protected void onSuccess(UpScreen upScreen) throws Exception {
                super.onSuccess(upScreen);
                if (upScreen != null) {
                    if (upScreen.getResponseCode() == 0) {
                        upScreenCursorId = upScreen.getCursorId();
                        if (upScreen.getMsg() != null && !upScreen.getMsg().isEmpty()) {
                            msgs.addAll(upScreen.getMsg());
                            mHandler.removeMessages(UPSCREEN_SHOW);
                            Message msg = Message.obtain();
                            msg.obj = msgs;
                            msg.what = UPSCREEN_SHOW;
                            mHandler.sendMessage(msg);
                        }
                    }
                }
            }

            @Override
            protected void onThrowable(Throwable t) throws RuntimeException {
                super.onThrowable(t);
            }
        }.execute();
    }

    /**
     * 请求消息数量
     */
    private void requestMessageNumber() {
        if(newPosition == null){
            return;
        }
        new AbstractRoboAsyncTask<Response>(activity){
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(services.createMessageNumberRequest(programId, newPosition)).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        if (response.getTotal() > 99 && getListAdapter().getHeadersCount() < 1) {
                            show(messageEmptyView);
                            show(moreView);
                            moreView.setText("99+ 条新消息");
                            int messageEmptyHeight = 0;

                            if (upScreenText.getVisibility() == View.VISIBLE) {
                                //显示新消息的高度
                                messageEmptyHeight += Utils.dip2px(activity, 42);
                            }
                            //上屏的高度
                            messageEmptyHeight += Utils.dip2px(activity, 42);
                            messageEmptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, messageEmptyHeight));
                        } else if (response.getTotal() > 0 && response.getTotal() < 99){
                            show(messageEmptyView);
                            show(moreView);
                            moreView.setText(response.getTotal() + " 条新消息");

                            int messageEmptyHeight = 0;
                            if (upScreenText.getVisibility() == View.VISIBLE) {
                                //显示新消息的高度
                                messageEmptyHeight += Utils.dip2px(activity, 42);
                            }
                            //上屏的高度
                            messageEmptyHeight += Utils.dip2px(activity, 42);
                            messageEmptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, messageEmptyHeight));
                        } else {
                            if (upScreenText.getVisibility() != View.VISIBLE) {
                                hide(messageEmptyView);
                            }
                            hide(moreView);
                        }
                    }
                }
            }
        }.execute();
    }

    /**
     * 刷新当前列表
     */
    public void refreshList() {
        hasMore = false;
        position = "0";
        requestPage = 1;
        isPublishMessage = true;
        if (bannerView != null)
            bannerView.setTranslationValue(0, 0);
        listView.setSelectionFromTop(0, 0);
        refresh();
    }

    @Override
    protected int getContentView() {
        return R.layout.group_chat_list;
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected MultiTypeAdapter createAdapter() {
        return new GroupChatAdapter(activity);
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
        headerView = getLayoutInflater(null).inflate(R.layout.program_chat_header, null);
        chatEmptyView = headerView.findViewById(R.id.program_chat_header_empty);
        if (getActivity() != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            chatEmptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, metrics.widthPixels / 2 + Utils.dip2px(getActivity(), 52)));
        }
        messageEmptyView = headerView.findViewById(R.id.program_chat_header_message_empty);
        return headerView;
    }

    @Override
    public void onDestroyView() {
        getListAdapter().removeHeader(headerView);
        super.onDestroyView();
    }

    /**
     * Create Request
     *
     * @return
     */
    @Override
    protected Request<GroupChat> createRequest() {
        return services.createGroupChatRequest(programId, position, requestCount);
    }

    @Override
    public void onLoadFinished(Loader<GroupChat> loader, final GroupChat data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                final GroupChatAdapter adapter = (GroupChatAdapter) getListAdapter().getWrappedAdapter();
                if (isPublishMessage) {
                    adapter.clear();
                    final int y = metrics.widthPixels / 2;
                    final int location = headerView.getHeight() - Utils.dip2px(activity, 52) - messageEmptyView.getHeight();
                    final int currentY = location + headerView.getTop();
                    if (currentY > 0) {
                        scrollView(y);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            listView.smoothScrollToPositionFromTop(1, -location);
                        }
                    }
                    isPublishMessage = false;
                }
                if (data.getPosts() != null && !data.getPosts().isEmpty()) {
                    show(refreshListView);
                    show(listView);
                    hide(emptyListView);
                    this.items = data.getPosts();
                    if(newPosition == null || Long.valueOf(newPosition) < Long.valueOf(data.getPosts().get(0).getId())){
                        newPosition = data.getPosts().get(0).getId();
                    }
                    this.position = data.getPosts().get(data.getPosts().size() - 1).getId();
                    adapter.addItems(data.getPosts());
                } else {
                    hasMore = true;
                    if (!this.items.isEmpty()) {
//                        loadingIndicator.loadingAllFinish();
                    } else {
                        show(emptyListView);
                    }
                }
            } else {
                newPosition = "0";
                ToastUtils.show(activity, data.getResponseMessage());
            }
        }else{
            newPosition = "0";
        }
        show(publishLayout);
        showList();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        this.items.clear();
        isRefresh = true;
        hasMore = false;
        position = "0";
        refresh();
    }

    @Override
    public void onLastItemVisible() {
        isRefresh = false;
        if (hasMore)
            return;
        if (getLoaderManager().hasRunningLoaders())
            return;
        refresh();
    }

    /**
     * Get error message to display forexception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.group_chat_error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.group_chat_loading;
    }
}
