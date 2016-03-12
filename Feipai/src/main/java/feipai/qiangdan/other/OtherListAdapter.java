package feipai.qiangdan.other;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;

import feipai.qiangdan.R;


public class OtherListAdapter extends SingleTypeAdapter<SetBean> {

    private Context activity;

    public OtherListAdapter(Activity activity) {
        super(activity, R.layout.layout_other_adapter);
        this.activity = activity;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{
                R.id.text, //显示标题 0
                R.id.icon_other //显示图标 1
        };
    }

    /**
     * @param position 位置 从0开始
     * @param item     bean对象
     */
    @Override
    protected void update(int position, SetBean item) {
        if (item != null) {
            setText(0, item.getText());
            imageView(1).setImageResource(item.getResid());
        }
    }

}
