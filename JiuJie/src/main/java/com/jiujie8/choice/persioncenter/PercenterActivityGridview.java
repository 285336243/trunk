package com.jiujie8.choice.persioncenter;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.jiujie8.choice.R;
import com.jiujie8.choice.core.DialogFragmentActivity;
import com.jiujie8.choice.core.ToastUtils;
import com.jiujie8.choice.home.entity.ChoiceMode;
import com.jiujie8.choice.persioncenter.entity.PersonDetails;
import com.jiujie8.choice.persioncenter.entity.PersonJiujieBean;
import com.jiujie8.choice.persioncenter.entity.PersonReplyBean;
import com.jiujie8.choice.persioncenter.entity.UserDetailsModel;
import com.jiujie8.choice.publicentity.User;
import com.jiujie8.choice.setting.SettingActivity;
import com.jiujie8.choice.util.HttpHelp;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.util.ImageUtils;
import com.jiujie8.choice.view.BadgeView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import in.srain.cube.views.loadmore.LoadMoreContainer;
import in.srain.cube.views.loadmore.LoadMoreGridViewContainer;
import in.srain.cube.views.loadmore.LoadMoreHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
import uk.co.senab.actionbarpulltorefresh.library.viewdelegates.AbsListViewDelegate;

@ContentView(R.layout.activity_person_center)
public class PercenterActivityGridview extends DialogFragmentActivity implements OnRefreshListener {


    //初始化adapter
    @Inject
    private PersonJiuJieSingleAdapter adapterJiujie;
    @Inject
    private PersonReplySingleAdapter adapterReply;
    @Inject
    private PersonCollectSingleAdapter adapterCollect;
    private ImageView gender;
    private ImageView userPhoto;
    private TextView countJiujie;
    private TextView countReply;
    private TextView countCollect;
    private RadioGroup radioGroup;


    @InjectView(R.id.ptr_layout)
    protected PullToRefreshLayout refreshLayout;
    private static final String PERSON_DETAIL_URL = "choice/user/userDetails";
    private static final String PERSON_JIUJIE_URL = "choice/user/choiceList";
    private static final String PERSON_REPLY_URL = "choice/user/postList";
    private static final String PERSON_COLLECT_URL = "choice/user/favoriteList";
    private Context context;

    @InjectView(R.id.load_more_grid_view_ptr_frame)
    private PtrFrameLayout mPtrFrameLayout;
    @InjectView(R.id.load_more_grid_view)
    private GridViewWithHeaderAndFooter mGridView;
    @InjectView(R.id.load_more_grid_view_container)
    private LoadMoreGridViewContainer loadMoreContainer;

