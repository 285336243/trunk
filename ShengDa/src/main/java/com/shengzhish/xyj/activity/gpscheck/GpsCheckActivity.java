package com.shengzhish.xyj.activity.gpscheck;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//import com.gitonway.niftydialogeffects.widget.niftydialogeffects .Effectstype;
//import com.gitonway.niftydialogeffects.widget.niftydialogeffects.NiftyDialogBuilder;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.ActivityDetails;
import com.shengzhish.xyj.activity.entity.ShakeResponse;
import com.shengzhish.xyj.core.DialogFragmentActivity;
import com.shengzhish.xyj.http.HttpboLis;
import com.shengzhish.xyj.util.IConstant;

import roboguice.inject.InjectView;

/**
 * GPS签到
 */
public class GpsCheckActivity extends DialogFragmentActivity {

    @InjectView(R.id.back_imageview)
    private View back;
    @InjectView(R.id.rule_textview)
    private TextView ruleTextview;
    @InjectView(R.id.gps_check_button)
    private Button gpsCheckButton;


    private Location location;
    private Dialog dialog;
   /* NiftyDialogBuilder dialogBuilder;*/

    private String id;
    private int isJoined;
    private View.OnClickListener checkclickLisenner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (location == null) {
                Toast.makeText(GpsCheckActivity.this, "无法获得您的位置信息，请检查您的GPS设置", Toast.LENGTH_SHORT).show();

            } else {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String url = String.format("activity/gps.json?id=%s&lat=%s&lng=%s", id, String.valueOf(latitude), String.valueOf(longitude));
                HttpboLis.getInstance().getHttpDialog(GpsCheckActivity.this,
                        ShakeResponse.class, url, "正在签到", new HttpboLis.OnCompleteListener<ShakeResponse>() {
                    @Override
                    public void onComplete(ShakeResponse response) {
                        if (response.getResponseCode() == 0) {
                            showMessageDialog(response.getResponseMessage());
                            gpsCheckButton.setText("已签到");
                            gpsCheckButton.setBackgroundResource(R.drawable.corner_shake_once_more_button);
                            gpsCheckButton.setEnabled(false);
                        } else {
                            Toast.makeText(GpsCheckActivity.this, response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    };
    // Gps消息监听器
    private LocationListener locationListener = new LocationListener() {

        // 位置发生改变后调用
        @Override
        public void onLocationChanged(Location location) {
            GpsCheckActivity.this.location = location;
        }

        // provider状态变化时调用
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        // provider被用户关闭后调用
        @Override
        public void onProviderEnabled(String provider) {
            GpsCheckActivity.this.location = null;
        }

        // provider被用户开启后调用
        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    private void showMessageDialog(String message) {
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.gps_check_dialog_layout);
        TextView checkRezultRextview = (TextView) dialog.findViewById(R.id.check_rezult_textview);
        checkRezultRextview.setText(message);
        dialog.findViewById(R.id.back_imageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });

        if (!dialog.isShowing())
            dialog.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_check_layout);
        WindowManager mWindowManager = this.getWindowManager();


        id = getIntent().getStringExtra(IConstant.ACTIVITY_ID);
        isJoined = getIntent().getIntExtra(IConstant.ACTIVITY_ISJOINED, 1000);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gpsCheckButton.setOnClickListener(checkclickLisenner);

        if (isJoined == 1) {
            gpsCheckButton.setText("已签到");
            gpsCheckButton.setBackgroundResource(R.drawable.corner_shake_once_more_button);
            gpsCheckButton.setEnabled(false);
        } else {
            gpsCheckButton.setText("签 到");
            gpsCheckButton.setBackgroundResource(R.drawable.check_button_icon4);
            gpsCheckButton.setEnabled(true);
        }

        HttpboLis.getInstance().getHttpDialog(this, ActivityDetails.class, String.format("activity/detail.json?id=%s", id), "正在加载...", new HttpboLis.OnCompleteListener<ActivityDetails>() {
            @Override
            public void onComplete(ActivityDetails response) {
                if (!TextUtils.isEmpty(response.getActivity().getRule())) {
                    ruleTextview.setText(response.getActivity().getRule() + "\n");
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        openGPSSettings();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            openGPSSettings();
        }

    }


    private void openGPSSettings() {
        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS模块正常", Toast.LENGTH_SHORT).show();
            getLocation();
            return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(GpsCheckActivity.this);
        dialog.setTitle("注意").setMessage("需要打开GPS才能签到，现在打开GPS?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 0); //此为设置完成后返回到获取界面
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();//取消弹出框
            }
        }).create().show();

/*        try {
            dialogBuilder= NiftyDialogBuilder.getInstance(GpsCheckActivity.this);

            dialogBuilder.withTitle("Modal Dialog") // .withTitle(null) no title
                    .withTitleColor("#FFFFFF") // def
                    .withDividerColor("#11000000") // def
                    .withMessage("This is a modal Dialog.") // .withMessage(null) no
                            // Msg
                    .withMessageColor("#FFFFFF") // def
                    .withIcon(getResources().getDrawable(R.drawable.icon)).isCancelableOnTouchOutside(true) // def
                    // |
                    // isCancelable(true)
                    .withDuration(700) // def
                    .withEffect(Effectstype.SlideBottom) // def Effectstype.Slidetop
                    .withButton1Text("OK") // def gone
                    .withButton2Text("Cancel") // def gone
                    .setCustomView(R.layout.custom_view, GpsCheckActivity.this) // .setCustomView(View
                            // or
                            // ResId,context)
                    .setButton1Click(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(), "i'm btn1", Toast.LENGTH_SHORT).show();
                        }
                    }).setButton2Click(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "i'm btn2", Toast.LENGTH_SHORT).show();
                    dialogBuilder.cancel();
                }
            }).show();
        } catch (RuntimeException e) {
              *//*   if(dialogBuilder!=null&&!dialogBuilder.isShowing())
                     dialogBuilder.show();*//*
            Toast.makeText(this, "有问题"+e.toString(), Toast.LENGTH_LONG).show();
        }*/

    }

    private void getLocation() {
        // 获取位置管理服务
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) this.getSystemService(serviceName);
        // 构建位置查询条件
        Criteria criteria = new Criteria();
        // 查询精度：高
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        // 是否查询海拨：否
        criteria.setAltitudeRequired(false);
        // 是否查询方位角:否
        criteria.setBearingRequired(false);
        // 是否允许付费：是
        criteria.setCostAllowed(true);
        // 使用省电模式
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        // 返回最合适的符合条件的provider，第2个参数为true说明,如果只有一个provider是有效的,则返回当前provider
        String provider = locationManager.getBestProvider(criteria, true);
        // 获得当前的位置
        location = locationManager.getLastKnownLocation(provider);
//        updateToNewLocation(location);
        // 注册监听器locationListener，第2、3个参数可以控制接收gps消息的频度以节省电力。第2个参数为毫秒，
        // 表示调用listener的周期，第3个参数为米,表示位置移动指定距离后就调用listener
        locationManager.requestLocationUpdates(provider, 10 * 1000, 50, locationListener);
    }

/*    private void updateToNewLocation(Location location) {


        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            ruleTextview.setText("维度：" + latitude + "\n经度" + longitude);
        } else {
            ruleTextview.setText("无法获取地理信息");
        }

    }*/

}
