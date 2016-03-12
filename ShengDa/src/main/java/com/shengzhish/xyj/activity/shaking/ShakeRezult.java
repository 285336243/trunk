package com.shengzhish.xyj.activity.shaking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.DialogFragmentActivity;
import com.shengzhish.xyj.util.IConstant;

import roboguice.inject.InjectView;

/**
 * 摇一摇结果
 */
public class ShakeRezult extends DialogFragmentActivity {


    @InjectView(R.id.back_imageview)
    private View back;
    @InjectView(R.id.show_rezult)
    private TextView showRezult;
    @InjectView(R.id.once_more_button)
    private Button onceMoreButton;


    private int isHit;
    private String congratulation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shake_rezult_layout);
        Bundle bundle = getIntent().getExtras();
        isHit = bundle.getInt(IConstant.ISHIT, 1000);
        congratulation = bundle.getString(IConstant.CONGRATULATION);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        showRezult.setText(congratulation);
        if (isHit == 0) {
            onceMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }
        if (isHit == 1) {
            onceMoreButton.setVisibility(View.INVISIBLE);
            showRezult.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(R.drawable.gesture_icon2));
        }
    }
}
