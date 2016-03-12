package com.jiujie8.choice.setting;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.jiujie8.choice.R;
import com.jiujie8.choice.core.ThirdPartyShareActivity;
import com.jiujie8.choice.core.ToastUtils;
import com.jiujie8.choice.publicentity.User;
import com.jiujie8.choice.setting.entity.ClientVersion;
import com.jiujie8.choice.setting.entity.SetBean;
import com.jiujie8.choice.setting.entity.ShareBean;
import com.jiujie8.choice.setting.entity.ShareFriend;
import com.jiujie8.choice.setting.entity.UpdateBean;
import com.jiujie8.choice.setting.entity.UserDetails;
import com.jiujie8.choice.util.AppManager;
import com.jiujie8.choice.util.HttpHelp;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.util.ImageUtils;
import com.jiujie8.choice.util.LoginUtil;
import com.jiujie8.choice.util.StorageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 *
 */
@ContentView(R.layout.activity_setting)
public class SettingActivity extends ThirdPartyShareActivity {

    private static final int REQUST_CODE = 0x12;
    private static final String UPDATE_VERSION_URL = "identity/client/update";
    private static final String SHARE_URL = "identity/user/shareFriend";
    @InjectView(R.id.list_view)
    private ListView listView;

    private CustomProgressDialog progressDialog;
    private Handler mHandler = new Handler();
    private static final String USER_DETAILS = "identity/user/details";
    private View headerView;
    private Bundle bundle;
    private int code;
    private ShareFriend shareFrind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_choice);
        actionBar.setTitle("设置");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);
        headerView = LayoutInflater.from(this).inflate(R.layout.setting_header, null);
        listView.addHeaderView(headerView);

        SettingListAdapter adapter = new SettingListAdapter(this);
        adapter.setItems(getData());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(SettingActivity.this, "点击了 position = " + position, Toast.LENGTH_SHORT).show();
                selectedItem(position);
            }
        });
        loadUserData();
        bundle = new Bundle();
    }

    private void loadUserData() {
        final boolean isLogin = LoginUtil.isLogin();
        if (isLogin) {
            HttpHelp.getInstance().getHttp(this, UserDetails.class, USER_DETAILS, null, new HttpHelp.OnCompleteListener<UserDetails>() {
                @Override
                public void onComplete(UserDetails response) {
                    if (IConstant.STATE_OK.equals(response.getCode())) {
                        User user = response.getEntity().getUser();
                        setDataToHeader(user);
                        bundle.clear();
                        bundle.putSerializable(IConstant.USER, user);
                    } else {
                        ToastUtils.show(activity, response.getMessage());
                    }
                }
            });
        }
    }

    private void setDataToHeader(User user) {
        ImageView header = (ImageView) headerView.findViewById(R.id.user_header);
        ImageView gender = (ImageView) headerView.findViewById(R.id.user_gender);
        TextView name = (TextView) headerView.findViewById(R.id.user_name);
        String imageAddress = user.getAvatar();
        if (imageAddress.contains("http:")) {
            ImageLoader.getInstance().displayImage(imageAddress, header, ImageUtils.avatarImageLoader());
        } else {
            ImageLoader.getInstance().displayImage(Uri.fromFile(new File(imageAddress)).toString(), header, ImageUtils.avatarImageLoader());
        }
        name.setText(user.getNickname());
        if ("male".equals(user.getGender())) {
            gender.setImageResource(R.drawable.icon_man_choice);
        }
        if ("female".equals(user.getGender())) {
            gender.setImageResource(R.drawable.icon_lady_choice);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopProgressDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUST_CODE && resultCode == RESULT_OK) {
            User user = (User) data.getSerializableExtra(IConstant.USER_MODIFY);
            setDataToHeader(user);
        }

    }


    private void selectedItem(int position) {
        switch (position) {
            case 0:
                //修改个人资料
                startActivityForResult(new Intent(SettingActivity.this, ModifyPersonInforActivity.class).putExtras(bundle), REQUST_CODE);
                break;
            case 2:
                //告诉小伙伴
                shareFrinends();
                break;
            case 3:
                //提点意见
                startOther(FeedBackActivity.class);
                break;
            case 5:
                //清楚缓存
                clearCatch();
                break;
            case 6:
                //检查更新
                check_update();
                break;
            case 7:
                //玩转纠结
            case 8:
                //用户协议
                userProtocol();
                break;
            case 10:
                //退出当前帐号
                signOut();
                break;
            default:
                break;
        }
    }

    private void userProtocol() {
        startOther(UserProtocoActivity.class);
    }

    private void shareFrinends() {

        HttpHelp.getInstance().getHttp(this, ShareBean.class,SHARE_URL,null,new HttpHelp.OnCompleteListener<ShareBean>() {
            @Override
            public void onComplete(ShareBean response) {
                if(response.getCode().equals(IConstant.STATE_OK)){
                   shareFrind= response.getEntity().getShareFriend();

                    creatShareDialog(shareFrind);
                }else{
                    ToastUtils.show(SettingActivity.this,response.getMessage());
                }

            }
        });



    }

    private void creatShareDialog(final ShareFriend shareFrind) {
        final String webUrl = shareFrind.getShareUrl();
        final String shareText = shareFrind.getShareText();
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon);
        final SetShareDialog shareDialog = new SetShareDialog();
        shareDialog.show(getSupportFragmentManager(), "blur");
        shareDialog.setDialogListener(new SetShareDialog.DialogListener() {
            /**
             * 分享新浪微博
             */
            @Override
            public void shareSina() {
                sinaSharePic(bitmap,shareText+":"+webUrl);
            }

            /**
             * 分享微信朋友圈
             */
            @Override
            public void shareWeiXinFriends() {
                shareWeiXinWebPage(webUrl,"纠结分享",shareText,bitmap);
            }
            /**
             * 分享微信好友
             */
            @Override
            public void shareWeiXinGoodFriend() {
                shareWeiXinFriendWebPage(webUrl,"纠结分享",shareText,bitmap);
            }
        });
    }

    private void check_update() {
        try {
            code = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<String, String>();
//        map.put("client_version",String.valueOf(code));
        map.put("client_platform", "android");
        HttpHelp.getInstance().getHttp(this, UpdateBean.class, UPDATE_VERSION_URL, map, new HttpHelp.OnCompleteListener<UpdateBean>() {
            @Override
            public void onComplete(UpdateBean response) {
                if (response.getCode().equals((IConstant.STATE_OK))) {
                    ClientVersion version = response.getEntity().getClientVersion();
                    Bundle bundle = new Bundle();
                    bundle.putString("updateUrl", version.getUpdateUrl());
                    bundle.putString("versionNo", version.getVersion());
                    bundle.putString("updateMessage", version.getUpdateNote());

                    UpdateDialog mDialog;
                    if (version.getVersionCode() > code) {
                        bundle.putBoolean("isUpdate", true);
                    } else {
                        bundle.putBoolean("isUpdate", false);
                    }
                    mDialog = UpdateDialog.newInstance(bundle);
                    mDialog.show(getSupportFragmentManager(), "blur");
                } else {
                    ToastUtils.show(SettingActivity.this, response.getMessage());
                }

            }
        });


    }

    private void signOut() {
        LoginUtil.clear();
        AppManager.getAppManager().AppExit(activity);
    }

    /**
     * 清除缓存
     */
    private void clearCatch() {
        //启动进度框
        startProgressDialog();
        new Thread(new Runnable() {
            public void run() {
                StorageUtil.clearFile(new File(Environment.getExternalStorageDirectory(), "JiuJie"));
                StorageUtil.clearFile(new File(StorageUtil.getSDCardPath(SettingActivity.this)));
                ImageLoader.getInstance().clearDiskCache();
            }
        }).start();

        mHandler.postDelayed(mRunnable, 1000);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            stopProgressDialog();
            ToastUtils.show(SettingActivity.this, "缓存清除完成");
        }
    };

    private void startOther(Class<?> cls) {
        startActivity(new Intent(SettingActivity.this, cls));
    }

    private List<SetBean> getData() {
        String[] itemTitle = {null, "告诉小伙伴", "提点意见", null, "清除缓存", "检查更新", "玩转纠结", "用户协议", null, "退出当前帐号"};
        List<SetBean> setData = new LinkedList<SetBean>();
        for (String str : itemTitle) {
            setData.add(new SetBean(str));
        }
        return setData;
    }

    private void startProgressDialog() {
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(this);
            progressDialog.setMessage("正在清除中...");
        }
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void stopProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
