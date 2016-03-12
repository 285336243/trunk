package com.socialtv.personcenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragment;
import com.socialtv.core.Intents;
import com.socialtv.core.Utils;
import com.socialtv.feed.SelfFeedActivity;
import com.socialtv.home.WebViewActivity;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.ExternalAccounts;
import com.socialtv.personcenter.entity.UserResponse;
import com.socialtv.publicentity.User;
import com.socialtv.publicentity.UserBadge;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;

import java.util.List;

import roboguice.inject.InjectView;

/**
 * person center pager
 */
public class PersionCenterFragment extends DialogFragment {

    private final static int REQUEST_CODE = 0;

    @Inject
    private PersonalUrlService service;

    @InjectView(R.id.person_nikename)
    private TextView personNickName;

    @InjectView(R.id.person_photo)
    private ImageView userHeadPhoto;

    @InjectView(R.id.gender)
    private ImageView genderImageView;

    @InjectView(R.id.person_center_self_score_text)
    private TextView scoreText;

    @InjectView(R.id.person_center_self_score)
    private View scoreView;

    @InjectView(R.id.person_signature)
    private TextView personSignature;

    @InjectView(R.id.attention_count)
    private TextView attentionCount;

    @InjectView(R.id.fensi_count)
    private TextView fansCount;

    @InjectView(R.id.person_infor_edit)
    private View personInforEdit;

    @InjectView(R.id.private_message_layout)
    private View privateMessageLayout;

    @InjectView(R.id.setting_layout)
    private View settingLayout;

    @InjectView(R.id.join_group)
    private View joinGroup;

    @InjectView(R.id.person_center_photo_album)
    private View photoAlbumView;

    @InjectView(R.id.person_center_follow)
    private View followView;

    @InjectView(R.id.person_center_fans)
    private View fansView;

    @InjectView(R.id.person_center_self_feed)
    private View selfFeedView;

    @InjectView(R.id.person_center_invite_friend)
    private View inviteFriendView;

    @InjectView(R.id.person_center_binding_setting)
    private View bindingSettingView;

    @InjectView(R.id.person_center_modify_password)
    private View modifyPasswordView;

    @InjectView(R.id.person_badge_layout)
    private LinearLayout badgesLayout;

