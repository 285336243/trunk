package com.shengzhish.xyj.persionalcore;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.Response;
import com.shengzhish.xyj.core.AbstractRoboAsyncTask;
import com.shengzhish.xyj.core.DialogFragment;
import com.shengzhish.xyj.core.Intents;
import com.shengzhish.xyj.core.LightProgressDialog;
import com.shengzhish.xyj.core.ProgressDialogTask;
import com.shengzhish.xyj.core.ToastUtils;
import com.shengzhish.xyj.http.HttpUtils;
import com.shengzhish.xyj.persionalcore.entity.Login;
import com.shengzhish.xyj.persionalcore.entity.MessageAmount;
import com.shengzhish.xyj.persionalcore.entity.Update;
import com.shengzhish.xyj.util.CacheDataKeeper;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.ImageUtils;
import com.shengzhish.xyj.util.LoginUtilSh;
import com.shengzhish.xyj.util.NetWorkConnect;

import roboguice.inject.InjectView;

/**
 * 个人中心
 */
public class PersionalCoreFragment extends DialogFragment {

    @Inject
    UserPersonService service;

    @InjectView(R.id.prob_loading)
    private View pbLoading;
    @InjectView(R.id.scrollview)
    private View scrollView;
    @InjectView(R.id.person_information)
    private View personInfor;
    @InjectView(R.id.message_notice)
    private View messageNotice;
    @InjectView(R.id.feed_back)
    private View feedBsck;
    @InjectView(R.id.clear_cache)
    private View clearCache;
    @InjectView(R.id.check_update)
    private View checkUpdate;
    @InjectView(R.id.about_us)
    private View aboutUs;
    @InjectView(R.id.exit_accout)
    private View exitAccout;

    @InjectView(R.id.person_nikename)
    private TextView personNickName;
    @InjectView(R.id.person_photo)
    private ImageView userHeadPhoto;

    @InjectView(R.id.gender)
    private ImageView genderImageView;
    @InjectView(R.id.message_amount)
    private TextView messageAmount;
    @InjectView(R.id.current_version)
    private TextView currentVersion;

