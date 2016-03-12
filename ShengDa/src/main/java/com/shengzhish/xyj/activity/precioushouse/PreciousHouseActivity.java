package com.shengzhish.xyj.activity.precioushouse;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.ActivityDetails;
import com.shengzhish.xyj.activity.entity.CodeItem;
import com.shengzhish.xyj.activity.entity.ScannerResponse;
import com.shengzhish.xyj.activity.precioushouse.zxing.activity.CaptureActivity;
import com.shengzhish.xyj.core.DialogFragmentActivity;
import com.shengzhish.xyj.http.HttpboLis;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.ScreenUtil;
import com.shengzhish.xyj.util.Utils;

import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * 宝藏室
 */
@ContentView(R.layout.precious_house_layout)
public class PreciousHouseActivity extends DialogFragmentActivity {

    private static final int TOTAL_COUNT = 5;
    @InjectView(R.id.back_imageview)
    private View back;
    @InjectView(R.id.scanner_qr_code)
    private ImageView scannerQrCode;

    @InjectView(R.id.red_frame_front_ground)
    private ImageView redFrameFrontGround;

    @InjectView(R.id.right_front_ground)
    private ImageView rightFrontGround;

    @InjectView(R.id.count_textview)
    private TextView countTextView;

    @InjectView(R.id.total_count_textview)
    private TextView totalCountTextview;

    @InjectView(R.id.note_message)
    private TextView noteMessage;

    @InjectView(R.id.view_pager)
    private ViewPager viewPager;
    @InjectView(R.id.pager_layout)
    private RelativeLayout viewPagerContainer;


    @InjectView(R.id.hint_arrow)
    private ImageButton hintArrow;

    private Context context;
    private String id;
    private int currentPosition;
    private List<CodeItem> pics;
    private String code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = PreciousHouseActivity.this;
        id = getIntent().getStringExtra(IConstant.ACTIVITY_ID);

//        inatiaViewPager();
        getPictures();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        scannerQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCameraIntent = new Intent(PreciousHouseActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });

        HttpboLis.getInstance().getHttp(this, ActivityDetails.class, String.format("activity/detail.json?id=%s", id), new HttpboLis.OnCompleteListener<ActivityDetails>() {
            @Override
            public void onComplete(ActivityDetails response) {
                if (response.getActivity() != null /*&& dialogIsShowing*/) {
                    if (!TextUtils.isEmpty(response.getActivity().getRule())) {
                        final Dialog dialog = new Dialog(PreciousHouseActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
                        dialog.setContentView(R.layout.activity_news_rule_dialog);
                        TextView textView = (TextView) dialog.findViewById(R.id.activity_rule_text);
                        View closeView = dialog.findViewById(R.id.close_activity_rule_dialog);
                        closeView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        textView.setText(response.getActivity().getRule() + "\n");
                        if (!dialog.isShowing()) {
                            dialog.show();
                        }
                    }
                }
            }
        });

    }

    private void getPictures() {
        String url = code == null ? String.format("activity/code.json?id=%s", id) :
                String.format("activity/code.json?id=%s&code=%s", id, code);
        HttpboLis.getInstance().getHttpDialog(context, ScannerResponse.class,
                url, "正在加载", new HttpboLis.OnCompleteListener<ScannerResponse>() {
            @Override
            public void onComplete(ScannerResponse response) {
                countTextView.setText(response.getGotTotal());
                totalCountTextview.setText("/" + response.getTotal());
                pics = response.getPics();
                CodeItem rezult = response.getResult();
                int currentPosition = 0;
                if (null != rezult) {
                    for (int i = 0; i <= pics.size(); i++) {
                        if (pics.get(i).getId().equals(rezult.getId())) {
                            currentPosition = i;
                            break;
                        }
                    }
                }
                inatiaViewPager(pics, currentPosition);
            }
        });
    }

    private void inatiaViewPager(List<CodeItem> items, int position) {
        viewPager.setPageMargin(20);
        int widthArrow = ScreenUtil.getViewWidth(hintArrow);
        int width = ScreenUtil.getScreenWidth(this) - widthArrow * 2;
        int pageWidth = (width - viewPager.getPageMargin() * 2 - Utils.dip2px(context, 16)) / 3;
//        RelativeLayout.LayoutParams viewPagerPara = new RelativeLayout.LayoutParams(pageWidth, pageWidth * 2);
//        viewPager.setLayoutParams(viewPagerPara);

        RelativeLayout.LayoutParams imageViewPara = new RelativeLayout.LayoutParams(pageWidth, pageWidth * 2);
        imageViewPara.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        redFrameFrontGround.setLayoutParams(imageViewPara);
        viewPager.setLayoutParams(imageViewPara);
        viewPager.setAdapter(new MyPagerAdapter(context, items));
        viewPager.setOffscreenPageLimit(TOTAL_COUNT);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
//        viewPager.setCurrentItem(0);
        viewPager.setCurrentItem((items.size()) * 100 + position);
        redFrameFrontGround.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // dispatch the events to the ViewPager, to solve the problem that we can swipe only the middle view.
                return viewPager.dispatchTouchEvent(event);
            }
        });
        rightFrontGround.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // dispatch the events to the ViewPager, to solve the problem that we can swipe only the middle view.
                return viewPager.dispatchTouchEvent(event);
            }
        });
        viewPagerContainer.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // dispatch the events to the ViewPager, to solve the problem that we can swipe only the middle view.
                return viewPager.dispatchTouchEvent(event);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            code = scanResult;
            getPictures();
        }
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        /**
         * This method will be invoked when the current page is scrolled, either as part
         * of a programmatically initiated smooth scroll or a user initiated touch scroll.
         *
         * @param position             Position index of the first page currently being displayed.
         *                             Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
         * @param positionOffsetPixels Value in pixels indicating the offset from position.
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // 刷新布局
            if (viewPagerContainer != null) {
                viewPagerContainer.invalidate();
            }
        }

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        @Override
        public void onPageSelected(int position) {
//            Log.v("person", "person === " + position);

            noteMessage.setText(pics.get(position % pics.size()).getDesc() + "\n" +
                    "pics.get(position)=" + position % pics.size());
        }

        /**
         * Called when the scroll state changes. Useful for discovering when the user
         * begins dragging, when the pager is automatically settling to the current page,
         * or when it is fully stopped/idle.
         *
         * @param state The new scroll state.
         * @see android.support.v4.view.ViewPager#SCROLL_STATE_IDLE
         * @see android.support.v4.view.ViewPager#SCROLL_STATE_DRAGGING
         * @see android.support.v4.view.ViewPager#SCROLL_STATE_SETTLING
         */
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
