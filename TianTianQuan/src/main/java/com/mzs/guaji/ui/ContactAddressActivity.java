package com.mzs.guaji.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.ContactAddress;
import com.mzs.guaji.util.GiveUpEditingDialog;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlanjie on 14-2-13.
 */
public class ContactAddressActivity extends GuaJiActivity {

    private Context context = ContactAddressActivity.this;
    private LinearLayout mBackLayout;
    private LinearLayout mSaveLayout;
    private EditText mContactNameEdit;
    private EditText mContactAddressEdit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_address_layout);
        mBackLayout = (LinearLayout) findViewById(R.id.contact_address_back);
        mBackLayout.setOnClickListener(mBackClickListener);
        mSaveLayout = (LinearLayout) findViewById(R.id.contact_address_save);
        mSaveLayout.setOnClickListener(mSaveClickListener);
        mContactNameEdit = (EditText) findViewById(R.id.contact_name);
        mContactAddressEdit = (EditText) findViewById(R.id.contact_address);
        mApi.requestGetData(getShippingRequest(), ContactAddress.class, new Response.Listener<ContactAddress>() {
            @Override
            public void onResponse(ContactAddress response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        mContactNameEdit.setText(response.getName());
                        mContactAddressEdit.setText(response.getAddress());
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }, null);
    }

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GiveUpEditingDialog.showGiveUpEditingDialog(ContactAddressActivity.this);
        }
    };

    View.OnClickListener mSaveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = mContactNameEdit.getText().toString();
            String address = mContactAddressEdit.getText().toString();
            if("".equals(name)) {
                ToastUtil.showToast(context, "姓名不能为空");
                return;
            }
            if("".equals(address)) {
                ToastUtil.showToast(context, "地址不能为空");
                return;
            }
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("name", name);
            headers.put("address", address);
            TipsUtil.showTipDialog(context, "正在提交数据");
            mApi.requestPostData(getSaveRequest(), ContactAddress.class, headers, new Response.Listener<ContactAddress>() {
                @Override
                public void onResponse(ContactAddress response) {
                    if(response != null) {
                        if(response.getResponseCode() == 0) {
                            ToastUtil.showToast(context, "提交成功");
                            TipsUtil.dismissDialog();
                            finish();
                        }else {
                            ToastUtil.showToast(context, response.getResponseMessage());
                        }
                    }
                }
            }, null);
        }
    };

    private String getSaveRequest() {
        return DOMAIN + "shipping/address_save.json";
    }

    private String getShippingRequest() {
        return DOMAIN + "shipping/address_get.json";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        GiveUpEditingDialog.showGiveUpEditingDialog(this);
    }
}