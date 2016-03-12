package com.jiujie8.choice.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiujie8.choice.ChoiceApplication;
import com.jiujie8.choice.R;
import com.jiujie8.choice.core.AbstractRoboAsyncTask;
import com.jiujie8.choice.core.Intents;
import com.jiujie8.choice.home.HomeActivity;
import com.jiujie8.choice.home.HomeServices;
import com.jiujie8.choice.home.entity.ChoiceItem;
import com.jiujie8.choice.home.entity.ChoiceMode;
import com.jiujie8.choice.home.entity.VoteResponse;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.persioncenter.LoginActivity;
import com.jiujie8.choice.persioncenter.PercenterActivityGridview;
import com.jiujie8.choice.publicentity.User;
import com.jiujie8.choice.view.PieGraph;
import com.jiujie8.choice.view.PieSlice;
import com.jiujie8.choice.view.ProgressPieView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlanjie on 14/12/4.
 */
public class ChoiceTypeUtil {

    private final static String YN = "YN";
    private final static String OR = "OR";
    private final static String TEXT = "TEXT";

    public static class ItemInfo {
        public View headerView;
        public View floorView;
        public TextView collectView;
        public TextView commentView;
        public TextView voteView;
        public PieGraph pieGraph;
        public View avatarLayout;
    }

