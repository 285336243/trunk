package feipai.qiangdan.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.my.LoginActivity;
import feipai.qiangdan.util.SaveSettingUtil;

import feipai.qiangdan.view.CheckSwitchButton;
import roboguice.inject.InjectView;


/**
 *
 */
public class SettingActivity extends DialogFragmentActivity {

    @InjectView(R.id.about_back)
    private View backView;

    @InjectView(R.id.setting_sound)
    private CheckSwitchButton soundButton;

    @InjectView(R.id.setting_vibration)
    private CheckSwitchButton vibrationButton;

    @InjectView(R.id.sign_out_layout)
    private View signOut;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting);
        context = SettingActivity.this;
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        Log.v("kan", "  启动设定中心");
        soundButton.setChecked(SaveSettingUtil.isSound(this));
        soundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Toast.makeText(SettingActivity.this, "声音  sOn状态 :" + isChecked, Toast.LENGTH_SHORT).show();
//                Log.v("kan", "声音  sOn状态 :" + isChecked);
                if (isChecked) {
                    Toast.makeText(SettingActivity.this, "声音 状态 :" + "开", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingActivity.this, "声音 状态  :" + "关", Toast.LENGTH_SHORT).show();
                }
                SaveSettingUtil.saveSoundState(SettingActivity.this, isChecked);
            }
        });

        vibrationButton.setChecked(SaveSettingUtil.isVibrate(this));
        vibrationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.v("kan", "震动 sOn状态 :" + isChecked);
                if (isChecked) {
                    Toast.makeText(SettingActivity.this, "震动 状态 :" + "开", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingActivity.this, "震动 状态 :" + "关", Toast.LENGTH_SHORT).show();
                }
                SaveSettingUtil.saveVibrateState(SettingActivity.this, isChecked);
            }
        });
        //退出帐号
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveSettingUtil.removeLoginSid(SettingActivity.this);
                startActivity(new Intent(context, LoginActivity.class));
                finish();
            }
        });
    }


}


