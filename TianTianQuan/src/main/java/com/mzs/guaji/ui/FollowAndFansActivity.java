package com.mzs.guaji.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mzs.guaji.R;
import com.mzs.guaji.fragment.PersonalFansFragment;
import com.mzs.guaji.fragment.PersonalFollowFragment;

public class FollowAndFansActivity extends GuaJiActivity {
	
	private TextView mTitleText;
	private RadioGroup mRadioGroup;
	private PersonalFollowFragment mAttentionFragment;
	public PersonalFansFragment mFansFragment;
	private FragmentManager mFragmentManager;
	public RadioButton mFollowButton;
	public RadioButton mFansButton;
    private Fragment mLastShowFragment;
    private boolean isSelf;
    private long userId;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.follow_fans_layout);
        isSelf = getIntent().getBooleanExtra("isSelf", true);
        userId = getIntent().getLongExtra("userId", -1);
		LinearLayout mBackLayout = (LinearLayout) findViewById(R.id.follow_fans_title_back);
		mBackLayout.setOnClickListener(mBackClickListener);
		mTitleText = (TextView) findViewById(R.id.attention_fans_title);
		mRadioGroup = (RadioGroup) findViewById(R.id.attion_fans_group);
		mRadioGroup.setOnCheckedChangeListener(mChangeListener);
		mFragmentManager = getSupportFragmentManager();
		mFollowButton = (RadioButton) findViewById(R.id.attention_radiobutton);
		mFansButton = (RadioButton) findViewById(R.id.fans_radiobutton);
        FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
        if(mAttentionFragment == null) {
            mAttentionFragment = PersonalFollowFragment.newInstance(isSelf, userId);
            mTransaction.add(R.id.attentio_fans_content, mAttentionFragment);
            mLastShowFragment = new PersonalFansFragment();
            mFansButton.setText("粉丝");
        }
        addFragmentToStack(mAttentionFragment, mTransaction);
	}
	
	RadioGroup.OnCheckedChangeListener mChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.attention_radiobutton:
				FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
				mTitleText.setText(R.string.attention);
				if(mAttentionFragment == null) {
                    mAttentionFragment = PersonalFollowFragment.newInstance(isSelf, userId);
                    mTransaction.add(R.id.attentio_fans_content, mAttentionFragment);
                }
                addFragmentToStack(mAttentionFragment, mTransaction);
				break;

			case R.id.fans_radiobutton:
				FragmentTransaction mFansTranscation = mFragmentManager.beginTransaction();
				mTitleText.setText(R.string.fans);
				if(mFansFragment == null) {
                    mFansFragment = PersonalFansFragment.newInstance(isSelf, userId);
                    mFansTranscation.add(R.id.attentio_fans_content, mFansFragment);
                }
                addFragmentToStack(mFansFragment, mFansTranscation);
				break;
			}
		}
	};

    private void addFragmentToStack(Fragment mFragment, FragmentTransaction mTransaction) {
        if(mLastShowFragment != null) {
            mTransaction.hide(mLastShowFragment);
            mTransaction.show(mFragment);
            mLastShowFragment = mFragment;
            mTransaction.commitAllowingStateLoss();
        }
    }

}
