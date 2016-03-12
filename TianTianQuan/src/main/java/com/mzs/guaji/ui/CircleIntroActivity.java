package com.mzs.guaji.ui;

import com.mzs.guaji.R;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 圈子简介 activity
 * @author lenovo
 *
 */
public class CircleIntroActivity extends GuaJiActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.circle_intro_layout);
		final LinearLayout mBackLayout = (LinearLayout) findViewById(R.id.circle_intro_back);
		mBackLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		RelativeLayout mMasterLayout = (RelativeLayout) findViewById(R.id.circle_intro_master_layout);
		mMasterLayout.setOnClickListener(mMasterListener);
		
		RelativeLayout mMemberLayout = (RelativeLayout) findViewById(R.id.circle_intro_member_layout);
		mMemberLayout.setOnClickListener(mMemberListener);
	}
	
	/**
	 * 领主点击事件
	 */
	View.OnClickListener mMasterListener = new  View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
		}
	};
	
	/**
	 * 成员点击事件
	 */
	View.OnClickListener mMemberListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
		}
	};
}
