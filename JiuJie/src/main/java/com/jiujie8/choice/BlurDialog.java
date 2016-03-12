package com.jiujie8.choice;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.jiujie8.choice.util.IConstant;

/**
 * Created by wlanjie on 14/12/15.
 * 高斯模糊的Dialog
 */
public class BlurDialog extends DialogFragment {

    protected BlurDialogEngine mBlurEngine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlurEngine = new BlurDialogEngine(getActivity());
        mBlurEngine.debug(IConstant.DEBUG);
        mBlurEngine.setBlurRadius(6);
        mBlurEngine.setDownScaleFactor(6);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBlurEngine.onResume(getRetainInstance());
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mBlurEngine.onDismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBlurEngine.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    /**
     * Set the down scale factor used by the {@link fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine}
     *
     * @param factor down scaled factor used to reduce the size of the source image.
     *               Range :  ]0,infinity)
     */
    public void setDownScaleFactor(float factor) {
        if (factor > 0) {
            mBlurEngine.setDownScaleFactor(factor);
        }
    }

    /**
     * Set the blur radius used by the {@link fr.tvbarthel.lib.blurdialogfragment.BlurDialogEngine}
     *
     * @param radius down scaled factor used to reduce the size of the source image.
     *               Range :  [1,infinity)
     */
    public void setBlurRadius(int radius) {
        if (radius > 0) {
            mBlurEngine.setBlurRadius(radius);
        }
    }
}
