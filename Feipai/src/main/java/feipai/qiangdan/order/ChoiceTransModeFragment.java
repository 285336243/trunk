package feipai.qiangdan.order;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.Display;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.RadioGroup;

import feipai.qiangdan.R;
import feipai.qiangdan.home.HomeActivity;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.Utils;
import roboguice.inject.InjectView;

/**
 * Created by 51wanh on 2015/1/17.
 */
public class ChoiceTransModeFragment extends DialogFragment {

    //    @InjectView(R.id.next_button)
    private Button nextButton;
    //    @InjectView(R.id.choice_group)
    private RadioGroup choiceGroup;

    public static ChoiceTransModeFragment newInstance() {
        ChoiceTransModeFragment f = new ChoiceTransModeFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getDialog() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        View root = inflater.inflate(R.layout.layout_choicetransmodeframent, container, false);
        choiceGroup = (RadioGroup) root.findViewById(R.id.choice_group);
        nextButton = (Button) root.findViewById(R.id.next_button);
        return root;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //返回的时候所依附得类也要关闭
    //按返回键时调用
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (null != getActivity()) {
            getActivity().finish();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // change dialog width
        if (getDialog() != null) {

            int fullWidth = getDialog().getWindow().getAttributes().width;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                fullWidth = size.x;
            } else {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                fullWidth = display.getWidth();
            }

            final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                    .getDisplayMetrics());

            int w = fullWidth - padding;
            int h = getDialog().getWindow().getAttributes().height;
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().getWindow().setLayout(w, h);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.TRANS_MODE = "公交";
        choiceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    //电动车
                    case R.id.btn_0:
                        Utils.TRANS_MODE = "电动车";
                        break;
                    //公交
                    case R.id.btn_1:
                        Utils.TRANS_MODE = "公交";
                        break;
                    //汽车
                    case R.id.btn_2:
                        Utils.TRANS_MODE = "汽车";
                        break;
                    default:
                        break;
                }

            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这样不行，不能到达地图页面
//                startActivity(new Intent(getActivity(), HomeActivity.class).putExtra(IConstant.MAP_GUID,1));
                sendLoginBroadcast();
                getActivity().finish();
            }
        });
    }

    private void sendLoginBroadcast() {
        Intent eIntent = new Intent();
//        Log.v("person", "发送广播");
        eIntent.setAction(IConstant.MAP_BROADCAST);
        getActivity().sendBroadcast(eIntent);

    }


}
