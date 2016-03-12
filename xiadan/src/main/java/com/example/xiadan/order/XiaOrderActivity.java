package com.example.xiadan.order;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewFlipper;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.VolleyError;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.example.xiadan.R;
import com.example.xiadan.bean.LoginBean;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.loginregister.LoginActivity;
import com.example.xiadan.util.AnimationHelper;
import com.example.xiadan.util.DateUtil;
import com.example.xiadan.util.HttpClientUtil;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.ProgressDlgUtil;
import com.example.xiadan.util.SaveSettingUtil;
import com.example.xiadan.util.VertifyPhoneNumber;
import com.example.xiadan.whole.AppManager;
import com.example.xiadan.whole.BaseActivity;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class XiaOrderActivity extends BaseActivity implements View.OnTouchListener {
    private static final String COST_URL = "page/api/v1/order/new";
    private static final int COST_CODE = 0X2F1;
    public static XiaOrderActivity instance = null;
    private ViewFlipper viewFlipper;
    /**
     * 城市ui
     */
//    private EditText mCity;
    /**
     * 寄件ui
     */
    private EditText jName, jPhone, jAddress;
    /**
     * 是否定时取，定时送
     */
    private CheckBox takeCheck, sendCheck;
    /**
     * 取件和送件layoutt
     */
    private View takeLayout, sendLayout;
    /**
     * 取件时间,送件时间
     */
    private EditText etStartTime, etEndTime;
    /**
     * 物品名称,物品重量，备注
     */
    private EditText mGoodsName, mGoodsWeight, mRemark;
    /**
     * 寄件人信息
     */
    private String jname, jphone, jaddress;

    /**
     * 收件ui
     */
    private EditText sName, sPhone, sAddress;
    /**
     * 有飞豆数
     */
    private TextView countFeidou;
    /**
     * 收件人信息
     */
    private String sname, sphone, saddress;

    /**
     * 下一步button,算算费用button
     */
    private Button mButton, costButton;
    private Activity context;
    private GeoCoder mSearch1, mSearch2; // 搜索模块，也可去掉地图模块独立使用
    private boolean isJaddresslat = false;
    private boolean isSaddressLat = false;
    private LatLng sLatLng;
    private LatLng jLatLng;
    private HashMap<String, String> map = new HashMap<String, String>();
    //算算费用结果
    private String costResult;
    /**
     * 距离
     * 传米，不带小数点，不带单位
     */
    private String mDistance;
    /**
     * 是否定时取(不带则为否;1:是；0：否)
     */
    private int isTime;
    /**
     * 指定取件时间(格式:yyyy-MM-dd HH:mm;当isTime=1时，必填)
     */
    private String jTime;
    /**
     * // 是否定时送(不带则为否;1:是；0：否)
     */
    private int isSong;
    /**
     * 指定送达时间(格式:yyyy-MM-dd HH:mm;当isSong=1时，必填)
     */
    private String endTime;
    /**
     * 是否下一步了
     */
    private boolean isNext;
    /**
     * 飞豆chechbox控件
     */
    private CheckBox feidouCheck;
    /**
     * 拥有飞豆数
     */
    private int mCount;
    /**
     * 飞豆editview
     */
    private EditText feidouEdit;
    /**
     * 是否使用飞豆
     */
    private boolean isUseFeiDou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_xiadan);
        instance = this;
        AppManager.getAppManager().addActivity(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("订单信息");
        //设置可点击
        actionBar.setDisplayHomeAsUpEnabled(true);
        initWholeView();
        initBasicView();
        // 初始化搜索模块，注册事件监听
        mSearch1 = GeoCoder.newInstance();
        mSearch2 = GeoCoder.newInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isNext) {
                    previousstep();
                } else {
                    finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //讲dialog置空，以便重新启动
            ProgressDlgUtil.stopProgressDlg();
            if (isNext) {
                previousstep();
            } else {
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        }
        return false;
    }

    private void initWholeView() {
        context = XiaOrderActivity.this;
        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        viewFlipper.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
        viewFlipper.setFlipInterval(300);
    }

    private void initBasicView() {
//        mCity = (EditText) findViewById(R.id.city_edit);

        jName = (EditText) findViewById(R.id.jname_edit);
        jPhone = (EditText) findViewById(R.id.jphone_edit);
        jPhone.setInputType(InputType.TYPE_CLASS_PHONE);//电话
        jAddress = (EditText) findViewById(R.id.jaddress_name);

        sName = (EditText) findViewById(R.id.sname_edit);
        sPhone = (EditText) findViewById(R.id.sphone_edit);
        sPhone.setInputType(InputType.TYPE_CLASS_PHONE);//电话
        sAddress = (EditText) findViewById(R.id.saddress_edit);
        //点击下一步
        mButton = (Button) findViewById(R.id.next_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.v("kan","点击下一步");
                checkContent();

            }
        });

        //第二页控件初始化
        //取件和送时间初始化
        //取得客户飞豆数
        mCount = getIntent().getIntExtra(IConstant.FEIDOU, 0);
        countFeidou = (TextView) findViewById(R.id.count_feitou);
        countFeidou.setText("您有飞豆" + mCount + "个，折合人民币￥" + String.format("%.2f", (double) mCount / 100d) + "元");
        takeCheck = (CheckBox) findViewById(R.id.take_checkbox);
        //飞豆 editview
        feidouEdit = (EditText) findViewById(R.id.feidou_edit);

        sendCheck = (CheckBox) findViewById(R.id.send_checkbox);
        feidouCheck = (CheckBox) findViewById(R.id.feidou_checkbox);
        feidouCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isUseFeiDou = true;
                } else {
                    isUseFeiDou = false;
                }
            }
        });

        takeLayout = findViewById(R.id.take_layout);
        sendLayout = findViewById(R.id.send_layout);

        final TextView sendTime = (TextView) findViewById(R.id.send_time_hint);
        etStartTime = (EditText) this.findViewById(R.id.et_take_time);
        etEndTime = (EditText) this.findViewById(R.id.et_send_time);
        etStartTime.setOnTouchListener(this);
        etEndTime.setOnTouchListener(this);
        //是否定时取监听
        takeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    takeLayout.setVisibility(View.VISIBLE);
                    isTime = 1;
                    if (isSong == 1) {
                        sendTime.setText(getString(R.string.songtime_note));
                    }
                } else {
                    takeLayout.setVisibility(View.GONE);
                    isTime = 0;
                    if (isSong == 1) {
                        sendTime.setText("提示：送件时间距离当前时间至少120分钟 ");
                    }
                }
            }
        });
        //是否定时送监听
        sendCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sendLayout.setVisibility(View.VISIBLE);
                    isSong = 1;
                    if (isTime == 1) {
                        sendTime.setText(getString(R.string.songtime_note));
                    } else {
                        sendTime.setText("提示：送件时间距离当前时间至少120分钟 ");
                    }

                } else {
                    sendLayout.setVisibility(View.GONE);
                    isSong = 0;
                }
            }
        });
        //算算费用button
        costButton = (Button) findViewById(R.id.cost_button);
        costButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculCost();
            }
        });
    }

    private void calculCost() {
        //物品名称，必填
        mGoodsWeight = (EditText) findViewById(R.id.weight_edit);
        String weight = mGoodsWeight.getText().toString().trim();
        if (TextUtils.isEmpty(weight)) {
            ToastUtils.show(context, "请填写物品重量");
            return;
        }
        //内容是否为空前面已经检查过，这里直接取值
        //寄件人部分
        jname = jName.getText().toString().trim();
        jphone = jPhone.getText().toString().trim();
        jaddress = jAddress.getText().toString().trim();
        //收件人部分
        sname = sName.getText().toString().trim();
        sphone = sPhone.getText().toString().trim();
        saddress = sAddress.getText().toString().trim();

        map.put("weight", weight);
        //寄件人姓名
        map.put("jName", jname);
        //寄件人地址
        map.put("jAddress", jaddress);
        // 寄件人手机号码
        map.put("jPhone", jphone);

        //收件人姓名
        map.put("sName", sname);
        //送达地址
        map.put("sAddress", saddress);
        // 收件人手机号码
        map.put("sPhone", sphone);

        // 是否定时取(不带则为否;1:是；0：否)
        map.put("isTime", String.valueOf(isTime));
        if (isTime == 1) {
            // 指定取件时间(格式:yyyy-MM-dd HH:mm;当isTime=1时，必填)
            jTime = etStartTime.getText().toString().trim();//获得定时取件时间
            if (TextUtils.isEmpty(jTime)) {
                ToastUtils.show(context, "取件时间不能为空");
                return;
            }
            if (!DateUtil.compare_current(jTime, 30)) {
                ToastUtils.show(context, "取件时间距离当前时间至少30分钟，请重新填写");
                return;
            }
            map.put("jTime", jTime);
        }

        // 是否定时送(不带则为否;1:是；0：否)
        map.put("isSong", String.valueOf(isSong));
        if (isSong == 1) {
            // 指定送达时间(格式:yyyy-MM-dd HH:mm;当isSong=1时，必填)
            endTime = etEndTime.getText().toString().trim();//获得定时送件时间
            if (TextUtils.isEmpty(endTime)) {
                ToastUtils.show(context, "送件时间不能为空");
                return;
            }
            //不定时买，仅仅是定时送
            if (isTime == 0) {
                if (!DateUtil.compare_current(endTime, 120)) {
                    ToastUtils.show(context, "送件时间距离当前时间至少120分钟，请重新填写");
                    return;
                }
            } else {
                //即定时买，也定时送
                //取件时间 和 送件时间 必须是同一天,送件时间距离取件时间至少90分钟
                if (!DateUtil.compare_twodate(jTime, endTime, 90) || !DateUtil.isSameDay(jTime, endTime)) {
                    ToastUtils.show(context, getString(R.string.songtime_note));
                    return;
                }
            }
            map.put("endTime", endTime);
        }


        //物品名称
        mGoodsName = (EditText) findViewById(R.id.edit_goods);
        String goodsName = mGoodsName.getText().toString().trim();
        if (!TextUtils.isEmpty(goodsName)) {
            map.put("goodsName", goodsName);
        }
        //备注信息
        mRemark = (EditText) findViewById(R.id.remark_edit);
        String remark = mRemark.getText().toString().trim();
        if (!TextUtils.isEmpty(remark)) {
            map.put("remark", remark);
        }
        //登陆验证码
        map.put("sid", SaveSettingUtil.getUserSid(this));
        if (isUseFeiDou) {
            int feiNumber = Integer.parseInt(feidouEdit.getText().toString().trim());
            if (feiNumber < 0) {
                ToastUtils.show(context, "使用飞豆数不能小于0!");
                return;
            }
            if (feiNumber > mCount) {
                ToastUtils.show(context, "使用飞豆数不能大于拥有飞豆数!");
                return;
            }
            map.put("useFD", String.valueOf(feiNumber));
        } else {
            //使用飞豆数量(不带默认为0)
            map.put("useFD", String.valueOf(0));
        }

        //[必填] 距离
        map.put("distance", mDistance);
//        map.put("distance", "12345");
        //算算费用请求
        volley_post(COST_URL, map);
/*        ProgressDlgUtil.showProgressDlg(context, "计算中......");
        HttpClientUtil.getInstance().doPostAsyn(COST_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                costResult = result;
                Message msg = Message.obtain();
                msg.what = COST_CODE;
                mHandler.sendMessage(msg);
            }
        });*/
    }

    @Override
    public void errorResponse(VolleyError error) {
        super.errorResponse(error);
    }

    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        startActivity(new Intent(XiaOrderActivity.this, ResultActivity.class).putExtra(IConstant.ORDER_RESULT, response));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

