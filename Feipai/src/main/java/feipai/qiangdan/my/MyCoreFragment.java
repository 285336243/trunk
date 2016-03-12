package feipai.qiangdan.my;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragment;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.javabean.MyInfoBean;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.NetWorkConnect;
import feipai.qiangdan.util.SaveSettingUtil;
import feipai.qiangdan.util.VolleyUtil;
import roboguice.inject.InjectView;

/**
 * 个人中心
 */
public class MyCoreFragment extends DialogFragment {

    private static final String TOTAL_ORDERNUM_URL = "api/v1/account/info";
    private static final int TOTAL_ORDER_NUM = 0xdc;
    @InjectView(R.id.my_order_page)
    private View myOrderPage;

    @InjectView(R.id.crash_account_page)
    private View crasAccountPage;

    @InjectView(R.id.reward_page)
    private View rewarPage;

    //已完结订单数量
    @InjectView(R.id.dan_number)
    private TextView orderNumber;
    //个人姓名
    @InjectView(R.id.person_name)
    private TextView mName;
    // 总收入
    @InjectView(R.id.total_income)
    private TextView totalIncome;
    // 本月收入
    @InjectView(R.id.month_income)
    private TextView monthIncome;
    // 昨日收入
    @InjectView(R.id.yes_day_income)
    private TextView yDayIncome;
    // 现金账户
    @InjectView(R.id.cash_number)
    private TextView myMoney;
    //转账记录
    @InjectView(R.id.transfer_list)
    private View transferList;

    private String resultResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_my_center, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clickListener();
        getTotalOrderNum();
    }

    private void getTotalOrderNum() {
        if (!NetWorkConnect.isConnect(getActivity())) {
            ToastUtils.show(getActivity(), "您的网络不可用，请检查您的网络状况");
            return;
        }
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(getActivity()));
        VolleyUtil.getInstance(getActivity()).volley_get(TOTAL_ORDERNUM_URL, map, new VolleyUtil.OnCompleteListener() {
            @Override
            public void correct(String str) {
                Gson gson = new Gson();
                MyInfoBean data = gson.fromJson(str, MyInfoBean.class);
                setPersonInfo(data);
            }

            @Override
            public void error(VolleyError error) {
                if(error.networkResponse.statusCode==401){

                }
            }
        });
/*        HttpClientUtil.getInstance().doGetAsyn(TOTAL_ORDERNUM_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                resultResponse = result;
                Message msg = Message.obtain();
                msg.what = TOTAL_ORDER_NUM;
                mHandler.sendMessage(msg);

            }
        });*/
    }

/*    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOTAL_ORDER_NUM:
                    if (resultResponse.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(getActivity(), resultResponse);
                    } else {
                        Gson gson = new Gson();
                        MyInfoBean data = gson.fromJson(resultResponse, MyInfoBean.class);
                        setPersonInfo(data);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    private void setPersonInfo(MyInfoBean myinfo) {
        mName.setText(myinfo.getName());
        orderNumber.setText(myinfo.getEndOrderNum() + "");
        totalIncome.setText(myinfo.getAllIncome());
        monthIncome.setText(myinfo.getMonthIncome());
        yDayIncome.setText(myinfo.getyDayIncome());
        myMoney.setText(myinfo.getMyMoney());

    }

    private void clickListener() {
        myOrderPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOther(MyHistoryOrderActivity.class);
            }
        });
        crasAccountPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOther(MyCrashAccountActivity.class);
            }
        });
        rewarPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOther(RewardsActivity.class);
            }
        });
        transferList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOther(MyHistoryTransActivity.class);
            }
        });
    }

    private void startOther(Class<?> aClass) {
        startActivity(new Intent(getActivity(), aClass));
    }
}
