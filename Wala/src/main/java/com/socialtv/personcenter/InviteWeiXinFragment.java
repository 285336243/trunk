package com.socialtv.personcenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragment;
import com.socialtv.http.HttpUtils;
import com.socialtv.publicentity.MessageResource;
import com.socialtv.share.weixin.ShareWeiXin;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-8-14.
 */
public class InviteWeiXinFragment extends DialogFragment {

    @InjectView(R.id.invite_weixin_friend)
    private View friendView;

    @InjectView(R.id.invite_weixin_friend_circle)
    private View friendCircleView;

    @Inject
    private ShareWeiXin weiXin;

    @Inject
    private UserService service;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.invite_weixin, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AbstractRoboAsyncTask<MessageResource>(getActivity()){
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected MessageResource run(Object data) throws Exception {
                return (MessageResource) HttpUtils.doRequest(service.createWeixinMessage()).result;
            }

            @Override
            protected void onSuccess(final MessageResource messageResource) throws Exception {
                super.onSuccess(messageResource);
                if (messageResource != null) {
                    friendView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            weiXin.shareWeiXinText(messageResource.getMessage());
                        }
                    });

                    friendCircleView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            weiXin.shareWeiXinCircleText(messageResource.getMessage());
                        }
                    });
                }
            }
        }.execute();
    }
}
