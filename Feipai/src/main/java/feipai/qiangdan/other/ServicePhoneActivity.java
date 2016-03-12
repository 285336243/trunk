package feipai.qiangdan.other;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.core.ToastUtils;
import roboguice.inject.InjectView;

/**
 * Created by 51wanh on 2015/1/14.
 */
public class ServicePhoneActivity extends DialogFragmentActivity {

    @InjectView(R.id.return_back)
    private View returnBack;
    @InjectView(R.id.button_dial_tel)
    private View buttonDialTel;
    @InjectView(R.id.textview_number)
    private TextView textviewNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_telohpne_phone);
        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonDialTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = textviewNumber.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    ToastUtils.show(ServicePhoneActivity.this, "号码为空，请检查");
                    return;
                }
                //用intent启动拨打电话
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                startActivity(intent);
            }
        });
    }
}
