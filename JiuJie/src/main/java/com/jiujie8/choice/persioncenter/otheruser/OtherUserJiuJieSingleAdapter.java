package com.jiujie8.choice.persioncenter.otheruser;

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
import com.jiujie8.choice.choice.PublishChoiceActivity;
import com.jiujie8.choice.core.Utils;
import com.jiujie8.choice.home.entity.ChoiceItem;
import com.jiujie8.choice.home.entity.ChoiceMode;
import com.jiujie8.choice.myjiujie.JiuJieDetailActivity;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;

/**
 *
 */
public class OtherUserJiuJieSingleAdapter extends SingleTypeAdapter<ChoiceMode> {

    private static final int PIC = 0;
    private static final int WORD = 1;
    private static final int FAVORITE = 2;
    private static final int COMMENT = 3;
    private static final int VOTE = 4;
    private static final int OR = 5;
    private static final int LEFTIMAGE = 6;
    private static final int RIGHTIMAGE = 7;
    private static final int ROOT = 8;
    private final Activity activity;
    private final int width;
    private final Resources res;
    private ImageLoader imageLoader;


    @Inject
    public OtherUserJiuJieSingleAdapter(Activity activity) {
        super(activity, R.layout.layout_adapter_otheruserjiujie);
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
                R.id.single__item_layout  //单个布局    8

        };
    }

    @Override
    protected void update(int position, final ChoiceMode item) {
        //设置布局参数，不然按内容填充
        int intenalmWidrh = (width - Utils.dip2px(activity, 2)) / 2;
        FrameLayout.LayoutParams marginPara = new FrameLayout.LayoutParams(intenalmWidrh, (int) (intenalmWidrh * 0.9));
        view(ROOT).setLayoutParams(marginPara);

        if (item != null) {
                ChoiceItem choice = item.getChoice();
            view(ROOT).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(IConstant.ChOICE_MODE, item);
//                        mBundle.putParcelableArrayList(IConstant.LIST, (ArrayList)jiuChoice);
//                    mBundle.putSerializable(IConstant.LIST, (LinkedList)jiuChoice);
                    activity.startActivityForResult(new Intent(activity, OtherDetailActivity.class).putExtras(mBundle), IConstant.OTHER_RESULT_CODE);
                }
            });
                if (choice.getChoiceType().equals("TEXT")) {
                    view(PIC).setVisibility(View.GONE);
                    view(OR).setVisibility(View.GONE);
                    textView(WORD).setVisibility(View.VISIBLE);
                    textView(WORD).setText(choice.getDesc());
                    textView(WORD).setBackgroundColor(Color.rgb(choice.getBgColorRed(), choice.getBgColorGreen(), choice.getBgColorBlue()));
                }
                if (choice.getChoiceType().equals("YN")) {
                    textView(WORD).setVisibility(View.GONE);
                    view(OR).setVisibility(View.GONE);
                    view(PIC).setVisibility(View.VISIBLE);
                    imageLoader.displayImage(choice.getImg(), imageView(PIC), ImageUtils.defaultImageLoader());
                }
                if (choice.getChoiceType().equals("OR")) {
                    view(PIC).setVisibility(View.GONE);
                    textView(WORD).setVisibility(View.GONE);
                    view(OR).setVisibility(View.VISIBLE);
                    imageLoader.displayImage(choice.getLeftImg(), imageView(LEFTIMAGE), ImageUtils.defaultImageLoader());
                    imageLoader.displayImage(choice.getRightImg(), imageView(RIGHTIMAGE), ImageUtils.defaultImageLoader());
                }
                setText(FAVORITE, String.valueOf(choice.getFavoriteCnt()));
                setText(COMMENT, String.valueOf(choice.getPostCnt()));
                setText(VOTE, String.valueOf(choice.getVoteCnt()));
        }
    }
}
