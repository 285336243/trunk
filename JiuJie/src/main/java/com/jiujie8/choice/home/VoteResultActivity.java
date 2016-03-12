package com.jiujie8.choice.home;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.jiujie8.choice.R;
import com.jiujie8.choice.core.DialogFragmentActivity;
import com.jiujie8.choice.core.ThrowableLoader;
import com.jiujie8.choice.core.ToastUtils;
import com.jiujie8.choice.home.entity.ChoiceItem;
import com.jiujie8.choice.home.entity.ChoiceMode;
import com.jiujie8.choice.home.entity.VoteResultItem;
import com.jiujie8.choice.home.entity.VoteResultResponse;
import com.jiujie8.choice.http.GsonRequest;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.publicentity.User;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.view.PieGraph;
import com.jiujie8.choice.view.PieSlice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 15/1/13.
 * 投票结果的页面
 */
@ContentView(R.layout.vote_result_layout)
public class VoteResultActivity extends DialogFragmentActivity {

    private ChoiceMode mMode;

    private final static String LEFT = "L";
    private final static String RIGHT = "R";
    private final static String YES = "Y";
    private final static String NO = "N";

    @InjectView(R.id.vote_result_pie_layout)
    private FrameLayout mPieGraphLayout;

    @InjectView(R.id.vote_result_pie_graph)
    private PieGraph mPieGraphView;

    @InjectView(R.id.vote_result_vote_count)
    private TextView mVoteCountText;

    @InjectView(R.id.vote_result_left_ratio_text)
    private TextView mLeftRatioText;

    @InjectView(R.id.vote_result_left_ratio_image)
    private ImageView mLeftRatioImage;

    @InjectView(R.id.vote_result_right_ratio_text)
    private TextView mRightRatioText;

    @InjectView(R.id.vote_result_right_ratio_image)
    private ImageView mRightRatioImage;

    @InjectView(R.id.vote_result_yes_image)
    private TextView mYesImageView;

    @InjectView(R.id.vote_result_yes_grid)
    private GridView mYesGridView;

    @InjectView(R.id.vote_result_no_image)
    private TextView mNoImageView;

    @InjectView(R.id.vote_result_no_grid)
    private GridView mNoGridView;

    @Inject
    private VoteResultAdapter mYesAdapter;