/*    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //登录结果
                case COST_CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (costResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(context, costResult);

                    } else {
                        startActivity(new Intent(XiaOrderActivity.this, ResultActivity.class).putExtra(IConstant.ORDER_RESULT, costResult));
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    private void checkContent() {
//        String city = mCity.getText().toString().trim();
        //寄件人部分
        jname = jName.getText().toString().trim();
        jphone = jPhone.getText().toString().trim();
        jaddress = jAddress.getText().toString().trim();
        //收件人部分
        sname = sName.getText().toString().trim();
        sphone = sPhone.getText().toString().trim();
        saddress = sAddress.getText().toString().trim();
        if (TextUtils.isEmpty(jname)) {
            ToastUtils.show(context, "寄件人姓名不能为空");
            return;
        }
        if (TextUtils.isEmpty(jphone)) {
            ToastUtils.show(context, "寄件人手机号不能为空");
            return;
        }
        if (!VertifyPhoneNumber.isMobileNO(jphone)) {
            ToastUtils.show(context, "请输入正确寄件人手机号");
            return;
        }
        if (jphone.length() < 11) {
            ToastUtils.show(context, "请填写正确的寄件人手机号");
            return;
        }
        if (TextUtils.isEmpty(jaddress)) {
            ToastUtils.show(context, "寄件人地址不能为空");
            return;
        }
        if (jaddress.length() < 4) {
            ToastUtils.show(context, "寄件地址太简单啦，请填写详细");
            return;
        }
        if (jaddress.contains("小区")) {
            if (!jaddress.contains("区")) {
                ToastUtils.show(context, "寄件地址应填写相应区");
                return;
            }
        }
        if (!jaddress.contains("区")) {
            ToastUtils.show(context, "寄件地址应填写相应区");
            return;
        }
        if (TextUtils.isEmpty(sname)) {
            ToastUtils.show(context, "收件人姓名不能为空");
            return;
        }
        if (TextUtils.isEmpty(sphone)) {
            ToastUtils.show(context, "收件人手机号不能为空");
            return;
        }
        if (!VertifyPhoneNumber.isMobileNO(sphone)) {
            ToastUtils.show(context, "请输入正确的收件人手机号");
            return;
        }
        if (sphone.length() < 11) {
            ToastUtils.show(context, "请填写正确的收件人手机号");
            return;
        }
        if (TextUtils.isEmpty(saddress)) {
            ToastUtils.show(context, "收件人地址不能为空");
            return;
        }
        if (saddress.length() < 4) {
            ToastUtils.show(context, "收件地址太简单啦，请填写详细");
            return;
        }
        if (saddress.contains("小区")) {
            if (!saddress.contains("区")) {
                ToastUtils.show(context, "收件地址应填写相应区");
                return;
            }
        }
        if (!saddress.contains("区")) {
            ToastUtils.show(context, "收件地址应填写相应区");
            return;
        }
        analysAddress(jaddress, saddress);
    }

    private void analysAddress(final String jaddress, final String saddress) {
        //寄件地址 Geo搜索  必须加上.city("")，不然解析不出来
        mSearch1.geocode(new GeoCodeOption().city("").address(jaddress));
        ProgressDlgUtil.showProgressDlg(context, "正在解析地址");
//        Log.v("kan","进入解析地址");
        mSearch1.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                ProgressDlgUtil.stopProgressDlg();
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {

                    ToastUtils.show(context, "抱歉，找不到寄件人地址，请重新填写或更改为相近的地址");
                    return;
                }
                String strInfo = String.format("纬度：%f 经度：%f", result.getLocation().latitude, result.getLocation().longitude);
//                ToastUtils.show(context, strInfo);
                //获得寄件地址经纬度
                jLatLng = result.getLocation();
                isJaddresslat = true;
                if (isSaddressLat) {
                    double distance = DistanceUtil.getDistance(jLatLng, sLatLng);//单位 米
                    long intDiance = (long) (distance * 1.47);//传米，不带小数点，不带单位,要转换成整数，直线距离转为驾车距离乘以1.47
                    mDistance = String.valueOf(intDiance);

                    Log.v("kan", " mSearch1  距离nextstep: " + mDistance);
                    isSaddressLat = false;
                    isJaddresslat = false;
                    nextstep();
                }

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
        //收件地址 Geo搜索
        mSearch2.geocode(new GeoCodeOption().city("").address(saddress));
        mSearch2.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                ProgressDlgUtil.stopProgressDlg();
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    ToastUtils.show(context, "抱歉，找不到收件人地址，请重新填写或更改为相近的地址");
                    return;
                }
                String strInfo = String.format("纬度：%f 经度：%f", result.getLocation().latitude, result.getLocation().longitude);
//                ToastUtils.show(context, strInfo);
                //获得收件地址经纬度
                sLatLng = result.getLocation();
                isSaddressLat = true;
                if (isJaddresslat) {
                    double distance = DistanceUtil.getDistance(jLatLng, sLatLng);//单位 米
                    long intDiance = (long) (distance * 1.47);//传米，不带小数点，不带单位,要转换成整数，直线距离转为驾车距离乘以1.47
                    mDistance = String.valueOf(intDiance);

                    Log.v("kan", " mSearch2  距离nextstep: " + mDistance);
                    isJaddresslat = false;
                    isSaddressLat = false;
                    nextstep();
                }
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
    }

    /**
     * 上一步
     */
    private void previousstep() {
        if (!TextUtils.isEmpty(mDistance)) {
            mDistance = null;
        }
        if (!map.isEmpty()) {
            map.clear();
        }
        isNext = false;
        viewFlipper.setInAnimation(AnimationHelper.inFromLeft());
        viewFlipper.setOutAnimation(AnimationHelper.outToRight());
        viewFlipper.setDisplayedChild(0);
    }

    /**
     * 下一步
     */
    private void nextstep() {
        viewFlipper.setInAnimation(AnimationHelper.inFromRight());
        viewFlipper.setOutAnimation(AnimationHelper.outToLeft());
        viewFlipper.setDisplayedChild(1);
        isNext = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = View.inflate(this, R.layout.date_time_dialog, null);
            final DatePicker datePicker = (DatePicker) view.findViewById(R.id.date_picker);
            final TimePicker timePicker = (android.widget.TimePicker) view.findViewById(R.id.time_picker);
            builder.setView(view);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

            timePicker.setIs24HourView(true);
            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(Calendar.MINUTE);

            if (v.getId() == R.id.et_take_time) {
                final int inType = etStartTime.getInputType();
                etStartTime.setInputType(InputType.TYPE_NULL);
                etStartTime.onTouchEvent(event);
                etStartTime.setInputType(inType);
                etStartTime.setSelection(etStartTime.getText().length());

                builder.setTitle("选取时间");
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d-%02d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));
                        sb.append(" ");
                        String hour = String.valueOf(timePicker.getCurrentHour());
                        if (hour.length() == 1) {
                            hour = "0" + hour;
                        }
                        sb.append(hour).append(":");
                        String minute = String.valueOf(timePicker.getCurrentMinute());
                        if (minute.length() == 1) {
                            minute = "0" + minute;
                        }
                        sb.append(minute);
                        etStartTime.setText(sb);
                        etEndTime.requestFocus();

                        dialog.cancel();
                    }
                });

            } else if (v.getId() == R.id.et_send_time) {
                int inType = etEndTime.getInputType();
                etEndTime.setInputType(InputType.TYPE_NULL);
                etEndTime.onTouchEvent(event);
                etEndTime.setInputType(inType);
                etEndTime.setSelection(etEndTime.getText().length());

                builder.setTitle("选取结束时间");
                builder.setPositiveButton("确  定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d-%02d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));
                        sb.append(" ");
                        String hour = String.valueOf(timePicker.getCurrentHour());
                        if (hour.length() == 1) {
                            hour = "0" + hour;
                        }
                        sb.append(hour).append(":");
                        String minute = String.valueOf(timePicker.getCurrentMinute());
                        if (minute.length() == 1) {
                            minute = "0" + minute;
                        }
                        sb.append(minute);
                        etEndTime.setText(sb);

                        dialog.cancel();
                    }
                });
            }

            Dialog dialog = builder.create();
            dialog.show();
        }

        return true;
    }
}
