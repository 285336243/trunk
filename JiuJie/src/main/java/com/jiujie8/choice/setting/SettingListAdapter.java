package com.jiujie8.choice.setting;



import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.jiujie8.choice.R;
import com.jiujie8.choice.core.Utils;
import com.jiujie8.choice.setting.entity.SetBean;


public class SettingListAdapter extends SingleTypeAdapter<SetBean> {

    private Context activity;

    public SettingListAdapter(Activity activity) {
        super(activity, R.layout.setting_text);
        this.activity=activity;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{
                R.id.text //显示文本 0
        };
    }

    /**
     *
     * @param position 位置 从0开始
     * @param item bean对象
     */
    @Override
    protected void update(int position, SetBean item) {
        Log.v("kan", "position =" + position);
        if (item != null) {
            setText(0,  item.getText());
            if(position==9){
                textView(0).setTextColor(activity.getResources().getColor(R.color.red));
            }
            if(position==0||position==3||position==8){
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dip2px(activity, 12));
                textView(0).setLayoutParams(params);
                textView(0).setBackgroundColor(activity.getResources().getColor(R.color.white_6E));
            }

        }

    }

}
