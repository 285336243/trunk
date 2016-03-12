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
public class SetShareDialog extends BlurDialog {

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
        mDialog.setCanceledOnTouchOutside(false);
        final Window mWindow = mDialog.getWindow();
        final View view = getActivity().getLayoutInflater().inflate(R.layout.share_dialog_layout, null);
        mWindow.setContentView(view);
        View sinaWeiBo = view.findViewById(R.id.share_sina);
        sinaWeiBo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().finish();
                dialogListener.shareSina();
                ToastUtils.show(getActivity(), "分享到微博");
                dismiss();
            }
        });
        View weixinFriends = view.findViewById(R.id.share_weixin_friends);
        weixinFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.show(getActivity(),"分享到朋友圈");
                dialogListener.shareWeiXinFriends();
                dismiss();
            }
        });
        View goodFrinds = view.findViewById(R.id.share_weixin_good_frinds);
        goodFrinds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.show(getActivity(),"分享到微信好友");
                dialogListener.shareWeiXinGoodFriend();
                dismiss();
            }
        });
        return mDialog;
    }
    public interface DialogListener{
        public abstract void shareSina();
        public abstract void shareWeiXinFriends();
        public abstract void shareWeiXinGoodFriend();
    }

}
