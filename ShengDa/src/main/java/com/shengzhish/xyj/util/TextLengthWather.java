package com.shengzhish.xyj.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;


/**
 * 监听输入内容是否超出设定长度，并设置光标位置
 */
public class TextLengthWather implements TextWatcher {

    private int maxLength;
    private EditText editText;
    private Context context;
    private CharSequence temp;
    private int editStart;
    private int editEnd;

    public TextLengthWather(Context context, int maxLength, EditText editText) {
        this.maxLength = maxLength;
        this.editText = editText;
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        temp = s;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String content = editText.getText().toString().trim();
        editStart = editText.getSelectionStart();
        editEnd = editText.getSelectionEnd();
//        mTextView.setText("您输入了" + temp.length() + "个字符");
        if (temp.length() > maxLength) {
            Toast.makeText(context,
                    "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT)
                    .show();
            s.delete(editStart - 1, editEnd);
            int tempSelection = editStart;
            editText.setText(s);
            editText.setSelection(tempSelection);

        }
    }
}
