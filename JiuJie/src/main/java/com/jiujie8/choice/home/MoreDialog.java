package com.jiujie8.choice.home;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.jiujie8.choice.BlurDialog;
import com.jiujie8.choice.R;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.core.AbstractRoboAsyncTask;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.util.IConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlanjie on 14/12/23.
 * 更多的dialog
 */
public class MoreDialog extends BlurDialog {

    private boolean isSelf = false;

    private String choiceId = "";

    private OnMoreCallBack mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            isSelf = args.getBoolean(IConstant.IS_SELF);
            choiceId = String.valueOf(args.getLong(IConstant.CHOICE_ID));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog moreDialog = new Dialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        final Window mWindow = moreDialog.getWindow();
        View view = getActivity().getLayoutInflater().inflate(R.layout.more_dialog, null);
        mWindow.setContentView(view);
        final TextView mTextView = (TextView) view.findViewById(R.id.more_dialog_content);
        final View mLineView = view.findViewById(R.id.more_dialog_line);
        final View mNotReadView = view.findViewById(R.id.more_dialog_not_read);
        if (isSelf) {
            mTextView.setText(getResources().getString(R.string.delete_content));
//            mLineView.setVisibility(View.GONE);
            mNotReadView.setVisibility(View.GONE);
        } else {
            mTextView.setText(getResources().getString(R.string.report_content));
            mLineView.setVisibility(View.VISIBLE);
            mNotReadView.setVisibility(View.VISIBLE);
        }
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSelf) {
                    postOnDeleteToServer();
                } else {
                    postOnReportToServer();
                }
            }
        });

        mNotReadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallback != null) {
                    dismiss();
                    mCallback.onNotReadListener();
                }
            }
        });
        return moreDialog;
    }

    /**
     * 举报请求
     */
    private void postOnReportToServer() {
        new AbstractRoboAsyncTask<Response>(getActivity()) {

            @Override
            protected Response run(Object data) throws Exception {
                final Map<String, String> mBodys = new HashMap<String, String>();
                mBodys.put(IConstant.CHOICE_ID, choiceId);
                mBodys.put(IConstant.REPORT_MESSAGE, "");
                return (Response) HttpUtils.doRequest(HomeServices.createReportPostRequest(mBodys)).result;
            }

            @Override
            protected void onSuccessCallback(Response response) {
                dismiss();
            }
        }.execute();
    }

    /**
     * 删除的请求
     */
    private void postOnDeleteToServer() {
        new AbstractRoboAsyncTask<Response>(getActivity()) {

            @Override
            protected Response run(Object data) throws Exception {
                final Map<String, String> map = new HashMap<String, String>();
                map.put(IConstant.CHOICE_ID, choiceId);
                return (Response) HttpUtils.doRequest(HomeServices.createDeleteChoiceRequest(map)).result;
            }

            @Override
            protected void onSuccessCallback(Response response) {
                if (mCallback != null) {
                    mCallback.onDeleteSuccess();
                }
                dismiss();
            }
        }.execute();
    }

    public void setOnDeleteCallback(OnMoreCallBack callback) {
        mCallback = callback;
    }

    public interface OnMoreCallBack {
        /**
         * 删除成功的回调
         */
        public void onDeleteSuccess();

        /**
         * 不在看到该内容的回调
         */
        public void onNotReadListener();
    }
}
