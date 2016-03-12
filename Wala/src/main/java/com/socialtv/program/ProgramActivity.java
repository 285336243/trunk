package com.socialtv.program;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.Intents;
import com.socialtv.core.ThirdPartyShareActivity;
import com.socialtv.core.ThrowableLoader;
import com.socialtv.core.ToastUtils;
import com.socialtv.home.BannerAdapter;
import com.socialtv.home.entity.Banner;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.program.entity.Group;
import com.socialtv.program.entity.Join;
import com.socialtv.program.entity.ProgramHeader;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-8.
 * 节目组的页面
 */
@ContentView(R.layout.program)
public class ProgramActivity extends ThirdPartyShareActivity implements LoaderManager.LoaderCallbacks<ProgramHeader> {

    private final static int REFRESH_MENU = 0;

    private final static int JOIN_MENU = 1;

    @Inject
    private Activity activity;

    @Inject
    private ProgramServices services;

    @InjectExtra(value = IConstant.PROGRAM_ID, optional = true)
    private String programId;

    @InjectView(R.id.program_pager)
    private ViewPager viewPager;

    @InjectView(R.id.program_banner)
    private ViewPager bannerPager;

    @InjectView(R.id.program_banner_indicator)
    private CirclePageIndicator indicator;

    @InjectView(R.id.program_group)
    private RadioGroup radioGroup;

    @InjectView(R.id.program_group_line)
    private View groupLineView;

    @InjectView(R.id.program_group_chat)
    private RadioButton chatButton;

    @InjectView(R.id.program_group_topic)
    private RadioButton topicButton;

    @InjectView(R.id.program_group_news)
    private RadioButton newsButton;

    @InjectView(R.id.program_banner_view)
    private BannerView bannerView;

    @InjectView(R.id.program_banner_name)
    private TextView nameTextView;

    @InjectView(R.id.program_banner_tag)
    private TextView tagTextView;

    @InjectView(R.id.program_banner_topic_count)
    private TextView topicCountTextView;

    @InjectView(R.id.program_banner_member_count)
    private TextView memberCountTextView;

    private ProgramPagerAdapter adapter;

    DisplayMetrics metrics = new DisplayMetrics();

    @Inject
    private BannerAdapter bannerAdapter;

    private ArrayList<Banner> bannerList;

    private ProgramHeader programHeader;

