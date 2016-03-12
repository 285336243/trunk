package com.jiujie8.choice.home;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.jiujie8.choice.BlurDialog;
import com.jiujie8.choice.R;

/**
 * Created by wlanjie on 14/12/23.
 * 分享的dialog
 */
public class ShareDialog extends BlurDialog {

    private OnShareListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog moreDialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        final Window mWindow = moreDialog.getWindow();
        View view = getActivity().getLayoutInflater().inflate(R.layout.share_dialog, null);
        mWindow.setContentView(view);
        View mSinaView = view.findViewById(R.id.share_dialog_sina);
        View mWeixinTimeline = view.findViewById(R.id.share_dialog_weixin_timeline);
        View mWeixinSession = view.findViewById(R.id.share_dialog_weixin_session);

        mSinaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onShareSinaListener();
                }
            }
        });

        mWeixinTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onShareWeixinTimelineListener();
                }
            }
        });

        mWeixinSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onShareWeixinSessionListener();
                }
            }
        });
        return moreDialog;
    }

    public void setShareListener(OnShareListener l) {
        mListener = l;
    }

    public interface OnShareListener {

        public void onShareWeixinTimelineListener();

        public void onShareWeixinSessionListener();

        public void onShareSinaListener();
    }
}
