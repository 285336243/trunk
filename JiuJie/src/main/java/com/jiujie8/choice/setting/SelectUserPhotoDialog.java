package com.jiujie8.choice.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.jiujie8.choice.BlurDialog;
import com.jiujie8.choice.R;
import com.jiujie8.choice.core.ToastUtils;

/**
 * 选择用户头像
 */
public class SelectUserPhotoDialog extends BlurDialog {

    private DialogListener dialogListener;

    public void setDialogListener(DialogListener listener){
        dialogListener=listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlurEngine.setBlurRadius(4);
        mBlurEngine.setDownScaleFactor(4);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog mDialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
//        mDialog.setCanceledOnTouchOutside(false);
        final Window mWindow = mDialog.getWindow();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.select_userphoto_dialog, null);
        mWindow.setContentView(view);
        View mGiveUpView = view.findViewById(R.id.from_take_picture);
        mGiveUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().finish();
                dialogListener.takePicture();
//                ToastUtils.show(getActivity(),"拍照");
                dismiss();
            }
        });
        View mContinueView = view.findViewById(R.id.from_photo_album);
        mContinueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtils.show(getActivity(),"相册选择");
                dialogListener.photoAlbum();
                dismiss();
            }
        });
        return mDialog;
    }
    public interface DialogListener{
        public abstract void takePicture();
        public abstract void photoAlbum();
    }

}
