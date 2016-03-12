package feipai.qiangdan.other;


import android.app.Activity;
import android.content.Context;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;

import feipai.qiangdan.R;


public class MapListAdapter extends SingleTypeAdapter<String> {

    private Context activity;

    public MapListAdapter(Activity activity) {
        super(activity, R.layout.layout_map_adapter);
        this.activity = activity;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{
                R.id.text, //显示城市名字 0
        };
    }

    /**
     * @param position 位置 从0开始
     * @param item     bean对象
     */
    @Override
    protected void update(int position, String item) {
        if (item != null) {
            setText(0, item);
        }
    }

}
