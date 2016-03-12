package feipai.qiangdan.other;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragment;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.javabean.VersionBean;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.ProgressDlgUtil;
import feipai.qiangdan.util.SaveSettingUtil;
import feipai.qiangdan.util.VolleyUtil;
import roboguice.inject.InjectView;

/**
 *
 */
public class OtherFragement extends DialogFragment {

    private static final String CHECK_VERSION_URL = "api/v1/version";
    private static final int VERSION_INFOR = 0XB2;
    @InjectView(R.id.list_view)
    private ListView listView;
    private String veisionInfol;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_other, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OtherListAdapter adapter = new OtherListAdapter(getActivity());
        adapter.setItems(getData());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getActivity(), "点击了 position = " + position, Toast.LENGTH_SHORT).show();
                selectedItem(position);
            }
        });
    }

    private void selectedItem(int position) {
        switch (position) {
            case 0:
                //手册
                startOther(ReferenceActivity.class);
                break;
            case 1:
                //常见问题
                startOther(CommonQuestionActivity.class);
                break;
            case 2:
                //价格表
                // startOther(PriceActivity.class);
                WebViewActivity.launch(getActivity(), "价格时效表", IConstant.DOMAIN + "api/v1/price/fp");
                break;
            case 3:
                //客服电话
                startOther(ServicePhoneActivity.class);
                break;
            case 4:
                //离线地图
                startOther(OffineMapActivity.class);
                break;
            case 5:
                //通知
                WebViewActivity.launch(getActivity(), "通知", IConstant.DOMAIN + "api/v1/inform");
                break;
            case 6:
                //设置
                startOther(SettingActivity.class);
                break;
            case 7:
                //意见反馈
                startOther(FeedBackActivity.class);
                break;
            case 8:
                //密码修改
                startOther(ModifyPasswordActivity.class);
                break;
            case 9:
                //版本升级
                checkUpdateMethd();
                break;
            default:
                break;
        }
    }

    private void checkUpdateMethd() {
        ProgressDlgUtil.showProgressDlg(getActivity(), "正在检查版本");
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("sid", SaveSettingUtil.getUserSid(getActivity()));
        VolleyUtil.getInstance(getActivity()).volley_get(CHECK_VERSION_URL, map, new VolleyUtil.OnCompleteListener() {
            @Override
            public void correct(String str) {
                ProgressDlgUtil.stopProgressDlg();
                try {
                    code = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                Gson gson = new Gson();
                VersionBean response = gson.fromJson(str, VersionBean.class);
                if (response.getVersionNum() > code) {
                    UpgradeVersonDialog.createUpdateDialog(getActivity(), response.getLink(),
                            response.getVersion(), response.getInfo());
                } else {
                    UpgradeVersonDialog.needNotUpdate(getActivity());
                }

            }

            @Override
            public void error(VolleyError error) {
                ProgressDlgUtil.stopProgressDlg();
            }
        });
/*        HttpClientUtil.getInstance().doGetAsyn(CHECK_VERSION_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                veisionInfol = result;
                Message msg = Message.obtain();
                msg.what = VERSION_INFOR;
                mHandler.sendMessage(msg);

            }
        });*/
    }

    private int code;
/*    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VERSION_INFOR:
                    ProgressDlgUtil.stopProgressDlg();

                    try {
                        code = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (veisionInfol.contains(IConstant.REQUST_FAIL)) {
                        ToastUtils.show(getActivity(), veisionInfol);
                    } else {
                        Gson gson = new Gson();
                        VersionBean response = gson.fromJson(veisionInfol, VersionBean.class);
                        if (response.getVersionNum() > code) {
                            UpgradeVersonDialog.createUpdateDialog(getActivity(), response.getLink(),
                                    response.getVersion(), response.getInfo());
                        } else {
                            UpgradeVersonDialog.needNotUpdate(getActivity());
                        }
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    private void startOther(Class<?> aClass) {
        startActivity(new Intent(getActivity(), aClass));
    }


    private List<SetBean> getData() {
        String[] name = new String[]{"飞派员手册", "常见问题", "价格时效表", "客服电话", "离线地图", "通知", "设置",
                "我要吐槽", "密码修改", "版本升级"};
        int[] icon = new int[]{R.drawable.icon_handbook,
                R.drawable.icon_nomalproblem, R.drawable.icon_price,
                R.drawable.icon_telephone, R.drawable.icon_map,
                R.drawable.icon_notification, R.drawable.icon_set,
                R.drawable.icon_opinion, R.drawable.icon_password,
                R.drawable.icon_vertions};
        List<SetBean> setData = new LinkedList<SetBean>();
        for (int i = 0; i < name.length; i++) {
            setData.add(new SetBean(name[i], icon[i]));
        }
        return setData;
    }
}
