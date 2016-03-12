package com.example.xiadan;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.android.volley.VolleyError;
import com.example.xiadan.bean.CustomBean;
import com.example.xiadan.bean.VersionBean;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.home.PriceActivity;
import com.example.xiadan.home.QuestionActivity;
import com.example.xiadan.home.UpgradeVersonDialog;
import com.example.xiadan.loginregister.LoginActivity;
import com.example.xiadan.order.XiaOrderActivity;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.NetWorkConnect;
import com.example.xiadan.util.SaveSettingUtil;
import com.example.xiadan.util.VolleyUtil;
import com.example.xiadan.whole.AppManager;
import com.example.xiadan.whole.BaseActivity;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.HashMap;


public class MainActivity extends BaseActivity {

    private static final String COMSTOME_INFO_URL = "page/api/v1/user";
    private static final int COM_CODE = 0X2C;
    private static final String CHECK_VERSION_URL = "page/api/v1/version";
    private SlidingMenu mMenu;
    private String costomResult;
    private Activity context;
    private TextView mName;
    private int numberFeidou;
    /**
     * 下单button
     */
    private Button xiaButton;
    /**
     * 更新对话框
     */
    private boolean isShowUpdateDialog = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = MainActivity.this;
        AppManager.getAppManager().addActivity(this);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("飞派");
        actionBar.setLogo(R.drawable.munu_icon);
        //设置可点击
        actionBar.setDisplayHomeAsUpEnabled(true);
        setupSlidingMenu();
        initView();
//        checkLoginState();
        if (isShowUpdateDialog) {
            //版本升级
            checkUpdateMethd();
            isShowUpdateDialog = false;
        }
    }

    private void checkUpdateMethd() {
        if (!NetWorkConnect.isConnect(context)) {
            ToastUtils.show(context, "没有网络连接，请检查");
            return;
        }
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", "feipai");//版本地址写死，不用验证码，不登陆也可以更新
        VolleyUtil.getInstance(activity).volley_get(CHECK_VERSION_URL, map, new VolleyUtil.OnCompleteListener() {
            @Override
            public void correct(String str) {
                int code = 1;
                try {
                    code = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                VersionBean response = gson.fromJson(str, VersionBean.class);
                if (response.getVersionNum() > code) {
                    UpgradeVersonDialog.createUpdateDialog(activity, response.getLink(),
                            response.getVersion(), response.getInfo());
                }

            }

            @Override
            public void error(VolleyError str) {
                if (str.networkResponse.statusCode == 401) {
                    startActivity(LoginActivity.class);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkLoginState();
    }

    /**
     * 检查是否登录
     */
    private void checkLoginState() {
        xiaButton.setText("立即下单");
        //点击下单按钮
        xiaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //已经登录
                if (null != SaveSettingUtil.getUserSid(context)) {
                    startActivity(new Intent(context, XiaOrderActivity.class).putExtra(IConstant.FEIDOU, numberFeidou));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
        //已经登录
        if (null != SaveSettingUtil.getUserSid(context)) {
            //请求个人数据
//            getCustomeInfor();
            getPersonInfor();

        } else {
            //没有登录
            mName.setVisibility(View.INVISIBLE);//个人信息设为不可见
        }
    }

    /**
     * 请求个人数据
     */
    private void getPersonInfor() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(this));
        volley_get(COMSTOME_INFO_URL, map);
    }

    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        Gson gson = new Gson();
        CustomBean data = gson.fromJson(response, CustomBean.class);
        setCustomData(data);
    }

    /**
     * get客户信息
     */
/*    private void getCustomeInfor() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(this));
        HttpClientUtil.getInstance().doGetAsyn(COMSTOME_INFO_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                costomResult = result;
                Message msg = Message.obtain();
                msg.what = COM_CODE;
                mHandler.sendMessage(msg);
            }
        });
    }*/


/*    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //登录结果
                case COM_CODE:
                    if (costomResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(context, costomResult);
                    } else {
                        Gson gson = new Gson();
                        CustomBean data = gson.fromJson(costomResult, CustomBean.class);
                        setCustomData(data);

                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/
    private void setCustomData(CustomBean data) {
        //显示个人信息
        mName.setVisibility(View.VISIBLE);
        numberFeidou = data.getIntegral();
        mName.setText(data.getUser() + "\n" + numberFeidou + " 飞豆");

        if (data.getIsOff() == 1) {
            xiaButton.setText("暂不可下单");
            xiaButton.setBackgroundResource(R.drawable.button_grey);
        } else {
            xiaButton.setBackgroundResource(R.drawable.xiadan_button);
            xiaButton.setText("立即下单");
            xiaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, XiaOrderActivity.class).putExtra(IConstant.FEIDOU, numberFeidou));
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            });
        }
    }

    private void initView() {
        mName = (TextView) findViewById(R.id.name_text);
        //下单button
        xiaButton = (Button) findViewById(R.id.xia_order_button);
/*        Button orderButton = (Button) findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });*/
        //飞派价格
        findViewById(R.id.fei_price_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PriceActivity.class);
            }
        });
        //常见问题
        findViewById(R.id.comquestion_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(QuestionActivity.class);
            }
        });
    }

    /**
     * 启动方法
     *
     * @param cls 要启动类
     */
    protected void startActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(context, cls));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void setupSlidingMenu() {
        mMenu = new SlidingMenu(this);

        mMenu.setMode(SlidingMenu.LEFT);
        mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mMenu.setShadowWidthRes(R.dimen.menu_shadow_width);
        mMenu.setShadowDrawable(R.drawable.shadow_menu);
        // 设置滑动菜单视图的宽度
        mMenu.setBehindOffsetRes(R.dimen.menu_behind_offset);
        mMenu.setFadeDegree(0.35f);
        mMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

        mMenu.setMenu(R.layout.menu_frame);

        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame,
                new MenuFragment()).commit();
    }

    public SlidingMenu getSlidingMenu() {
        return mMenu;
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (null != SaveSettingUtil.getUserSid(context)) {
                    mMenu.toggle();
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
