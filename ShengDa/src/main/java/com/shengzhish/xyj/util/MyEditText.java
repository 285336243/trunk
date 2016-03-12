package com.shengzhish.xyj.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 禁止粘贴内容输入的自定义EditText
 */
public class MyEditText extends EditText {
    /**
     * 粘贴id
     */
    private static final int ID_PASTE = android.R.id.paste;

    public MyEditText(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == ID_PASTE) {
            Toast toast = Toast.makeText(getContext(), "不能输入粘贴内容，请手输！", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            return false;
        }
        return super.onTextContextMenuItem(id);
    }

}
