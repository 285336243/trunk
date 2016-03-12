package feipai.qiangdan.order;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.javabean.OrderItem;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.SaveSettingUtil;
import feipai.qiangdan.util.Utils;
import roboguice.inject.InjectView;

/**
 *
 */
public class OrderDetaisActivity extends DialogFragmentActivity {
    private static final String ORDER_DETAIL_URL = "api/v1/order/orderdetails";
    private static final int ORDER_DETAIL_CODE = 0xAC;
    //订单概述
    @InjectView(R.id.order_info)
    private TextView orderInfo;
    //送往地址
    @InjectView(R.id.to_address_textview)
    private TextView toAddressTextview;
    //寄出地址
    @InjectView(R.id.from_address_textview)
    private TextView fromAddressTextview;
    // 其他订单信息
    @InjectView(R.id.other_order_infor)
    private TextView otherOrderInfor;

    //订单取消的前景
    @InjectView(R.id.front_bckg)
    private ImageView frontBckg;
    @InjectView(R.id.call_button)
    private View callButton;
    @InjectView(R.id.view_map)
    private View viewMap;


    @InjectView(R.id.colose_imageview)
    private View coloseView;
    private String message;
    private String[] areas = new String[2];
    private String[] phoneNumber = new String[2];
    private String detailResult;
    private ProgressDialog progressdialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_order_details);

        coloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        String orderId = intent.getStringExtra(IConstant.ORDER_ID);
        getOderDetails(orderId);
        callButton.setOnClickListener(new AlertClickListener());
        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChoiceTransModeFragment dialog = new ChoiceTransModeFragment();
                dialog.show(getSupportFragmentManager(), "ChoiceTransModeFragment");
            }
        });
    }


    private void getOderDetails(String orderId) {
        //网络请求进度条
        progressdialog = new ProgressDialog(OrderDetaisActivity.this);
        progressdialog.setMessage("正在加载订单...");
        progressdialog.setCancelable(true); //点击返回键是否消失 true为消失
        progressdialog.show();

        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("orderid", orderId);
        map.put("sid", SaveSettingUtil.getUserSid(this));
        volley_get(ORDER_DETAIL_URL, map);
/*        new Thread() {
            @Override
            public void run() {
                try {
                    Message msg = Message.obtain();
                    //待处理订单请求
                    detailResult = HttpClientUtil.getInstance().getGetData(ORDER_DETAIL_URL, map);
                    msg.what = ORDER_DETAIL_CODE;
                    mHandler.sendMessage(msg);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }.start();*/
    }


    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        //进度条消失
        if (null != progressdialog || progressdialog.isShowing())
            progressdialog.dismiss();
        Gson gson = new Gson();
        OrderItem data = gson.fromJson(response, OrderItem.class);
        setOrderDetailData(data);
    }

    class AlertClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            new AlertDialog.Builder(OrderDetaisActivity.this).setIcon(R.drawable.icon).setTitle("选择电话").setItems(areas, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(OrderDetaisActivity.this, "您已经选择了: " + which + ":" + areas[which], Toast.LENGTH_LONG).show();
                    switch (which) {
                        case 0:
                            callTelephone(phoneNumber[0]);
                            break;
                        case 1:
                            callTelephone(phoneNumber[1]);
                            break;
                    }
                    dialog.dismiss();
                }
            }).show();
        }
    }

    private void callTelephone(String number) {
        //用intent启动拨打电话
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
        startActivity(intent);
    }

/*    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ORDER_DETAIL_CODE:
                    //进度条消失
                    if (null != progressdialog || progressdialog.isShowing())
                        progressdialog.dismiss();
                    if (detailResult.contains(IConstant.REQUST_FAIL)) {
                        ToastUtils.show(OrderDetaisActivity.this, detailResult);
                    } else {
                        Gson gson = new Gson();
                        OrderItem data = gson.fromJson(detailResult, OrderItem.class);
                        setOrderDetailData(data);
                    }
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    private void setOrderDetailData(OrderItem item) {
        String jiPhone = item.getjPhone();
        String shPhone = item.getsPhone();
        phoneNumber[0] = jiPhone;
        phoneNumber[1] = shPhone;
        areas[0] = "寄件人:" + jiPhone;
        areas[1] = "收件人:" + item.getsPhone();
        //寄件人地址，便于传出去，进行地图的路径规划
        Utils.FROM_ADDRESS = item.getjAddress();
        //收件人地址，便于传出去，进行地图的路径规划
        Utils.TO_ADDRESS = item.getsAddress();
/*        if (RouteSaveUtil.getFromAdd(this) != null) {
            RouteSaveUtil.removeFromAdd(this);
        }
        RouteSaveUtil.saveFromAdd(this, item.getjAddress());*/
        orderInfo.setText("¥" + item.getPrice() + "元/" + item.getDistance() + "公里/" + item.getWeight() + "公斤");
        toAddressTextview.setText("收件地址：" + item.getsAddress() + "\n收件人姓名：" + item.getsName() + "\n收件人电话：" + shPhone);
        fromAddressTextview.setText("寄件地址：" + item.getjAddress() + "\n寄件人姓名：" + item.getjName() + "\n寄件人电话：" + jiPhone);
        otherOrderInfor.setText("订单编号: " + item.getOrderNum() + "\n物品名称: " + item.getGoodsName() + "\n客户备注: " + item.getRemark()
                + "\n支付类型: " + item.getPaymentType() + "\n优惠金额：" + item.getCode_price() + "\n发布时间: " + item.getPublishTime());
        //是否可抢单，0否，1可以
//        if(item.getCanRob()==0) {
//            frontBckg.setVisibility(View.VISIBLE);
//        }else {
        frontBckg.setVisibility(View.GONE);
//        }

    }

}