    /**
     * 根据ChoiceMode choiceType 返回对应的view
     * @param context 上下文
     * @param mMode 实体类
     * @param listener view里的点击事件的回调
     * @return view
     */
    public final static ItemInfo showType(final Context context, final ChoiceMode mMode) {
        final ItemInfo mItemInfo = new ItemInfo();
        View v = null;
        View floorView = null;
        final ChoiceItem mItem = mMode.getChoice();
        if (mItem == null) {
            return null;
        }
        final String type = mItem.getChoiceType();
        final LayoutInflater mInflater = LayoutInflater.from(context);
        if (YN.equals(type)) {
            v = mInflater.inflate(R.layout.home_yn_header, null);
            final ImageView imageView = (ImageView) v.findViewById(R.id.home_yn_header_image);
            final ImageButton mRefreshButton = (ImageButton) v.findViewById(R.id.home_yn_refresh);
            final ProgressPieView mPieView = (ProgressPieView) v.findViewById(R.id.home_yn_header_progress);
            final PieGraph mPieGraphView = (PieGraph) v.findViewById(R.id.home_yn_header_pie_graph);
            mItemInfo.pieGraph = mPieGraphView;
            final TextView mLeftRatioText = (TextView) v.findViewById(R.id.home_yn_header_left_ratio_text);
            final ImageView mLeftRatioImage = (ImageView) v.findViewById(R.id.home_yn_header_left_ratio_image);
            final TextView mRightRatioText = (TextView) v.findViewById(R.id.home_yn_header_right_ratio_text);
            final ImageView mRightRatioImage = (ImageView) v.findViewById(R.id.home_yn_header_right_ratio_image);
            floorView = v.findViewById(R.id.home_yn_header_layout);
            ImageLoader.getInstance().displayImage(mItem.getImg(), imageView,ImageUtils.defaultImageLoader(), new SimpleImageLoading(mRefreshButton, mPieView), new SimpleImageLoadingProgress(mPieView));
            mRefreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageLoader.getInstance().displayImage(mItem.getImg(), imageView,ImageUtils.defaultImageLoader(), new SimpleImageLoading(mRefreshButton, mPieView), new SimpleImageLoadingProgress(mPieView));
                }
            });
            final View yesView = v.findViewById(R.id.home_yn_header_yes);
            final View noView = v.findViewById(R.id.home_yn_header_no);
            final View mRatioPieLayout = v.findViewById(R.id.home_yn_header_ratio_pie_layout);
            yesView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Map<String, String> mBodys = new HashMap<String, String>();
                    mBodys.put("choiceId", String.valueOf(mItem.getId()));
                    mBodys.put("value", "Y");
                    mBodys.put("type", mItem.getChoiceType());

                    new AbstractRoboAsyncTask<VoteResponse>(context){

                        @Override
                        protected VoteResponse run(Object data) throws Exception {
                            return (VoteResponse) HttpUtils.doRequest(HomeServices.createVoteRequest(mBodys)).result;
                        }

                        @Override
                        protected void onSuccessCallback(VoteResponse voteResponse) {
                            if (voteResponse.getEntity() != null && voteResponse.getEntity().getVote() != null) {
                                mMode.setVote(voteResponse.getEntity().getVote());
                            }
                            setSelfPieGraph(voteResponse.getEntity(), mItem, mItem.getUser(), mRatioPieLayout, mPieGraphView, yesView, noView, mLeftRatioText, mLeftRatioImage, mRightRatioText, mRightRatioImage, true);
                            if (context instanceof HomeActivity) {
                                ((HomeActivity) context).publishComment();
                            }
                        }
                    }.execute();
                }
            });
            noView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Map<String, String> mBodys = new HashMap<String, String>();
                    mBodys.put("choiceId", String.valueOf(mItem.getId()));
                    mBodys.put("value", "N");
                    mBodys.put("type", mItem.getChoiceType());

                    new AbstractRoboAsyncTask<VoteResponse>(context){

                        @Override
                        protected VoteResponse run(Object data) throws Exception {
                            return (VoteResponse) HttpUtils.doRequest(HomeServices.createVoteRequest(mBodys)).result;
                        }

                        @Override
                        protected void onSuccessCallback(VoteResponse voteResponse) {
                            if (voteResponse.getEntity() != null && voteResponse.getEntity().getVote() != null) {
                                mMode.setVote(voteResponse.getEntity().getVote());
                            }
                            setSelfPieGraph(voteResponse.getEntity(), mItem, mItem.getUser(), mRatioPieLayout, mPieGraphView, yesView, noView, mLeftRatioText, mLeftRatioImage, mRightRatioText, mRightRatioImage, true);
                            if (context instanceof HomeActivity) {
                                ((HomeActivity) context).publishComment();
                            }
                        }
                    }.execute();
                }
            });
            if (mItem.isVoteSupport()) {
                yesView.setVisibility(View.VISIBLE);
                noView.setVisibility(View.VISIBLE);
            } else {
                yesView.setVisibility(View.GONE);
                noView.setVisibility(View.GONE);
            }
            final User mUser = mItem.getUser();
            setSelfPieGraph(mMode, mItem, mUser, mRatioPieLayout, mPieGraphView, yesView, noView, mLeftRatioText, mLeftRatioImage, mRightRatioText, mRightRatioImage, false);
        } else if (OR.equals(type)) {
            boolean isSuccess = false;
            v = mInflater.inflate(R.layout.home_or_header, null);
            final PieGraph mPieGraphView = (PieGraph) v.findViewById(R.id.home_or_header_pie_graph);
            mItemInfo.pieGraph = mPieGraphView;
            final ImageView leftImageView = (ImageView) v.findViewById(R.id.home_or_header_left_image);
            final ImageButton mRefreshButton = (ImageButton) v.findViewById(R.id.home_or_header_refresh);
            final ProgressPieView mProgressPieView = (ProgressPieView) v.findViewById(R.id.home_or_header_progress);
            final TextView mLeftRatioText = (TextView) v.findViewById(R.id.home_or_header_left_ratio_text);
            final ImageView mLeftRatioImage = (ImageView) v.findViewById(R.id.home_or_header_left_ratio_image);
            final TextView mRightRatioText = (TextView) v.findViewById(R.id.home_or_header_right_ratio_text);
            final ImageView mRightRatioImage = (ImageView) v.findViewById(R.id.home_or_header_right_ratio_image);
            final View mRatioPieLayout = v.findViewById(R.id.home_or_header_ratio_pie_layout);
            floorView = v.findViewById(R.id.home_or_header_layout);
            ImageLoader.getInstance().displayImage(mItem.getLeftImg(), leftImageView, ImageUtils.defaultImageLoader(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            final ImageView rightImageView = (ImageView) v.findViewById(R.id.home_or_header_right_image);
            ImageLoader.getInstance().displayImage(mItem.getRightImg(), rightImageView, ImageUtils.defaultImageLoader(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            final View leftButton = v.findViewById(R.id.home_or_header_left_button);
            final View rightButton = v.findViewById(R.id.home_or_header_right_button);
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Map<String, String> mBodys = new HashMap<String, String>();
                    mBodys.put("choiceId", String.valueOf(mItem.getId()));
                    mBodys.put("value", "L");
                    mBodys.put("type", mItem.getChoiceType());

                    new AbstractRoboAsyncTask<VoteResponse>(context){

                        @Override
                        protected VoteResponse run(Object data) throws Exception {
                            return (VoteResponse) HttpUtils.doRequest(HomeServices.createVoteRequest(mBodys)).result;
                        }

                        @Override
                        protected void onSuccessCallback(VoteResponse voteResponse) {
                            setSelfPieGraph(voteResponse.getEntity(), mItem, mItem.getUser(), mRatioPieLayout, mPieGraphView, leftButton, rightButton, mLeftRatioText, mLeftRatioImage, mRightRatioText, mRightRatioImage, true);
                            if (context instanceof HomeActivity) {
                                ((HomeActivity) context).publishComment();
                            }
                        }
                    }.execute();
                }
            });
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Map<String, String> mBodys = new HashMap<String, String>();
                    mBodys.put("choiceId", String.valueOf(mItem.getId()));
                    mBodys.put("value", "R");
                    mBodys.put("type", mItem.getChoiceType());

                    new AbstractRoboAsyncTask<VoteResponse>(context){

                        @Override
                        protected VoteResponse run(Object data) throws Exception {
                            return (VoteResponse) HttpUtils.doRequest(HomeServices.createVoteRequest(mBodys)).result;
                        }

                        @Override
                        protected void onSuccessCallback(VoteResponse voteResponse) {
                            if (voteResponse.getEntity() != null && voteResponse.getEntity().getVote() != null) {
                                mMode.setVote(voteResponse.getEntity().getVote());
                            }
                            setSelfPieGraph(voteResponse.getEntity(), mItem, mItem.getUser(), mRatioPieLayout, mPieGraphView, leftButton, rightButton, mLeftRatioText, mLeftRatioImage, mRightRatioText, mRightRatioImage, true);
                            if (context instanceof HomeActivity) {
                                ((HomeActivity) context).publishComment();
                            }
                        }
                    }.execute();
                }
            });
            if (mItem.isVoteSupport()) {
                leftButton.setVisibility(View.VISIBLE);
                rightButton.setVisibility(View.VISIBLE);
            } else {
                leftButton.setVisibility(View.GONE);
                rightButton.setVisibility(View.GONE);
            }
            final User mUser = mItem.getUser();
            setSelfPieGraph(mMode, mItem, mUser, mRatioPieLayout, mPieGraphView, leftButton, rightButton, mLeftRatioText, mLeftRatioImage, mRightRatioText, mRightRatioImage, false);
        } else if (TEXT.equals(type)) {
            v = mInflater.inflate(R.layout.home_text_header, null);
            floorView = v.findViewById(R.id.home_text_header_layout);
            final PieGraph mPieGraphView = (PieGraph) v.findViewById(R.id.home_text_header_pie_graph);
            mItemInfo.pieGraph = mPieGraphView;
            final EditText mEditText = (EditText) v.findViewById(R.id.home_text_header_text);
            final ImageButton mRefreshButton = (ImageButton) v.findViewById(R.id.home_text_header_refresh);
            final ImageButton mYesButton = (ImageButton) v.findViewById(R.id.home_text_header_yes);
            final ImageButton mNoButton = (ImageButton) v.findViewById(R.id.home_text_header_no);
            final TextView mLeftRatioText = (TextView) v.findViewById(R.id.home_text_header_left_ratio_text);
            final ImageView mLeftRatioImage = (ImageView) v.findViewById(R.id.home_text_header_left_ratio_image);
            final TextView mRightRatioText = (TextView) v.findViewById(R.id.home_text_header_right_ratio_text);
            final ImageView mRightRatioImage = (ImageView) v.findViewById(R.id.home_text_header_right_ratio_image);
            final View mRatioPieLayout = v.findViewById(R.id.home_text_header_ratio_pie_layout);

            mYesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Map<String, String> mBodys = new HashMap<String, String>();
                    mBodys.put("choiceId", String.valueOf(mItem.getId()));
                    mBodys.put("value", "Y");
                    mBodys.put("type", mItem.getChoiceType());

                    new AbstractRoboAsyncTask<VoteResponse>(context){

                        @Override
                        protected VoteResponse run(Object data) throws Exception {
                            return (VoteResponse) HttpUtils.doRequest(HomeServices.createVoteRequest(mBodys)).result;
                        }

                        @Override
                        protected void onSuccessCallback(VoteResponse voteResponse) {
                            if (voteResponse.getEntity() != null && voteResponse.getEntity().getVote() != null) {
                                mMode.setVote(voteResponse.getEntity().getVote());
                            }
                            setSelfPieGraph(voteResponse.getEntity(), mItem, mItem.getUser(), mRatioPieLayout, mPieGraphView, mYesButton, mNoButton, mLeftRatioText, mLeftRatioImage, mRightRatioText, mRightRatioImage, true);
                            if (context instanceof HomeActivity) {
                                ((HomeActivity) context).publishComment();
                            }
                        }
                    }.execute();
                }
            });

            mNoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Map<String, String> mBodys = new HashMap<String, String>();
                    mBodys.put("choiceId", String.valueOf(mItem.getId()));
                    mBodys.put("value", "Y");
                    mBodys.put("type", mItem.getChoiceType());

                    new AbstractRoboAsyncTask<VoteResponse>(context){

                        @Override
                        protected VoteResponse run(Object data) throws Exception {
                            return (VoteResponse) HttpUtils.doRequest(HomeServices.createVoteRequest(mBodys)).result;
                        }

                        @Override
                        protected void onSuccessCallback(VoteResponse voteResponse) {
                            if (voteResponse.getEntity() != null && voteResponse.getEntity().getVote() != null) {
                                mMode.setVote(voteResponse.getEntity().getVote());
                            }
                            setSelfPieGraph(voteResponse.getEntity(), mItem, mItem.getUser(), mRatioPieLayout, mPieGraphView, mYesButton, mNoButton, mLeftRatioText, mLeftRatioImage, mRightRatioText, mRightRatioImage, true);
                            if (context instanceof HomeActivity) {
                                ((HomeActivity) context).publishComment();
                            }
                        }
                    }.execute();
                }
            });
            mEditText.setText(mItem.getDesc());
            mEditText.setBackgroundColor(Color.rgb(mItem.getBgColorRed(), mItem.getBgColorGreen(), mItem.getBgColorBlue()));
            if (mItem.isVoteSupport()) {
                mYesButton.setVisibility(View.VISIBLE);
                mNoButton.setVisibility(View.VISIBLE);
            } else {
                mYesButton.setVisibility(View.GONE);
                mNoButton.setVisibility(View.GONE);
            }
            final User mUser = mItem.getUser();
            setSelfPieGraph(mMode, mItem, mUser, mRatioPieLayout, mPieGraphView, mYesButton, mNoButton, mLeftRatioText, mLeftRatioImage, mRightRatioText, mRightRatioImage, false);
        }
        if (v != null) {
            final User mUser = mItem.getUser();
            if (mUser != null) {
                final View mAvatarLayout = v.findViewById(R.id.home_floor_header_avatar_layout);
                mItemInfo.avatarLayout = mAvatarLayout;
                final ImageView mAvatarImage = (ImageView) v.findViewById(R.id.home_floor_header_avatar);
                ImageLoader.getInstance().displayImage(mUser.getAvatar(), mAvatarImage, ImageUtils.avatarImageLoader());
                final TextView mNicknameText = (TextView) v.findViewById(R.id.home_floor_header_nickname);
                mNicknameText.setText(mUser.getNickname());
            }

            final TextView mContentView = (TextView) v.findViewById(R.id.home_floor_header_content);
            mContentView.setText(mItem.getTitle());
            final TextView mCreateTimeView = (TextView) v.findViewById(R.id.home_floor_header_create_time);
            mCreateTimeView.setText(mItem.getCreateTime());
            mItemInfo.collectView = (TextView) v.findViewById(R.id.home_floor_header_collect_number);
            mItemInfo.collectView.setText(String.valueOf(mItem.getFavoriteCnt()));
            mItemInfo.commentView = (TextView) v.findViewById(R.id.home_floor_header_comment_number);
            mItemInfo.commentView.setText(String.valueOf(mItem.getPostCnt()));
            mItemInfo.voteView = (TextView) v.findViewById(R.id.home_floor_header_vote_number);
            mItemInfo.voteView.setText(String.valueOf(mItem.getVoteCnt()));
            mItemInfo.floorView = floorView;
            mItemInfo.headerView = v;

            if (floorView != null) {
                final int width = context.getResources().getDisplayMetrics().widthPixels;
                final int height = (int) (width * 0.9);
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                floorView.setLayoutParams(params);
            }
        }
        return mItemInfo;
    }

    private static void setSelfPieGraph(final ChoiceMode mMode, final ChoiceItem mItem, final User mUser, final View mRatioPieLayout, final PieGraph mPieGraphView, final View mYesButton, final View mNoButton,
                                        final TextView mLeftRatioText, final ImageView mLeftRatioImage, final TextView mRightRatioText, final ImageView mRightRatioImage, final boolean isRequest) {
        final Context context = ChoiceApplication.getContext();
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final int screenWidth = metrics.widthPixels;
        int width = (int) ((screenWidth * 0.3) + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, metrics));
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(width, width);
        mParams.gravity = Gravity.CENTER;
        mPieGraphView.setLayoutParams(mParams);

        if (LoginUtil.getUserId().equals(mUser.getId())) {
            mYesButton.setVisibility(View.GONE);
            mNoButton.setVisibility(View.GONE);
            mPieGraphView.setVisibility(View.VISIBLE);

            mLeftRatioText.setVisibility(View.VISIBLE);
            mLeftRatioImage.setVisibility(View.VISIBLE);
            mRightRatioText.setVisibility(View.VISIBLE);
            mRightRatioImage.setVisibility(View.VISIBLE);
            setPieGraphSizeAndColor(context, mMode, mRatioPieLayout, mPieGraphView, mLeftRatioText, mRightRatioText, mLeftRatioImage, mRightRatioImage, true, isRequest);
         } else {
            if (mMode.getVote() == null) {
                mPieGraphView.setVisibility(View.GONE);
                mYesButton.setVisibility(View.VISIBLE);
                mNoButton.setVisibility(View.VISIBLE);
            } else {
                mPieGraphView.setVisibility(View.VISIBLE);
                mYesButton.setVisibility(View.GONE);
                mNoButton.setVisibility(View.GONE);
                mLeftRatioText.setVisibility(View.VISIBLE);
                mLeftRatioImage.setVisibility(View.VISIBLE);
                mRightRatioText.setVisibility(View.VISIBLE);
                mRightRatioImage.setVisibility(View.VISIBLE);
                setPieGraphSizeAndColor(context, mMode, mRatioPieLayout, mPieGraphView, mLeftRatioText, mRightRatioText, mLeftRatioImage, mRightRatioImage, false, isRequest);
            }
        }
    }

    private static void setPieGraphSizeAndColor(final Context context, final ChoiceMode mMode, final View mRatioPieLayout, final PieGraph mPieGraphView,
                                                final TextView mLeftRatioText, final TextView mRightRatioText, final ImageView mLeftRatioImage,
                                                final ImageView mRightRatioImage, final boolean isSelf, boolean isRequest) {
        final ChoiceItem mItem = mMode.getChoice();
        final Resources resources = context.getResources();
        final PieSlice slice = new PieSlice();
        final PieSlice mSlice = new PieSlice();

        final String type = mItem.getChoiceType();
        if ("OR".equals(type)) {
            final int totalCount = mItem.getVoteLeftCnt() + mItem.getVoteRightCnt();
            if (mItem.getVoteLeftCnt() == 0) {
                mLeftRatioText.setText("0%");
            } else {
                final int leftRatio = (int) (((float) mItem.getVoteLeftCnt() / (float) totalCount) * 100);
                mLeftRatioText.setText(leftRatio + "%" );
            }

            slice.setValue(mItem.getVoteLeftCnt());
            mSlice.setValue(mItem.getVoteRightCnt());

            if (mItem.getVoteRightCnt() == 0) {
                mRightRatioText.setText("0%");
            } else {
                final int rightRatio = (int) (((float) mItem.getVoteRightCnt() / (float) totalCount) * 100);
                mRightRatioText.setText(rightRatio + "%");
            }
            mLeftRatioImage.setImageResource(R.drawable.icon_tagleftarrowlight_choice);
            mRightRatioImage.setImageResource(R.drawable.icon_tagrightarrowlight_choice);
        } else {
            final int totalCount = mItem.getVoteYesCnt() + mItem.getVoteNoCnt();
            if (mItem.getVoteYesCnt() == 0) {
                mLeftRatioText.setText("0%");
            } else {
                final int leftRatio = (int) (((float) mItem.getVoteYesCnt() / (float) totalCount) * 100);
                mLeftRatioText.setText(leftRatio + "%");
            }

            slice.setValue(mItem.getVoteYesCnt());
            mSlice.setValue(mItem.getVoteNoCnt());

            if (mItem.getVoteNoCnt() == 0) {
                mRightRatioText.setText("0%");
            } else {
                final int rightRatio = (int) (((float) mItem.getVoteNoCnt() / (float) totalCount) * 100);
                mRightRatioText.setText(rightRatio + "%");
            }
            mLeftRatioImage.setImageResource(R.drawable.icon_tagyeslight_choice);
            mRightRatioImage.setImageResource(R.drawable.icon_tagnolight_choice);
        }

        if (mMode.getVote() != null) {
            final String value = mMode.getVote().getValue();
            if ("L".equals(value)) {
                mLeftRatioImage.setImageResource(R.drawable.icon_tagleftarrow_choice);
                if (!isSelf) {
                    slice.setBold(true);
                }
                mLeftRatioText.setTextColor(resources.getColor(R.color.light_red));
            } else if ("R".equals(value)) {
                mRightRatioImage.setImageResource(R.drawable.icon_tagrightarrow_choice);
                if (!isSelf) {
                    mSlice.setBold(true);
                }
                mRightRatioText.setTextColor(resources.getColor(R.color.blue));
            } else if ("Y".equals(value)) {
                mLeftRatioImage.setImageResource(R.drawable.icon_tagyes_choice);
                if (!isSelf) {
                    slice.setBold(true);
                }
                mLeftRatioText.setTextColor(resources.getColor(R.color.light_red));
            } else if ("N".equals(value)) {
                mRightRatioImage.setImageResource(R.drawable.icon_tagno_choice);
                if (!isSelf) {
                    mSlice.setBold(true);
                }
                mRightRatioText.setTextColor(resources.getColor(R.color.blue));
            }
        }

        mSlice.setColor(resources.getColor(R.color.blue));
        slice.setColor(resources.getColor(R.color.light_red));

        final ArrayList<PieSlice> pieSlices = new ArrayList<PieSlice>();
        pieSlices.add(mSlice);
        pieSlices.add(slice);
        mPieGraphView.setSlices(pieSlices);
        if (isRequest) {
            mPieGraphView.setDuration(500);//default if unspecified is 300 ms
            mPieGraphView.setInterpolator(new AccelerateDecelerateInterpolator());
            mPieGraphView.animateToGoalValues();
        }
        if (mPieGraphView.getVisibility() == View.VISIBLE) {
            mRatioPieLayout.setBackgroundColor(context.getResources().getColor(R.color.front_backgroud));
        } else {
            mRatioPieLayout.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        }
    }

    private static class SimpleImageLoadingProgress implements ImageLoadingProgressListener {

        private final ProgressPieView mPieView;

        public SimpleImageLoadingProgress(ProgressPieView mPieView) {
            this.mPieView = mPieView;
        }

        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {
            mPieView.setMax(total);
            mPieView.setProgress(current);
        }
    }

    private static class SuccessSimpleImageLoading implements ImageLoadingListener {

        private final ImageButton mRefreshButton;
        private final ProgressPieView mPieView;
        private boolean isSuccess;

        public SuccessSimpleImageLoading(final ImageButton refreshButton, final ProgressPieView mPieView, boolean isSuccess) {
            mRefreshButton = refreshButton;
            this.mPieView = mPieView;
            this.isSuccess = isSuccess;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            isSuccess = true;
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    }

    private static class SimpleImageLoading implements ImageLoadingListener {

        private final ImageButton mRefreshButton;
        private final ProgressPieView mPieView;

        public SimpleImageLoading(final ImageButton refreshButton, final ProgressPieView mPieView) {
            mRefreshButton = refreshButton;
            this.mPieView = mPieView;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {
            mRefreshButton.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            mRefreshButton.setVisibility(View.VISIBLE);
            mPieView.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            mRefreshButton.setVisibility(View.GONE);
            mPieView.setVisibility(View.GONE);
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            mRefreshButton.setVisibility(View.VISIBLE);
            mPieView.setVisibility(View.GONE);
        }
    }

    public interface TypeOnClickListener {
        /**
         * yes button OnClickListener
         * @param view yes view
         */
        public void yesOnClick(View view);

        /**
         * no button OnClickListener
         * @param view no button
         */
        public void noOnClick(View view);

        /**
         * left button OnClickListener
         * @param view left button
         */
        public void leftOnClick(View view);

        /**
         * right button OnClickListener
         * @param view right button
         */
        public void rightOnClick(View view);
    }

    /**
     * 简单实现的点击事件的类
     */
    public class SimpleTypeOnClickListener implements TypeOnClickListener {
        @Override
        public void yesOnClick(View view) {

        }

        @Override
        public void noOnClick(View view) {

        }

        @Override
        public void leftOnClick(View view) {

        }

        @Override
        public void rightOnClick(View view) {

        }
    }
}