    private String heahView;
    private String name;
    private String sex;
    private LogintBroadcastReceiver mLoginReceiver;
    private AlertDialog progress;
    private Context context;
    private Gson gson;
    private boolean isHaveCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        mLoginReceiver = new LogintBroadcastReceiver();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(IConstant.PERSON_LOGIN);
        iFilter.addAction(IConstant.MODIFY_PERSON_INFORMATION);
        iFilter.addAction(IConstant.REGISTER_BROADCAST);
        getActivity().registerReceiver(mLoginReceiver, iFilter);
    }

    private Handler handler = new Handler() {
        /**
         * Subclasses must implement this to receive messages.
         *
         * @param msg
         */
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progress.dismiss();
            ToastUtils.show(getActivity(), "缓存清除完成");
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.person_center_layout, null);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gson = new Gson();
        personInfor.setOnClickListener(listeners);

        exitAccout.setOnClickListener(listeners);

        isHaveCache = CacheDataKeeper.isCatchPersonResponse(getActivity());
        if (isHaveCache) {
            String json = CacheDataKeeper.getPersonResponse(getActivity());
            Login data = gson.fromJson(json, Login.class);
            setDataContent(data);
        } else {
            downLoadPersonInfoemation();
        }
        if (NetWorkConnect.isConnect(getActivity())) {
            messageNotice();
        }
        try {
            currentVersion.setText("当前版本为:" + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), PackageManager.GET_ACTIVITIES).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        messageNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intents(getActivity(), MessageNotificationActivity.class).toIntent());
            }
        });

        feedBsck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intents(getActivity(), OpinionFeedBackActivity.class).toIntent());
            }
        });

        clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                progress = LightProgressDialog.create(getActivity(), "");
                progress.show();
                ImageLoader.getInstance().clearDiskCache();
                CacheDataKeeper.clear(context);
                handler.sendEmptyMessageDelayed(0, 1000);

            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intents(getActivity(), AboutActivity.class).toIntent());
            }
        });
        if (NetWorkConnect.isConnect(getActivity())) {
            checkUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final int code = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
                        new ProgressDialogTask<Update>(getActivity()) {
                            /**
                             * Execute task with an authenticated account
                             *
                             * @param data
                             * @return result
                             * @throws Exception
                             */
                            @Override
                            protected Update run(Object data) throws Exception {
                                return (Update) HttpUtils.doRequest(service.createUpdateRequest()).result;
                            }

                            /**
                             * Sub-classes may override but should always call super to ensure the
                             * progress dialog is dismissed
                             *
                             * @param update
                             */
                            @Override
                            protected void onSuccess(Update update) throws Exception {
                                super.onSuccess(update);
                                if (update != null) {
                                    if (update.getResponseCode() == 0) {
                                        if (update.getVersionCode() > code) {

                                            UpdateDialog dialog = new UpdateDialog(getActivity(), update.getUpgradeUrl());
                                            View view = getLayoutInflater(null).inflate(R.layout.update_dialog, null);
                                            dialog.setContentView(view);
                                            if (!dialog.isShowing())
                                                dialog.show();
                                        } else {
                                            Toast toast = Toast.makeText(getActivity(), "当前已是最新版本", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();

                                        }
                                    } else {
                                        ToastUtils.show(getActivity(), update.getResponseMessage());
                                    }
                                }
                            }
                        }.start("正在检查更新");
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void downLoadPersonInfoemation() {
        new AbstractRoboAsyncTask<Login>(context) {
            @Override
            protected Login run(Object data) throws Exception {
                return (Login) HttpUtils.doRequest(service.createPersonRequest()).result;
            }

            @Override
            protected void onSuccess(Login loginbean) throws Exception {
                super.onSuccess(loginbean);
                if (loginbean != null) {
                    String json = gson.toJson(loginbean);
                    CacheDataKeeper.savePersonResponse(getActivity(), json);
                    setDataContent(loginbean);
                }
            }
        }.execute();


    }

    private void setDataContent(Login loginbean) {
        if (loginbean != null) {
            scrollView.setVisibility(View.VISIBLE);
            pbLoading.setVisibility(View.GONE);
            if (loginbean.getResponseCode() == 0) {
                userInfor(loginbean);
                personNickName.setText(loginbean.getUser().getNickname());
                if (!TextUtils.isEmpty(loginbean.getUser().getAvatar())) {
                    ImageLoader.getInstance().displayImage(loginbean.getUser().getAvatar(), userHeadPhoto, ImageUtils.imageLoader(getActivity(), 1000));
                }
                if (!TextUtils.isEmpty(loginbean.getUser().getGender())) {
                    if ("m".equals(loginbean.getUser().getGender()))
                        genderImageView.setImageResource(R.drawable.icon_boy_sd);
                    else
                        genderImageView.setImageResource(R.drawable.icon_girl_sd);
                }

            }
        }
    }

    private void userInfor(Login loginbean) {
        heahView = loginbean.getUser().getAvatar();
        name = loginbean.getUser().getNickname();
        sex = loginbean.getUser().getGender();
    }

    private void messageNotice() {
        new AbstractRoboAsyncTask<MessageAmount>(context) {

            @Override
            protected MessageAmount run(Object data) throws Exception {
                return (MessageAmount) HttpUtils.doRequest(service.createMessageRequest()).result;
            }

            @Override
            protected void onSuccess(MessageAmount message) throws Exception {
                super.onSuccess(message);
                if (message != null) {
                    if (message.getResponseCode() == 0) {
                        if (!TextUtils.isEmpty(message.getTotal()))
                            messageAmount.setText(message.getTotal());
                    } else {
//                        ToastUtils.show(getActivity(), message.getResponseMessage());
                    }
                }

            }
        }.execute();


    }

    private View.OnClickListener listeners = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check_update:

                    break;
                case R.id.exit_accout:
                    exitAccout();
                    break;
                case R.id.person_information:
                    Intent modifyIntent = new Intent(getActivity(), ModifyPersonInfoActivity.class);
                    modifyIntent.putExtra(IConstant.USER_PHOTO, heahView);
                    modifyIntent.putExtra(IConstant.USER_NAME, name);
                    modifyIntent.putExtra(IConstant.USER_SEX, sex);
                    startActivity(modifyIntent);
                    break;

            }

        }
    };

    private void exitAccout() {
        new AbstractRoboAsyncTask<Response>(getActivity()) {
            @Override
            protected Response run(Object data) throws Exception {

                return (Response) HttpUtils.doRequest(service.creatExitRequest()).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        genderImageView.setImageDrawable(null);
                        userHeadPhoto.setImageDrawable(null);
                        personNickName.setText(null);
                        clearUserInfor();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.putExtra("exit", 25);
                        startActivity(intent);
                    } else
                        ToastUtils.show(getActivity(), response.getResponseMessage());
                }
            }
        }.execute();

    }

    private void clearUserInfor() {
        LoginUtilSh.clear(getActivity());
        HttpUtils.clearCookie();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoginReceiver != null) {
            getActivity().unregisterReceiver(mLoginReceiver);
        }
    }

    private class LogintBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if ((IConstant.PERSON_LOGIN).equals(intent.getAction()) || intent.getAction().equals(IConstant.MODIFY_PERSON_INFORMATION) || intent.getAction().equals(IConstant.REGISTER_BROADCAST)) {
//                    Log.v("person", "===收到广播==");
                    if (View.GONE == pbLoading.getVisibility()) {
                        pbLoading.setVisibility(View.VISIBLE);
                    }
                    if (View.VISIBLE == scrollView.getVisibility()) {
                        scrollView.setVisibility(View.GONE);
                    }
                    downLoadPersonInfoemation();
                }
            }
        }
    }

}
