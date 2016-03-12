package feipai.qiangdan.my;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.javabean.OrderItem;
import feipai.qiangdan.javabean.OrderListBean;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.ProgressDlgUtil;
import feipai.qiangdan.util.SaveSettingUtil;

/**
 * Created by wlanjie on 14-6-18.
 */
public class HistoryOrderAdapter extends SingleTypeAdapter<OrderItem> {

    private final Activity activity;
    private Drawable drawable;

    /**
     * Create adapter
     *
     * @param activity
     */
    public HistoryOrderAdapter(Activity activity) {
        super(activity, R.layout.layout_history_order_adapter);
        this.activity = activity;
    }

    /**
     * Get child view ids to store
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @return ids
     */
    @Override
    protected int[] getChildViewIds() {
        return new int[]{
                R.id.text_note_message,//提示信息 0
                R.id.text_from_address,//寄件地址 1
                R.id.text_to_address, //收件地址 2
                R.id.state_textview,  //订单状态 3
                R.id.changestate_layout,//状态布局 4
                R.id.verifycode_textview,//验证码提示 5
                R.id.verifycode_editview,//验证码输入框 6
                R.id.sendcode_imageview //发送验证码按钮 7
        };
    }

    /**
     * Update item
     *
     * @param position 位置
     * @param item     单个订单
     */
    @Override
    protected void update(int position, final OrderItem item) {
        if (item != null) {
            setText(0, "¥" + item.getPrice() + "/" + item.getDistance() + "公里/" + item.getWeight() + "公斤");
            setText(1, item.getjAddress());//发货地址
            setText(2, item.getsAddress());//收获地址
            final String state = item.getState();
            final EditText virifyEdit = view(6);


            if (state.equals("取件中")) {
                view(4).setVisibility(View.VISIBLE);
                drawable = activity.getResources().getDrawable(R.drawable.icon_get_order);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textView(3).setCompoundDrawables(drawable, null, null, null);//设置TextView的drawableleft
                textView(3).setCompoundDrawablePadding(8);//设置图片和text之间的间距
                setText(3, "取件中");
                setText(5, "取件后，请从下单客户处获取验证码，填入下框，并发送");
            }
            if (state.equals("送货中")) {
                view(4).setVisibility(View.VISIBLE);
                drawable = activity.getResources().getDrawable(R.drawable.icon_sending);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textView(3).setCompoundDrawables(drawable, null, null, null);//设置TextView的drawableleft
                textView(3).setCompoundDrawablePadding(8);//设置图片和text之间的间距
                setText(3, "送件中");
                setText(5, "送达后，请从收单客户处获取验证码，填入下框，并发送");
            }
            if (state.equals("已完结") || state.equals("待评价")) {
                drawable = activity.getResources().getDrawable(R.drawable.icon_send_finish);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                textView(3).setCompoundDrawables(drawable, null, null, null);//设置TextView的drawableleft
                textView(3).setCompoundDrawablePadding(8);//设置图片和text之间的间距
                setText(3, "配送成功");
                view(4).setVisibility(View.GONE);
            }

        }
    }

}