    private TextView viewText;
    private LayoutInflater inflater;
    private View headerView;
    private int jiujiePage = 1;
    private int replyPage = 1;
    private int collectPage = 1;
    private int count = 10;
    private List<ChoiceMode> relyList = new LinkedList<ChoiceMode>();
    private List<ChoiceMode> collectList = new LinkedList<ChoiceMode>();
    private List<ChoiceMode> listChoice = new LinkedList<ChoiceMode>();
    private boolean moreJiuJIE;
    private boolean moreReply;
    private boolean moreCollect;
    private ActionBar actionBar;
    private BadgeView bageViewJiujie, bageViewReply;
    private View emptyView;
    private TextView emptyText;
    private boolean addPublish = true;
    private boolean noJiujieData = true;
    /**
     * 回答是否有数据
     */
    private boolean ishavereplyData;
    /**
     * 是否有纠结数据
     */
    private boolean ishavecollectionData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_choice);
        //该方法不但决定左上角图标的右侧是否有向左的小箭头，还决定是否可以点击。
        //parameters: true 有小箭头，并且图标可以点击；false 没有小箭头，并且图标不可以点击。
        actionBar.setDisplayHomeAsUpEnabled(true);

        inflater = getLayoutInflater();
        viewListener();
        requestPersonDetails();
        clickListener();
        getRequestJiejiuData();

        ActionBarPullToRefresh.from(this)
                .allChildrenArePullable()
                .listener(this)
                        // Here we'll set a custom ViewDelegate
                .useViewDelegate(GridViewWithHeaderAndFooter.class, new AbsListViewDelegate())
                .setup(refreshLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //从发布返回刷新纠结列表
        if (requestCode == IConstant.JIUJIE_RESULT_CODE && resultCode == RESULT_OK) {
//            radioGroup.check(R.id.radio_jiujie);
            checkedChange(R.id.radio_jiujie);
        }
        if (requestCode == IConstant.SELF_REPLY_CODE && resultCode == RESULT_OK) {
            checkedChange(R.id.radio_reply);
        }
        if (requestCode == IConstant.SELF_COLLECT_CODE && resultCode == RESULT_OK) {
            checkedChange(R.id.radio_collection);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "设置")
                .setIcon(R.drawable.icon_setting_choice)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case 0:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNoticeJiujie() {
        if (bageViewJiujie != null && !bageViewJiujie.isShown()) {
            bageViewJiujie.show();
        }
    }

    private void hideNoticeJiujie() {
        if (bageViewJiujie != null && bageViewJiujie.isShown()) {
            bageViewJiujie.hide();
        }
    }

    private void showNoticeReply() {
        if (bageViewReply != null && !bageViewReply.isShown()) {
            bageViewReply.show();
        }
    }

    private void hideNoticeReply() {
        if (bageViewReply != null && bageViewReply.isShown()) {
            bageViewReply.hide();
        }
    }


    private void viewListener() {
        emptyView = inflater.inflate(R.layout.empty_view, null);
        emptyText = (TextView) emptyView.findViewById(R.id.empty_hint_text);
        mGridView.addHeaderView(getHeaderView());
        mGridView.addHeaderView(emptyView);
        emptyView.setVisibility(View.GONE);
        viewText = (TextView) getLayoutInflater().inflate(R.layout.load_more, null);
        loadMoreContainer.setLoadMoreView(viewText);
        mGridView.setAdapter(adapterJiujie);
        //开始加载更多
        loadMoreContainer.setLoadMoreHandler(new LoadMoreHandler() {
            @Override
            public void onLoadMore(LoadMoreContainer loadMoreContainer) {
                if (moreJiuJIE) {
                    jiujiePage = jiujiePage + 1;
                    getRequestJiejiuData();
                }
                if (moreReply) {
                    replyPage = replyPage + 1;
                    getRequestReplyData();
                }
                if (moreCollect) {
                    collectPage = collectPage + 1;
                    getRequestCollectData();
                }
            }
        });

    }

    private void requestPersonDetails() {
        bageViewJiujie = new BadgeView(this, countJiujie);
        bageViewJiujie.setBadgeMargin(35, 10);
        bageViewJiujie.setImageResource(R.drawable.icon_pointer_choice);
        bageViewJiujie.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);

        bageViewReply = new BadgeView(this, countReply);
        bageViewReply.setBadgeMargin(35, 10);
        bageViewReply.setImageResource(R.drawable.icon_pointer_choice);
        bageViewReply.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);

        HttpHelp.getInstance().getHttp(this, PersonDetails.class, PERSON_DETAIL_URL, null, new HttpHelp.OnCompleteListener<PersonDetails>() {
            @Override
            public void onComplete(PersonDetails response) {
                if (response.getCode().equals(IConstant.STATE_OK)) {
                    UserDetailsModel entity = response.getEntity();
                    setPerosnDedails(entity);

                } else {
                    ToastUtils.show(PercenterActivityGridview.this, response.getMessage());
                }
            }
        });
    }

    private void setPerosnDedails(UserDetailsModel entity) {
        User user = entity.getUser();
        ImageLoader.getInstance().displayImage(user.getAvatar(), userPhoto, ImageUtils.avatarImageLoader());
//        name.setText(user.getNickname());
        countJiujie.setText(String.valueOf(entity.getChoices()));
        countReply.setText(String.valueOf(entity.getPosts()));
        countCollect.setText(String.valueOf(entity.getFavorites()));
        actionBar.setTitle(user.getNickname());
        if (entity.isChoiceStatus()) {
            showNoticeJiujie();
        }
        if (entity.isChoiceStatus()) {
            showNoticeReply();
        }
        if ("male".equals(user.getGender())) {
            gender.setImageResource(R.drawable.icon_man_choice);
        }
        if ("female".equals(user.getGender())) {
            gender.setImageResource(R.drawable.icon_lady_choice);
        }

    }

    private void clickListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkedChange(checkedId);
            }
        });
    }

    private void checkedChange(int id) {
        Resources res = getResources();
        viewText.setText("正在加载......");
        emptyView.setVisibility(View.GONE);
        viewText.setVisibility(View.VISIBLE);
        switch (id) {
            case R.id.radio_jiujie:
                hideNoticeJiujie();
                moreReply = false;
                moreCollect = false;
                addPublish = true;
                noJiujieData = true;
                if (!listChoice.isEmpty()) {
                    listChoice.clear();
                }
                adapterJiujie.setItems(listChoice);
                mGridView.setAdapter(adapterJiujie);
                jiujiePage = 1;
                getRequestJiejiuData();
                countJiujie.setTextColor(res.getColor(R.color.select_card_color));
                countCollect.setTextColor(res.getColor(R.color.light_grey));
                countReply.setTextColor(res.getColor(R.color.light_grey));
                break;
            case R.id.radio_reply:
                hideNoticeReply();
                ishavereplyData = false;
                moreJiuJIE = false;
                moreCollect = false;
                if (!relyList.isEmpty()) {
                    relyList.clear();
                }
                adapterReply.setItems(relyList);
                mGridView.setAdapter(adapterReply);
                replyPage = 1;
//                Log.v("kan","执行。R.id.radio_reply");
                getRequestReplyData();
                countJiujie.setTextColor(res.getColor(R.color.light_grey));
                countCollect.setTextColor(res.getColor(R.color.light_grey));
                countReply.setTextColor(res.getColor(R.color.select_card_color));
                break;
            case R.id.radio_collection:
                ishavecollectionData = false;
                moreJiuJIE = false;
                moreReply = false;
                if (!collectList.isEmpty()) {
                    collectList.clear();
                }
                adapterCollect.setItems(collectList);
                mGridView.setAdapter(adapterCollect);
                collectPage = 1;
                getRequestCollectData();
                countJiujie.setTextColor(res.getColor(R.color.light_grey));
                countCollect.setTextColor(res.getColor(R.color.select_card_color));
                countReply.setTextColor(res.getColor(R.color.light_grey));
                break;
            default:
                break;
        }
    }

    private void getRequestJiejiuData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("page", String.valueOf(jiujiePage));
        map.put("count", String.valueOf(count));
        HttpHelp.getInstance().getHttp(this, PersonJiujieBean.class, PERSON_JIUJIE_URL, map, new HttpHelp.OnCompleteListener<PersonJiujieBean>() {
            @Override
            public void onComplete(PersonJiujieBean response) {
                List<ChoiceMode> choiceModes = response.getRs();
//                Log.v("kan", "choiceModes.size()= "+choiceModes.size());
                if (choiceModes != null && !choiceModes.isEmpty()) {
                    noJiujieData = false;
                    listChoice.addAll(choiceModes);
                    if (addPublish) {
                        listChoice.add(0, listChoice.get(0));
                        addPublish = false;
                    }
                    adapterJiujie.setItems(listChoice);
                    adapterJiujie.setListChoice(listChoice);
                    if (choiceModes.size() < count) {

                        loadMoreContainer.loadMoreFinish(false, false);
                        viewText.setText("已加载全部");
//                    Log.v("kan", "choiceModes.size()内部= " + choiceModes.size());
                        return;
                    } else {
                        moreJiuJIE = true;
                  /*      listChoice.addAll(choiceModes);
                        adapterJiujie.setItems(listChoice);*/
                        loadMoreContainer.loadMoreFinish(false, true);
                    }
                } else {
                    if (noJiujieData) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyText.setText("没有纠结的人生？\n 太美好了~");
                        viewText.setVisibility(View.GONE);
                    } else {
                        loadMoreContainer.loadMoreFinish(false, false);
                        viewText.setText("已加载全部");
                    }
                }

            }
        });
    }

    private void getRequestCollectData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("page", String.valueOf(collectPage));
        map.put("count", String.valueOf(count));
        HttpHelp.getInstance().getHttp(this, PersonJiujieBean.class, PERSON_COLLECT_URL, map, new HttpHelp.OnCompleteListener<PersonJiujieBean>() {
            @Override
            public void onComplete(PersonJiujieBean response) {
                List<ChoiceMode> choiceModes = response.getRs();
//               Log.v("kan", "lectData size = "+choiceModes.size());
                if (choiceModes != null && !choiceModes.isEmpty()) {
                    ishavecollectionData = true;
                    if (choiceModes.size() < count) {
                        collectList.addAll(choiceModes);
                        adapterCollect.setItems(collectList);
                        loadMoreContainer.loadMoreFinish(false, false);
                        viewText.setText("已加载全部");

                        return;
                    } else {
                        moreCollect = true;
                        collectList.addAll(choiceModes);
                        adapterCollect.setItems(collectList);
                        loadMoreContainer.loadMoreFinish(false, true);
                    }
                } else {
                    if (!ishavecollectionData) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyText.setText("收藏你喜欢的纠结 \n 别人的经验也能给自己帮助");
                        viewText.setVisibility(View.GONE);
                    } else {
                        loadMoreContainer.loadMoreFinish(false, false);
                        viewText.setText("已加载全部");
                    }
                }
            }
        });
    }

    private void getRequestReplyData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("page", String.valueOf(replyPage));
        map.put("count", String.valueOf(count));
        HttpHelp.getInstance().getHttp(this, PersonReplyBean.class, PERSON_REPLY_URL, map, new HttpHelp.OnCompleteListener<PersonReplyBean>() {
            @Override
            public void onComplete(PersonReplyBean response) {
                List<ChoiceMode> relys = response.getRs();
                Log.v("kan", relys.isEmpty() ? "空 " : "非空" + " ");
                if (relys != null && !relys.isEmpty()) {
                    ishavereplyData = true;
                    if (relys.size() < count) {
                        relyList.addAll(relys);
                        adapterReply.setItems(relyList);
                        loadMoreContainer.loadMoreFinish(false, false);
                        viewText.setText("已加载全部");

                        return;
                    } else {
                        moreReply = true;
                        relyList.addAll(relys);
                        adapterReply.setItems(relyList);
                        loadMoreContainer.loadMoreFinish(false, true);
                    }
                } else {
                    if (!ishavereplyData) {
                        emptyView.setVisibility(View.VISIBLE);
                        emptyText.setText("还没有解答过任何问题 \n 现在开始帮助别人展示你的魅力吧");
                        viewText.setVisibility(View.GONE);
                    } else {
                        loadMoreContainer.loadMoreFinish(false, false);
                        viewText.setText("已加载全部");
                    }
                }
            }
        });
    }

    public View getHeaderView() {
        View headerView = inflater.inflate(R.layout.person_center_head, null);
        userPhoto = (ImageView) headerView.findViewById(R.id.person_photo);
        gender = (ImageView) headerView.findViewById(R.id.perosn_gender);
        countJiujie = (TextView) headerView.findViewById(R.id.count_jiujie);
        countReply = (TextView) headerView.findViewById(R.id.count_reply);
        countCollect = (TextView) headerView.findViewById(R.id.count_collect);
        radioGroup = (RadioGroup) headerView.findViewById(R.id.radio_group);
        return headerView;
    }

    @Override
    public void onRefreshStarted(View view) {

    }
}
