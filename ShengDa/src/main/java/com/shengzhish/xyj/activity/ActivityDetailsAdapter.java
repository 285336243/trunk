package com.shengzhish.xyj.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.ActivityDetailsItem;
import com.shengzhish.xyj.core.Utils;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.ImageUtils;
import com.shengzhish.xyj.view.RotateAnimation;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by wlanjie on 14-6-6.
 */
public class ActivityDetailsAdapter extends MultiTypeAdapter {

    private final static int ITEM = 0;
    private final static int LINE = 1;

    private final Activity activity;
    private final ImageLoader imageLoader;
    private final int width;
    private final SharedPreferences preferences;

    /**
     * Create adapter
     *
     * @param activity
     */
    public ActivityDetailsAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        this.imageLoader = ImageLoader.getInstance();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width = metrics.widthPixels - 32;
        preferences = activity.getSharedPreferences(IConstant.ALARM_CLOCK, Context.MODE_PRIVATE);
    }

    public void addItem(List<ActivityDetailsItem> items) {
        clear();
        for (int i = 0; i < items.size(); i++) {
            addItem(ITEM, items.get(i));
            if (i != items.size() - 1) {
                addItem(LINE, new ActivityDetailsItem());
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Get layout id for type
     *
     * @param type
     * @return layout id
     */
    @Override
    protected int getChildLayoutId(int type) {
        if (type % 2 == 0) {
            return R.layout.activity_details_item;
        } else {
            return R.layout.line;
        }
    }

    /**
     * Get child view ids for type
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @param type
     * @return array of view ids
     */
    @Override
    protected int[] getChildViewIds(int type) {
        if (type == ITEM) {
            return new int[]{
                    R.id.activity_details_time,
                    R.id.activity_details_location,
                    R.id.activity_details_image_layout,
                    R.id.activity_details_image,
                    R.id.activity_details_title,

                    R.id.activity_details_root_layout,
                    R.id.activity_details_item_layout,
                    R.id.activity_details_about_layout,
                    R.id.activity_details_about_text,
                    R.id.activity_details_order
            };
        }
        return new int[0];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Update view for item
     *
     * @param position
     * @param item
     * @param type
     */
    @Override
    protected void update(int position, Object item, int type) {
        if (item != null) {
            if (type == ITEM) {
                final ActivityDetailsItem detailsItem = (ActivityDetailsItem) item;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, (int) (width * 0.3));
                view(2).setLayoutParams(params);
                imageLoader.displayImage(detailsItem.getImg(), imageView(3), ImageUtils.imageLoader(activity, 0));
                setText(0, detailsItem.getShowTime());
                setText(1, detailsItem.getLocation());
                setText(4, detailsItem.getTitle());
                setText(8, detailsItem.getAbout());
                if (detailsItem.getIsItemGone() == 1) {
                    setGone(6, true);
                } else {
                    setGone(6, false);
                }

                if (detailsItem.getIsAboutGone() == 0) {
                    setGone(7, true);
                } else {
                    setGone(7, false);
                }

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
                calendar.setTimeInMillis(detailsItem.getTime() * 1000);
                Date date = calendar.getTime();
                if (!date.after(new Date())) {
                    setText(9, "已开始");
                    textView(9).setTextColor(activity.getResources().getColor(R.color.order_color));
                    textView(9).setBackgroundColor(activity.getResources().getColor(R.color.transparent));
                } else {
                    textView(9).setBackgroundResource(R.drawable.bdg_showbtn_sd);
                    String key = preferences.getString(detailsItem.getId(), "");
                    if (key.equals(detailsItem.getId())) {
                        setText(9, "已预约");
                        textView(9).setTextColor(activity.getResources().getColor(R.color.order_color));
                    } else {
                        setText(9, "预约提醒");
                        textView(9).setTextColor(activity.getResources().getColor(R.color.white));
                    }
                    textView(9).setPadding(0, Utils.dip2px(activity, 8), 0, Utils.dip2px(activity, 8));
                    setOrderClickListener(textView(9), detailsItem);
                }
                setOnItemClickListener(detailsItem, view(5), view(6), view(7));
            }
        }
    }

    private void setOrderClickListener(final TextView orderView, final ActivityDetailsItem item) {
        orderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                String key = preferences.getString(item.getId(), "");
                TextView textView = ((TextView) v);
                if (!TextUtils.isEmpty(key)) {
                    preferences.edit().remove(item.getId()).commit();
                    textView.setText("预约提醒");
                    textView.setTextColor(activity.getResources().getColor(R.color.white));

                    Intent intent = new Intent("alarm_clock");
                    intent.putExtra(IConstant.ACTIVITY_LOCATION, item.getLocation());
                    intent.putExtra(IConstant.ACTIVITY_TITLE, item.getTitle());
                    intent.putExtra(IConstant.NOTIFICATION_ID, Integer.parseInt(item.getId()));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, Integer.parseInt(item.getId()), intent, 0);
                    alarmManager.cancel(pendingIntent);
                } else {
                    textView.setText("已预约");
                    textView.setTextColor(activity.getResources().getColor(R.color.order_color));
                    long time = item.getTime() * 1000;
                    preferences.edit().putString(item.getId(), item.getId()).putString(IConstant.ACTIVITY_ID, ((ActivityDetailsActivity) activity).getActivityId()).commit();
                    Intent intent = new Intent("alarm_clock");
                    intent.putExtra(IConstant.ACTIVITY_LOCATION, item.getLocation());
                    intent.putExtra(IConstant.ACTIVITY_TITLE, item.getTitle());
                    intent.putExtra(IConstant.NOTIFICATION_ID, Integer.parseInt(item.getId()));
                    if (time - System.currentTimeMillis() < 1000 * 60 * 5) {
                        intent.putExtra("start", "start");
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, Integer.parseInt(item.getId()), intent, 0);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
                    } else {
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, Integer.parseInt(item.getId()), intent, 0);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, time - 1000 * 60 * 5, pendingIntent);
                    }
                }
            }
        });
    }

    private void setOnItemClickListener(final ActivityDetailsItem item, final View rootView, final View itemView, final View aboutView) {
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotateAnimation animation = null;
                if (itemView.getVisibility() == View.VISIBLE) {
                    animation = new RotateAnimation(rootView.getWidth() / 2, rootView.getHeight() / 2, RotateAnimation.ROTATE_INCREASE);
                    animation.setDuration(500);
                    animation.setInterpolatedTimeListener(new RotateAnimation.InterpolatedTimeListener() {
                        @Override
                        public void interpolatedTime(float interpolatedTime) {
                            if (interpolatedTime > 0.5f) {
                                itemView.setVisibility(View.GONE);
                                item.setIsItemGone(1);
                                item.setIsAboutGone(1);
                                aboutView.setVisibility(View.VISIBLE);
                                aboutView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, rootView.getHeight()));
                            }
                        }
                    });
                } else {
                    animation = new RotateAnimation(rootView.getWidth() / 2, rootView.getHeight() / 2, RotateAnimation.ROTATE_DECREASE);
                    animation.setDuration(500);
                    animation.setInterpolatedTimeListener(new RotateAnimation.InterpolatedTimeListener() {
                        @Override
                        public void interpolatedTime(float interpolatedTime) {
                            if (interpolatedTime > 0.5f) {
                                item.setIsItemGone(0);
                                item.setIsAboutGone(0);
                                itemView.setVisibility(View.VISIBLE);
                                aboutView.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                rootView.startAnimation(animation);
            }
        });
    }
}
