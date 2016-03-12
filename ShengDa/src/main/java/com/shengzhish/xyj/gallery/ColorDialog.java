package com.shengzhish.xyj.gallery;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.Response;
import com.shengzhish.xyj.core.ProgressDialogTask;
import com.shengzhish.xyj.http.GsonRequest;
import com.shengzhish.xyj.http.HttpUtils;
import com.shengzhish.xyj.util.MyEditText;
import com.shengzhish.xyj.util.TextLengthWather;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlanjie on 14-6-13.
 */
public class ColorDialog extends Dialog {

    private String uri;

    private String id;

    private Color color;

    private EditText editText;

    private RadioGroup radioGroup;
    private Context context;
    private final Map<Integer, Color> colors = new HashMap<Integer, Color>();

    public ColorDialog(Context context, final String uri) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.uri = uri;
        this.context = context;
        Window window = getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        colors.put(R.id.color_white, new Color(255, 255, 255));
        colors.put(R.id.color_yellow, new Color(248, 231, 28));
        colors.put(R.id.color_red, new Color(255, 0, 31));
        colors.put(R.id.color_blue, new Color(76, 159, 255));
        colors.put(R.id.color_cyan, new Color(97, 255, 220));
        colors.put(R.id.color_orange, new Color(255, 159, 0));
        colors.put(R.id.color_green, new Color(172, 255, 89));
    }

    public ColorDialog(Context context, int theme) {
        super(context, theme);
    }

    protected ColorDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        radioGroup = null;
        editText = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (editText != null) {
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                    showCancelDialog();
                } else {
                    dismiss();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        editText = (MyEditText) view.findViewById(R.id.comment_edit);
        editText.addTextChangedListener(new TextLengthWather(context, 70, editText));

        view.findViewById(R.id.comment_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = editText.getText().toString();
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                new ProgressDialogTask<Response>(getContext()) {
                    @Override
                    protected Response run(Object data) throws Exception {
                        return (Response) HttpUtils.doRequest(createPost(message)).result;
                    }

                    @Override
                    protected void onSuccess(Response response) throws Exception {
                        super.onSuccess(response);
                        if (response != null) {
                            if (response.getResponseCode() == 0) {
                                dismiss();
                            } else {
                                Toast.makeText(getContext(), response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }.start("正在发表评论");
            }
        });
        view.findViewById(R.id.comment_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                    showCancelDialog();
                } else {
                    dismiss();
                }
            }
        });
        radioGroup = (RadioGroup) view.findViewById(R.id.comment_color_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                color = colors.get(checkedId);
            }
        });
    }

    private void showCancelDialog() {
        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.edit_cancel_dialog);
        dialog.findViewById(R.id.edit_cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dismiss();
            }
        });
        dialog.findViewById(R.id.edit_continue_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (!dialog.isShowing())
            dialog.show();
    }

    private Request<Response> createPost(String message) {
        GsonRequest<Response> request = new GsonRequest<Response>(Request.Method.POST, String.format(uri, id));
        request.setClazz(Response.class);
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("id", id);
        bodys.put("msg", message);
        if (color == null) {
            color = colors.get(R.id.color_white);
        }
        bodys.put("r", color.getR() + "");
        bodys.put("g", color.getG() + "");
        bodys.put("b", color.getB() + "");
        request.setHeaders(bodys);
        return request;
    }
}