    private Bundle bundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.person_center_layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPersonInformation();
        personInforEdit.setOnClickListener(clickListens);
        privateMessageLayout.setOnClickListener(clickListens);
        settingLayout.setOnClickListener(clickListens);
        joinGroup.setOnClickListener(clickListens);
        scoreView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intents(getActivity(), WebViewActivity.class).add(IConstant.URL, IConstant.SCORE_URL).toIntent());
            }
        });
        modifyPasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intents(getActivity(), ModifyPasswordActivity.class).toIntent());
            }
        });
    }

    private void getPersonInformation() {
        new AbstractRoboAsyncTask<UserResponse>(getActivity()) {
            @Override
            protected UserResponse run(Object data) throws Exception {
                return (UserResponse) HttpUtils.doRequest(service.createPersonRequest()).result;
            }

            @Override
            protected void onSuccess(UserResponse userdata) throws Exception {
                super.onSuccess(userdata);
                if (userdata != null) {
                    if (userdata.getResponseCode() == 0) {
                        User user = userdata.getUser();
                        if (user != null) {
                            LoginUtil.saveUserMobile(getActivity(), user.getMobile());
                            bundle.clear();
                            bundle.putString(IConstant.PERSON_HEAD, user.getAvatar());
                            bundle.putString(IConstant.PERSON_GENDER, user.getGender());
                            bundle.putString(IConstant.PERSON_NIKCNAME, user.getNickname());
                            bundle.putString(IConstant.PERSON_SIGNATION, user.getSignature());
                            personNickName.setText(user.getNickname());
                            scoreText.setText(user.getScore() + " 番茄币");
                            attentionCount.setText(user.getFollowCnt() + "");
                            fansCount.setText(user.getFansCnt() + "");

                            if (user.getBadges() != null && !user.getBadges().isEmpty()) {
                                badgesLayout.removeAllViews();
                                badgesLayout.setVisibility(View.VISIBLE);
                                for (UserBadge badge : user.getBadges()) {
                                    if (badge != null) {
                                        ImageView imageView = new ImageView(getActivity());
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(getActivity(), 16), Utils.dip2px(getActivity(), 16));
                                        params.gravity = Gravity.CENTER_VERTICAL;
                                        imageView.setLayoutParams(params);
                                        ImageLoader.getInstance().displayImage(badge.getImg(), imageView, ImageUtils.imageLoader(getActivity(), 0));
                                        badgesLayout.addView(imageView);
                                    }
                                }
                            } else {
                                badgesLayout.setVisibility(View.GONE);
                            }
                            if (!TextUtils.isEmpty(user.getSignature())) {
                                personSignature.setText(user.getSignature());
                            } else {
                                personSignature.setText("");
                            }
                            if (!TextUtils.isEmpty(user.getAvatar())) {
                                ImageLoader.getInstance().displayImage(user.getAvatar(), userHeadPhoto, ImageUtils.avatarImageLoader());
                            }
                            if (!TextUtils.isEmpty(user.getGender())) {
                                genderImageView.setVisibility(View.VISIBLE);
                                if ("m".equals(user.getGender()))
                                    genderImageView.setImageResource(R.drawable.icon_man_tomato);
                                else
                                    genderImageView.setImageResource(R.drawable.icon_lady_tomato);
                            } else {
                                genderImageView.setVisibility(View.GONE);
                            }
                            setSelfFeedClickListener(user.getUserId());
                            setPhotoAlbumClickListener(user.getUserId());
                            setFollowClickListener(user.getUserId());
                            setFansClickListener(user.getUserId());
                            setInviteFriendClickListener();
                            setBindingSettingClickListener(userdata.getExternalAccount());
                        }
                    }
                }
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode != Activity.RESULT_CANCELED) {
            getPersonInformation();
        }
    }

    private void setFansClickListener(final String userId) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUsable())
                    startActivityForResult(new Intents(getActivity(), FollowActivity.class).add(IConstant.USER_ID, userId).add(IConstant.TAG, "fans").toIntent(), REQUEST_CODE);
            }
        };
        fansView.setOnClickListener(listener);
    }

    private void setFollowClickListener(final String userId) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUsable())
                    startActivityForResult(new Intents(getActivity(), FollowActivity.class).add(IConstant.USER_ID, userId).add(IConstant.TAG, "follow").toIntent(), REQUEST_CODE);
            }
        };
        followView.setOnClickListener(listener);
    }

    private void setPhotoAlbumClickListener(final String userId) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUsable())
                    startActivity(new Intents(getActivity(), PhotoAlbumActivity.class).add(IConstant.USER_ID, userId).toIntent());
            }
        };
        photoAlbumView.setOnClickListener(listener);
    }

    private void setSelfFeedClickListener(final String userId) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intents(getActivity(), SelfFeedActivity.class).add(IConstant.USER_ID, userId).toIntent());
            }
        };
        selfFeedView.setOnClickListener(listener);
    }

    private void setInviteFriendClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intents(getActivity(), InviteFriendActivity.class).toIntent(), REQUEST_CODE);
            }
        };
        inviteFriendView.setOnClickListener(listener);
    }

    private void setBindingSettingClickListener(final List<ExternalAccounts> accounts) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BindingSettingActivity.class);
                if (accounts != null && !accounts.isEmpty()) {
                    for (ExternalAccounts account : accounts) {
                        if (account != null) {
                            if ("SINA".equals(account.getType())) {
                                intent.putExtra(IConstant.SINA, account);
                            } else if ("QQ".equals(account.getType())) {
                                intent.putExtra(IConstant.QQ, account);
                            }
                        }
                    }
                }
                startActivity(intent);
            }
        };
        bindingSettingView.setOnClickListener(listener);
    }

    private View.OnClickListener clickListens = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.person_infor_edit:
                    if (bundle != null)
                    startActivityForResult(new Intent(getActivity(), PersonInforEdit.class).putExtras(bundle), REQUEST_CODE);
                    break;
                case R.id.private_message_layout:
                    startActivity(new Intent(getActivity(), PrivateAddress.class));
                    break;
                case R.id.setting_layout:
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                    break;
                case R.id.join_group:
                    startActivity(new Intent(getActivity(), JoinGroupActivity.class));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getPersonInformation();
        }
    }
}
