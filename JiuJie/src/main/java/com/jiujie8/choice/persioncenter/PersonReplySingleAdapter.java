package com.jiujie8.choice.persioncenter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.core.ToastUtils;
import com.jiujie8.choice.core.Utils;
import com.jiujie8.choice.home.entity.ChoiceItem;
import com.jiujie8.choice.home.entity.ChoiceMode;
import com.jiujie8.choice.myjiujie.JiuJieDetailActivity;

import com.jiujie8.choice.util.HttpHelp;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class PersonReplySingleAdapter extends SingleTypeAdapter<ChoiceMode> {

    private static final int PIC = 0;
    private static final int WORD = 1;
    private static final int FAVORITE = 2;
    private static final int COMMENT = 3;
    private static final int VOTE = 4;
    private static final int OR = 5;
    private static final int LEFTIMAGE = 6;
    private static final int RIGHTIMAGE = 7;
    private static final int APPROVE = 9;
    private static final int COMMENT_TEXT = 10;
    private static final int ICONS = 11;
    private static final int LAYOUT = 12;
    private static final String CHECK_SUPPORT_STATUS = "choice/user/checkPostStatus";
    private final Activity activity;
    private final int width;
    private final Resources res;
    private ImageLoader imageLoader;

    @Inject
    public PersonReplySingleAdapter(Activity activity) {
        super(activity, R.layout.layout_adapter_personreply);
        imageLoader = ImageLoader.getInstance();
        this.activity = activity;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width = metrics.widthPixels;
        res = activity.getResources();
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{
                R.id.imageview_pic,//纠结图片            0
                R.id.textview_word,//纠结文字          1
                R.id.textview_favorite,//纠结收藏数据   2
                R.id.textview_comment,//纠结评论数      3
                R.id.textview_vote,  //纠结投票数        4
                R.id.layout_or,   //or布局               5
                R.id.imageview_left,   //or 中左边图片     6
                R.id.imageview_right,  //or 中右边图片     7
                R.id.single__item_layout,  //单个布局  8
                R.id.approve_imageview,  //赞成view   9
                R.id.approve_textview,  //有人赞同你的评论 10
                R.id.bottom_icons,      //底部图标 11
                R.id.single__item_layout  //整个布局  12

        };
    }

    @Override
    protected void update(int position, final ChoiceMode item) {
        //设置布局参数，不然按内容填充
        int intenalmWidrh = (width - Utils.dip2px(activity, 2)) / 2;
        FrameLayout.LayoutParams marginPara = new FrameLayout.LayoutParams(intenalmWidrh, (int) (intenalmWidrh * 0.9));
        view(8).setLayoutParams(marginPara);

        if (item != null) {
            final ChoiceItem choice = item.getChoice();
            //点击跳转到详情
            view(LAYOUT).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(IConstant.ChOICE_MODE,item);
                    activity.startActivityForResult(new Intent(activity, JiuJieDetailActivity.class).putExtras(mBundle), IConstant.SELF_REPLY_CODE);
                    if(item.getPostAgreeStatus()==2){
                        Map<String, String> map=new HashMap<String, String>();
                        map.put("choiceId",String.valueOf(choice.getId()));
                        HttpHelp.getInstance().getHttp(activity, Response.class,CHECK_SUPPORT_STATUS,map,new HttpHelp.OnCompleteListener<Response>() {
                            @Override
                            public void onComplete(Response response) {
                                ToastUtils.show(activity,"点赞成功");
                            }
                        });
                    }
                }
            });
            if (choice.getChoiceType().equals("TEXT")) {
                view(PIC).setVisibility(View.GONE);
                view(OR).setVisibility(View.GONE);
                textView(WORD).setVisibility(View.VISIBLE);
                textView(WORD).setText(choice.getDesc());
                textView(WORD).setBackgroundColor(Color.rgb(choice.getBgColorRed(), choice.getBgColorGreen(), choice.getBgColorBlue()));
                setText(FAVORITE, String.valueOf(choice.getFavoriteCnt()));
                setText(COMMENT, String.valueOf(choice.getPostCnt()));
                setText(VOTE, String.valueOf(choice.getVoteCnt()));
            }
            if (choice.getChoiceType().equals("YN")) {
                textView(WORD).setVisibility(View.GONE);
                view(OR).setVisibility(View.GONE);
                view(PIC).setVisibility(View.VISIBLE);
                imageLoader.displayImage(choice.getImg(), imageView(PIC), ImageUtils.defaultImageLoader());
                setText(FAVORITE, String.valueOf(choice.getFavoriteCnt()));
                setText(COMMENT, String.valueOf(choice.getPostCnt()));
                setText(VOTE, String.valueOf(choice.getVoteCnt()));

            }
            if (choice.getChoiceType().equals("OR")) {
                view(PIC).setVisibility(View.GONE);
                textView(WORD).setVisibility(View.GONE);
                view(OR).setVisibility(View.VISIBLE);
                imageLoader.displayImage(choice.getLeftImg(), imageView(LEFTIMAGE), ImageUtils.defaultImageLoader());
                imageLoader.displayImage(choice.getRightImg(), imageView(RIGHTIMAGE), ImageUtils.defaultImageLoader());
                setText(FAVORITE, String.valueOf(choice.getFavoriteCnt()));
                setText(COMMENT, String.valueOf(choice.getPostCnt()));
                setText(VOTE, String.valueOf(choice.getVoteCnt()));
            }
//            setText(FAVORITE, String.valueOf(choice.getFavoriteCnt()));
//            setText(COMMENT, String.valueOf(choice.getPostCnt()));
//            setText(VOTE, String.valueOf(choice.getVoteCnt()));
            Drawable drawable;
 /*           if(choice.getPostStatus()==2){
                 drawable = res.getDrawable(R.drawable.icon_procomact_choice);
                //设置评论TextView的drawableleft
                textView(COMMENT).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }else {*/
                drawable = res.getDrawable(R.drawable.icon_procom_choice);
                //设置评论TextView的drawableleft
                textView(COMMENT).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
//            }
//            if(choice.getVoteStatus()==2){
//                drawable = res.getDrawable(R.drawable.icon_provoteact_choice);
//                //设置投票TextView的drawableleft
//                textView(VOTE).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
//
//            }else {
                drawable = res.getDrawable(R.drawable.icon_provote_choice);
                //设置投票TextView的drawableleft
                textView(VOTE).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
//            }
            //回答页面
            //有人点赞，未查看
            if(item.getPostAgreeStatus()==2){
                imageView(APPROVE).setVisibility(View.VISIBLE);
                imageView(APPROVE).setImageResource(R.drawable.icon_likeing_choice);
                view(COMMENT_TEXT).setVisibility(View.VISIBLE);
                view(ICONS).setVisibility(View.GONE);
            }
            //有人点赞，已经查看
            if(item.getPostAgreeStatus()==1){
                imageView(APPROVE).setVisibility(View.VISIBLE);
                imageView(APPROVE).setImageResource(R.drawable.icon_like_choice);
                view(COMMENT_TEXT).setVisibility(View.GONE);
                view(ICONS).setVisibility(View.GONE);
            }
            //当前评论未被赞
            if(item.getPostAgreeStatus()==0){
                imageView(APPROVE).setVisibility(View.GONE);
                view(COMMENT_TEXT).setVisibility(View.GONE);
                view(ICONS).setVisibility(View.VISIBLE);
            }
        }
    }
}
