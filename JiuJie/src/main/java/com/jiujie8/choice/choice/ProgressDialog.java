package com.jiujie8.choice.choice;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.jiujie8.choice.BlurDialog;
import com.jiujie8.choice.R;

/**
 * Created by wlanjie on 14/12/24.
 */
public class ProgressDialog extends BlurDialog {

    private ProgressBar mBar;

    private Dialog mDialog;

    private int i = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        return mDialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Window mWindow = mDialog.getWindow();
        final View mView = getActivity().getLayoutInflater().inflate(R.layout.progress_dialog, null);
        mWindow.setContentView(mView);
        mBar = (ProgressBar) mView.findViewById(R.id.progress_dialog_progress);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        System.out.println("progressbar = " + mBar);
    }

    public void setProgressTotalValue(final long totalValue, final long current) {
        System.out.println("total = " + totalValue+ " current = " + current +" bar = " + mBar);
        if (mBar != null) {
            mBar.setMax(100);
            mBar.setProgress(i+=3);
//            mBar.setMax((int) totalValue);
//            mBar.setProgress((int) current);
//            mBar.setSecondaryProgress((int) current);
        }
    }
}
