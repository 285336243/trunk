package com.mzs.guaji.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.PagedRequest;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.SingleEmptyAdapter;
import com.mzs.guaji.adapter.StarDetailAdapter;
import com.mzs.guaji.core.SingleListActivity;
import com.mzs.guaji.core.FragmentProvider;
import com.mzs.guaji.core.HeaderFooterListAdapter;
import com.mzs.guaji.core.PageIterator;
import com.mzs.guaji.core.ResourcePager;
import com.mzs.guaji.entity.CelebrityPost;
import com.mzs.guaji.entity.Star;
import com.mzs.guaji.topic.TopicDetailsActivity;
import com.mzs.guaji.topic.entity.StarDetail;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-3-12.
 */
public class StarActivity extends SingleListActivity<StarDetail> implements PullToRefreshBase.OnRefreshListener<ListView>, PullToRefreshBase.OnLastItemVisibleListener {

    @Inject
    Context context;
    private int width;
    private Star star;
    @InjectView(R.id.tv_star_back)
    TextView backText;

    @InjectView(R.id.tv_star_title)
    TextView titleText;

    private List<CelebrityPost> posts = new ArrayList<CelebrityPost>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        star = (Star) getSerializableExtra("star");
        super.onCreate(savedInstanceState);
        setEmptyText(R.string.star_empty);
        WindowManager mWindowManger = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics mMetrics = new DisplayMetrics();
        mWindowManger.getDefaultDisplay().getMetrics(mMetrics);
        width = mMetrics.widthPixels;
        listView.setDivider(null);
        listView.setDividerHeight(0);
        configureList(this, getListView());
        titleText.setText(star.getName());
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshListView.setOnRefreshListener(this);
        refreshListView.setOnLastItemVisibleListener(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.star_list_layout;
    }

    @Override
    protected ResourcePager<StarDetail> createPager() {
        return new ResourcePager<StarDetail>() {
            @Override
            protected Object getId(StarDetail resource) {
                return null;
            }

            @Override
            public PageIterator<StarDetail> createIterator(int page, int size) {
                PagedRequest<StarDetail> request = new PagedRequest<StarDetail>();
                request.setUri(getStarRequest(star.getId(), requestPage, requestCount));
                request.setClazz(StarDetail.class);
                return new PageIterator<StarDetail>(context, request);
            }
        };
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.star_error;
    }

    @Override
    protected int getLoadingMessage() {
        return R.string.star_loading;
    }

    @Override
    protected SingleTypeAdapter<?> createAdapter() {
        return new StarDetailAdapter(this);
    }

    @Override
    public void onLoadFinished(Loader<StarDetail> loader, StarDetail data) {
        super.onLoadFinished(loader, data);
        refreshListView.onRefreshComplete();
        loadingIndicator.setVisible(false);
        if (data != null && data.getPosts() != null) {
            if (data.getPosts().size() > 0) {
                this.items = data.getPosts();
//                posts.addAll(data.getPosts());
                setListItemClickListener(posts);
                getListAdapter().getWrappedAdapter().setItems(posts);
                loadingIndicator.setVisible(false);
            }
            showList();
        } else {
            List<Object> objects = new ArrayList<Object>();
            objects.add(new Object());
            this.items = objects;
            SingleTypeAdapter<?> wrapped = new SingleEmptyAdapter(this);
            HeaderFooterListAdapter<SingleTypeAdapter<?>> adapter = new HeaderFooterListAdapter<SingleTypeAdapter<?>>(getListView(), wrapped);
            adapter.addHeader(adapterHeaderView());
            listView.setAdapter(adapter);
            ViewUtils.setGone(progressBar, true);
            ViewUtils.setGone(refreshListView, false);
            ViewUtils.setGone(listView, false);
        }
    }

    private void setListItemClickListener(final List<CelebrityPost> posts) {
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, TopicDetailsActivity.class);
                Topic topic = posts.get(position-1).getTopic();
                if (topic != null) {
                    intent.putExtra("topicId", topic.getId());
                }
                startActivity(intent);
            }
        };
        listView.setOnItemClickListener(itemClickListener);
    }

    @Override
    protected boolean isAddAdapterHeader() {
        return true;
    }

    @Override
    protected View adapterHeaderView() {
        View view = getLayoutInflater().inflate(R.layout.star_header_layout, null);
        RelativeLayout parentLayout = (RelativeLayout) view.findViewById(R.id.rl_star_parent);
        parentLayout.setLayoutParams(new AbsListView.LayoutParams(width, width * 11 / 16));
        ImageView backgroundImage = (ImageView) view.findViewById(R.id.iv_star_background);
        ImageView avatarImage = (ImageView) view.findViewById(R.id.iv_star_avatar);
        TextView nicknameText = (TextView) view.findViewById(R.id.tv_star_nickname);
        TextView signatureText = (TextView) view.findViewById(R.id.tv_star_signature);
        if (star != null) {
            if (!TextUtils.isEmpty(star.getBgImg())) {
                ImageLoader.getInstance().displayImage(star.getBgImg(), backgroundImage, ImageUtils.imageLoader(this, 0));
            }
            if (!TextUtils.isEmpty(star.getAvatar())) {
                ImageLoader.getInstance().displayImage(star.getAvatar(), avatarImage, ImageUtils.imageLoader(this, 1000));
            }
            nicknameText.setText(star.getName());
            signatureText.setText(star.getSignature());
        }
        return view;
    }

    @Override
    protected FragmentProvider getProvider() {
        return null;
    }

    private String getStarRequest(long id, long page, long count) {
        return "celebrity/posts.json?id=" + id +"&p=" + page + "&cnt=" + count;
    }

    @Override
    public void onLastItemVisible() {
        requestPage = requestPage + 1;
        loadingIndicator.setVisible(true);
        forceRefresh();
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        requestPage = 1;
        posts.clear();
        forceRefresh();
    }
}
