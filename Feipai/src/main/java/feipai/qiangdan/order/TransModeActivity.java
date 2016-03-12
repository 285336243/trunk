package feipai.qiangdan.order;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.core.Log;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.javabean.OrderItem;
import feipai.qiangdan.javabean.OrderListBean;
import feipai.qiangdan.util.AudioHelp;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.SaveSettingUtil;
import feipai.qiangdan.util.ScreenUtil;
import feipai.qiangdan.util.Utils;
import feipai.qiangdan.util.VibrateHelper;
import roboguice.inject.InjectView;

/**
 *
 */
public class TransModeActivity extends DialogFragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = new View(this);
        setContentView(view);

        String jAddress = getIntent().getStringExtra(IConstant.JADDRESS);
        if (jAddress != null) {
            Utils.FROM_ADDRESS = jAddress;
        }
        String sAddress = getIntent().getStringExtra(IConstant.SADDRESS);
        if (sAddress != null) {
            Utils.FROM_ADDRESS = sAddress;
        }

        view.setBackgroundResource(R.color.transparent);
        ChoiceTransModeFragment dialog = new ChoiceTransModeFragment();
        dialog.show(getSupportFragmentManager(), "ChoiceTransModeFragment");

    }

}
