package com.example.xiadan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.VolleyError;
import com.example.xiadan.loginregister.LoginActivity;
import com.example.xiadan.menucontent.AboutUsActivity;
import com.example.xiadan.menucontent.CouponActivity;
import com.example.xiadan.menucontent.FeedBackActivity;
import com.example.xiadan.menucontent.ModifyPassworsdActivity;
import com.example.xiadan.menucontent.MyJiFenActivity;
import com.example.xiadan.menucontent.MyOderActivity;
import com.example.xiadan.util.SaveSettingUtil;
import com.example.xiadan.util.VolleyUtil;
import com.example.xiadan.whole.AppManager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public class MenuFragment extends SherlockFragment {
    private static final int LOGINOUT_CODE = 0X7F;
    private static final String LOGINOUT_URL = "page/api/v1/common/logout";
    protected MainActivity mFragmentContainer;

    private View mView;
    private ListView mCategories;
    private List<String> mDatas = Arrays
            .asList("我的订单", "我的优惠劵", "我的飞豆", "意见反馈", "密码修改", "关于我们", "退出登录");
    private ListAdapter mAdapter;
    //登出结果
    private String loginoutResult;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mView == null) {
            initView(inflater, container);
        }
        return mView;
    }


    private void initView(LayoutInflater inflater, ViewGroup container) {
        mView = inflater.inflate(R.layout.left_menu, container, false);
        mCategories = (ListView) mView.findViewById(R.id.listview_categories);
        mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mDatas);
        mCategories.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        createTableView();
        // dismiss menu if it's currently showing, it reduces animation glitch while creating fragment.
        SlidingMenu menu = mFragmentContainer.getSlidingMenu();
        if (menu.isMenuShowing()) {
            mFragmentContainer.getSlidingMenu().toggle();
        }
        mCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected(position);
//                ToastUtils.show(getActivity(), "点击了" + position);
            }
        });

    }

    private void selected(int position) {
        switch (position) {
            //我的订单列表
            case 0:
                startOther(MyOderActivity.class);
                break;
            //我的优惠券
            case 1:
                startOther(CouponActivity.class);
                break;
            //我的积分
            case 2:
                startOther(MyJiFenActivity.class);
                break;
            //意见反馈
            case 3:
                startOther(FeedBackActivity.class);
                break;
            //修改密码
            case 4:
                startOther(ModifyPassworsdActivity.class);
                break;
            //关于我们
            case 5:
                startOther(AboutUsActivity.class);
                break;
            //退出
            case 6:
                if (null == SaveSettingUtil.getUserSid(getActivity())) {
                    return;
                }
//                loginOut();
                loginOutVolley();
                break;
            default:
                break;
        }
    }

    private void loginOutVolley() {
        final HashMap<String, String> map = new HashMap<String, String>();
        //登陆验证码
        map.put("sid", SaveSettingUtil.getUserSid(getActivity()));
        VolleyUtil.getInstance(getActivity()).volley_put(LOGINOUT_URL, map,new VolleyUtil.OnCompleteListener() {
            @Override
            public void correct(String str) {
                //移除登录验证码
                SaveSettingUtil.removeLoginSid(getActivity());
                AppManager.getAppManager().AppExit(getActivity());
            }

            @Override
            public void error(VolleyError str) {
                SaveSettingUtil.removeLoginSid(getActivity());
                AppManager.getAppManager().AppExit(getActivity());
            }
        });
    }

/*    private void loginOut() {
        ProgressDlgUtil.showProgressDlg(getActivity(), "正在登出......");
        final Map<String, String> map = new HashMap<String, String>();
        //登陆验证码
        map.put("sid", SaveSettingUtil.getUserSid(getActivity()));
        HttpClientUtil.getInstance().doPutAsyn(LOGINOUT_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                loginoutResult = result;
                Message msg = Message.obtain();
                msg.what = LOGINOUT_CODE;
                mHandler.sendMessage(msg);
            }
        });
    }*/

/*    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //登录结果
                case LOGINOUT_CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (loginoutResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(getActivity(), loginoutResult);
                        SaveSettingUtil.removeLoginSid(getActivity());
                        AppManager.getAppManager().AppExit(getActivity());
                    } else {
                        //移除登录验证码
                        SaveSettingUtil.removeLoginSid(getActivity());
                        AppManager.getAppManager().AppExit(getActivity());
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    private void startOther(Class<?> aClass) {
        //验证有无登录
        if (null != SaveSettingUtil.getUserSid(getActivity())) {
            startActivity(new Intent(getActivity(), aClass));
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof MainActivity) mFragmentContainer = (MainActivity) activity;
        else throw new RuntimeException("Fragment must be attached to an instance of MainActivity");
        super.onAttach(activity);
    }
}
