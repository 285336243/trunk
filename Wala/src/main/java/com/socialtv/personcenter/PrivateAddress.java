package com.socialtv.personcenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.GsonRequest;
import com.socialtv.http.HttpUtils;
import com.socialtv.http.MultipartRequest;
import com.socialtv.shop.entity.GetShipResponse;
import com.socialtv.shop.entity.Shipping;

import roboguice.inject.InjectView;

/**
 * 修改个人联系地址
 */
public class PrivateAddress extends DialogFragmentActivity {

    private static final String INFORMATION_SUBMIT_URL = "user/shipping_address.json";
    @InjectView(R.id.user_name)
    private EditText userName;

    @InjectView(R.id.user_telphone)
    private EditText userTelphone;

    @InjectView(R.id.user_address)
    private EditText userAddress;

    @InjectView(R.id.submit_button)
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_address_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setTitle("私人信息");
        setSupportProgressBarIndeterminateVisibility(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitShippingInfor();
            }
        });
        getPersonInfor();
    }

    private void getPersonInfor() {
        new AbstractRoboAsyncTask<GetShipResponse>(this){
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected GetShipResponse run(Object data) throws Exception {
                GsonRequest<GetShipResponse> request = new GsonRequest<GetShipResponse>(Request.Method.GET, INFORMATION_SUBMIT_URL);
                request.setClazz(GetShipResponse.class);
                return (GetShipResponse) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(GetShipResponse response) throws Exception {
                super.onSuccess(response);
                if (response.getResponseCode() == 0) {
                    if ( response.getShipping() != null) {
                        Shipping data = response.getShipping();
                        userName.setText(data.getName());
                        userTelphone.setText(data.getMobile());
                        userAddress.setText(data.getAddress());
                    }
                } else {
                    ToastUtils.show(PrivateAddress.this, response.getResponseMessage());
                }
            }
        }.execute();
    }

    private void submitShippingInfor() {

        final String subName = userName.getText().toString().trim();
        final String subPhone = userTelphone.getText().toString().trim();
        final String subAddress = userAddress.getText().toString().trim();
        if (TextUtils.isEmpty(subName)) {
            ToastUtils.show(PrivateAddress.this, "姓名不能为空");
            return;
        } else if (TextUtils.isEmpty(subPhone)) {
            ToastUtils.show(PrivateAddress.this, "联系电话不能为空");
            return;
        } else if (TextUtils.isEmpty(subAddress)) {
            ToastUtils.show(PrivateAddress.this, "邮寄地址不能为空");
            return;
        }
        if (subPhone.length() != 11) {
            ToastUtils.show(PrivateAddress.this, "请输入正确的手机号码");
            return;
        }


        new ProgressDialogTask<GetShipResponse>(this) {

            @Override
            protected GetShipResponse run(Object data) throws Exception {
                setSupportProgressBarIndeterminateVisibility(true);
                MultipartRequest<GetShipResponse> request = new MultipartRequest<GetShipResponse>(INFORMATION_SUBMIT_URL);
                request.setClazz(GetShipResponse.class);
                request.addMultipartStringEntity("name", subName);
                request.addMultipartStringEntity("mobile", subPhone);
                request.addMultipartStringEntity("address", subAddress);
                return (GetShipResponse) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(GetShipResponse response) throws Exception {
                super.onSuccess(response);
                setSupportProgressBarIndeterminateVisibility(false);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        ToastUtils.show(PrivateAddress.this, "提交成功");
                        finish();
                    } else {
                        ToastUtils.show(PrivateAddress.this, response.getResponseMessage());

                    }

                }

            }
        }.start("正在提交");
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
}