    private final static int[] RADIO_ID = new int[]{
            R.id.program_group_chat,
            R.id.program_group_topic,
            R.id.program_group_news
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        bannerList = new ArrayList<Banner>();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        adapter = new ProgramPagerAdapter(getSupportFragmentManager(), programId, bannerView);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

        bannerPager.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, metrics.widthPixels / 2));
        bannerPager.setAdapter(bannerAdapter);
        indicator.setViewPager(bannerPager);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) {
                    hide(nameTextView);
                    hide(tagTextView);
                    hide(topicCountTextView);
                    hide(memberCountTextView);
                } else {
                    show(nameTextView);
                    show(tagTextView);
                    show(topicCountTextView);
                    show(memberCountTextView);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                radioGroup.check(RADIO_ID[position]);
//                bannerView.beginScroller();
                inputMethodManager.hideSoftInputFromWindow(viewPager.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                int y = metrics.widthPixels / 2;
                if (position == 0) {
                    ProgramChatFragment chatFragment = (ProgramChatFragment) adapter.instantiateItem(viewPager, position);
                    chatFragment.scrollView(-y);
                } else if (position == 1) {
                    ProgramTopicFragment topicFragment = (ProgramTopicFragment) adapter.instantiateItem(viewPager, position);
                    topicFragment.scrollView(-y);
                } else if (position == 2) {
                    ProgramNewsFragment newsFragment = (ProgramNewsFragment) adapter.instantiateItem(viewPager, position);
                    newsFragment.scrollView(-y);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.program_group_chat) {
                    viewPager.setCurrentItem(0);
                } else if (checkedId == R.id.program_group_topic) {
                    viewPager.setCurrentItem(1);
                } else if (checkedId == R.id.program_group_news) {
                    viewPager.setCurrentItem(2);
                }
            }
        });
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, REFRESH_MENU, 0, "刷新").setIcon(R.drawable.btn_lightrefresh_tomato).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        if (programHeader != null && programHeader.getGroup() != null) {
            if (programHeader.getGroup().getIsFollow() == 0) {
                menu.add(Menu.NONE, JOIN_MENU, 1, "加入").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            } else {
                menu.add(Menu.NONE, JOIN_MENU, 1, "已加入").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                break;
            case JOIN_MENU:
                if (LoginUtil.isLogin(activity)) {
                    joinRequest(programHeader);
                } else {
                    startActivity(new Intents(activity, LoginActivity.class).toIntent());
                }
                break;
            case REFRESH_MENU:
                refreshFragmentList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshFragmentList() {
        if (chatButton.isChecked()) {
            ProgramChatFragment chatFragment = (ProgramChatFragment) adapter.instantiateItem(viewPager, 0);
            chatFragment.refreshList();
        } else if (topicButton.isChecked()) {
            ProgramTopicFragment topicFragment = (ProgramTopicFragment) adapter.instantiateItem(viewPager, 1);
            topicFragment.refreshList();
        } else if (newsButton.isChecked()){
            ProgramNewsFragment newsFragment = (ProgramNewsFragment) adapter.instantiateItem(viewPager, 2);
            newsFragment.refreshList();
        }
    }

    private void joinRequest(ProgramHeader header) {
        if (header.getGroup().getIsFollow() == 1) {
            new AbstractRoboAsyncTask<Join>(activity) {
                /**
                 * Execute task with an authenticated account
                 *
                 * @param data
                 * @return result
                 * @throws Exception
                 */
                @Override
                protected Join run(Object data) throws Exception {
                    return (Join) HttpUtils.doRequest(services.createProgramUnJoinRequest(programId)).result;
                }

                @Override
                protected void onSuccess(Join join) throws Exception {
                    super.onSuccess(join);
                    if (join != null) {
                        if (join.getResponseCode() == 0) {
                            setResult(RESULT_OK);
                            memberCountTextView.setText(join.getFollowCnt() + " 成员");
                        } else {
                            ToastUtils.show(activity, join.getResponseMessage());
                        }
                    }
                }
            }.execute();
            header.getGroup().setIsFollow(0);
        } else {
            new AbstractRoboAsyncTask<Join>(activity) {
                /**
                 * Execute task with an authenticated account
                 *
                 * @param data
                 * @return result
                 * @throws Exception
                 */
                @Override
                protected Join run(Object data) throws Exception {
                    return (Join) HttpUtils.doRequest(services.createProgramJoinRequest(programId)).result;
                }

                @Override
                protected void onSuccess(Join join) throws Exception {
                    super.onSuccess(join);
                    if (join != null) {
                        if (join.getResponseCode() == 0) {
                            setResult(RESULT_OK);
                            memberCountTextView.setText(join.getFollowCnt() + " 成员");
                        } else {
                            ToastUtils.show(activity, join.getResponseMessage());
                        }
                    }
                }
            }.execute();
            header.getGroup().setIsFollow(1);
        }
        supportInvalidateOptionsMenu();
    }

    private void refreshHeader() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<ProgramHeader> onCreateLoader(int id, Bundle args) {
        return new ThrowableLoader<ProgramHeader>(this) {
            @Override
            public ProgramHeader loadData() throws Exception {
                return (ProgramHeader) HttpUtils.doRequest(services.createProgramHeaderRequest(programId)).result;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ProgramHeader> loader, ProgramHeader data) {
        if (data != null) {
            if (data.getResponseCode() == 0) {
                this.programHeader = data;
                show(radioGroup);
                show(groupLineView);
                supportInvalidateOptionsMenu();
                Group group = data.getGroup();
                if (group != null) {
                    nameTextView.setText(group.getName());
                    tagTextView.setText(group.getTag());
                    topicCountTextView.setText(group.getTopicsCnt() + " 话题");
                    memberCountTextView.setText(group.getFollowCnt() + " 成员");
                    Banner banner = new Banner();
                    banner.setImg(group.getImg());
                    banner.setName(group.getName());
                    bannerList.add(banner);
                    if (group.getBanners() != null && !group.getBanners().isEmpty())
                        bannerList.addAll(group.getBanners());
                    bannerAdapter.setItems(bannerList);
                }
            } else {
                ToastUtils.show(activity, data.getResponseMessage());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<ProgramHeader> loader) {

    }
}