    @Inject
    private VoteResultAdapter mNoAdapter;

    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.btn_back_choice);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mMode = (ChoiceMode) getSerializableExtra(IConstant.MODE_ITEM);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        int width = (int) ((screenWidth * 0.3) + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, metrics));
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(width, width);
        mParams.gravity = Gravity.CENTER;
        mPieGraphLayout.setLayoutParams(mParams);
        mYesGridView.setAdapter(mYesAdapter);
        mNoGridView.setAdapter(mNoAdapter);
        setPieGraphSizeAndColor();
        getSupportLoaderManager().initLoader(0 ,null, mCallback);
        mYesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User mUser = (User) parent.getItemAtPosition(position);
                startActivity(new Intent());
            }
        });
        mNoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    /**
     * Loader Callbacks
     */
    LoaderManager.LoaderCallbacks<VoteResultResponse> mCallback = new LoaderManager.LoaderCallbacks<VoteResultResponse>() {
        @Override
        public Loader<VoteResultResponse> onCreateLoader(int id, Bundle args) {
            return new ThrowableLoader<VoteResultResponse>(activity) {
                @Override
                public VoteResultResponse loadData() throws Exception {
                    final Map<String, String> mBodys = new HashMap<String, String>();
                    mBodys.put("choiceId", String.valueOf(mMode.getChoice().getId()));
                    final GsonRequest<VoteResultResponse> mRequest = HomeServices.createVoteResultRequest(mBodys);
                    return (VoteResultResponse) HttpUtils.doRequest(mRequest).result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<VoteResultResponse> loader, VoteResultResponse data) {
            if (data != null) {
                if (IConstant.STATE_OK.equals(data.getCode())) {
                    for (VoteResultItem mItem : data.getRs()) {
                        if (mItem != null) {
                            setGridAdapter(mItem);
                        }
                    }
                } else {
                    ToastUtils.show(activity, data.getMessage());
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<VoteResultResponse> loader) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置GridView的数据和高度
     * @param mItem
     */
    private void setGridAdapter(VoteResultItem mItem) {
        final int count = (mItem.getUser().size() + 5) / 6;
        final int avatarHeight = (screenWidth - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7 * 12, getResources().getDisplayMetrics())) / 6;
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(screenWidth, avatarHeight * count + (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (count - 1) * 12, getResources().getDisplayMetrics()));
        if (LEFT.equals(mItem.getLabel()) || YES.equals(mItem.getLabel())) {
            mYesGridView.setLayoutParams(mParams);
            mYesAdapter.addItems(0, mItem.getUser());
        } else if (RIGHT.equals(mItem.getLabel()) || NO.equals(mItem.getLabel())) {
            mNoGridView.setLayoutParams(mParams);
            mNoAdapter.addItems(0, mItem.getUser());
        }
    }

    /**
     * 设置饼图的大小和颜色
     */
    private void setPieGraphSizeAndColor() {
        final ChoiceItem mItem = mMode.getChoice();
        final Resources resources = getResources();
        final PieSlice slice = new PieSlice();
        final PieSlice mSlice = new PieSlice();

        int count;
        final String type = mItem.getChoiceType();
        if ("OR".equals(type)) {
            final int totalCount = mItem.getVoteLeftCnt() + mItem.getVoteRightCnt();
            if (mItem.getVoteLeftCnt() == 0) {
                mLeftRatioText.setText("0%");
            } else {
                final int leftRatio = (int) (((float) mItem.getVoteLeftCnt() / (float) totalCount) * 100);
                mLeftRatioText.setText(leftRatio + "%" );
            }

            count = mItem.getVoteLeftCnt() + mItem.getVoteRightCnt();
            slice.setValue(mItem.getVoteLeftCnt());
            mSlice.setValue(mItem.getVoteRightCnt());

            if (mItem.getVoteRightCnt() == 0) {
                mRightRatioText.setText("0%");
            } else {
                final int rightRatio = (int) (((float) mItem.getVoteRightCnt() / (float) totalCount) * 100);
                mRightRatioText.setText(rightRatio + "%");
            }
            mLeftRatioImage.setImageResource(R.drawable.icon_tagleftarrow_choice);
            mRightRatioImage.setImageResource(R.drawable.icon_tagrightarrow_choice);
            mYesImageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_tagleftarrow_choice, 0, 0, 0);
            mNoImageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_tagrightarrow_choice, 0, 0, 0);
        } else {
            final int totalCount = mItem.getVoteYesCnt() + mItem.getVoteNoCnt();
            if (mItem.getVoteYesCnt() == 0) {
                mLeftRatioText.setText("0%");
            } else {
                final int leftRatio = (int) (((float) mItem.getVoteYesCnt() / (float) totalCount) * 100);
                mLeftRatioText.setText(leftRatio + "%");
            }

            count = mItem.getVoteYesCnt() + mItem.getVoteNoCnt();
            slice.setValue(mItem.getVoteYesCnt());
            mSlice.setValue(mItem.getVoteNoCnt());

            if (mItem.getVoteNoCnt() == 0) {
                mRightRatioText.setText("0%");
            } else {
                final int rightRatio = (int) (((float) mItem.getVoteNoCnt() / (float) totalCount) * 100);
                mRightRatioText.setText(rightRatio + "%");
            }
            mLeftRatioImage.setImageResource(R.drawable.icon_tagyes_choice);
            mRightRatioImage.setImageResource(R.drawable.icon_tagno_choice);
            mYesImageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_tagyes_choice, 0, 0, 0);
            mNoImageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_tagno_choice, 0, 0, 0);
        }

        if (mMode.getVote() != null) {
            final String value = mMode.getVote().getValue();
            if ("L".equals(value)) {
                slice.setColor(resources.getColor(R.color.light_red));
                mSlice.setColor(resources.getColor(R.color.blue));
            } else if ("R".equals(value)) {
                mSlice.setColor(resources.getColor(R.color.blue));
                slice.setColor(resources.getColor(R.color.light_red));
            } else if ("Y".equals(value)) {
                slice.setColor(resources.getColor(R.color.light_red));
                mSlice.setColor(resources.getColor(R.color.blue));
            } else if ("N".equals(value)) {
                mSlice.setColor(resources.getColor(R.color.blue));
                slice.setColor(resources.getColor(R.color.light_red));
            }
        }

        mVoteCountText.setText(String.valueOf(count));
        final ArrayList<PieSlice> pieSlices = new ArrayList<PieSlice>();
        pieSlices.add(mSlice);
        pieSlices.add(slice);
        mPieGraphView.setSlices(pieSlices);
    }
}
