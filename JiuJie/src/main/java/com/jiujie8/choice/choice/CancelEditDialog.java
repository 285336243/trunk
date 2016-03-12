package com.jiujie8.choice.choice;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.jiujie8.choice.BlurDialog;
import com.jiujie8.choice.R;

/**
 * Created by wlanjie on 14/12/23.
 *
 * 取消的对话框
 */
public class CancelEditDialog extends BlurDialog {

    private CancelEditDialogContinueListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog mDialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        final Window mWindow = mDialog.getWindow();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.cancel_edit_dialog, null);
        mWindow.setContentView(view);
        View mGiveUpView = view.findViewById(R.id.cancel_edit_dialog_giveup);
        mGiveUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onGiveUpListener();
                    dismiss();
                } else {
                    getActivity().finish();
                }
            }
        });
        View mContinueView = view.findViewById(R.id.cancel_edit_dialog_continue);
        mContinueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onContinueListener();
                    dismiss();
                }
            }
        });
        return mDialog;
    }

    /**
     * 设置继续按钮的监听
     * @param l
     */
    public void setContinueListener(CancelEditDialogContinueListener l) {
        mListener = l;
    }

    /**
     * 按钮的监听接口
     */
    public interface CancelEditDialogContinueListener {

        public void onContinueListener();

        /**
         * 继续的监听
         */
        public void onGiveUpListener();
    }
}
