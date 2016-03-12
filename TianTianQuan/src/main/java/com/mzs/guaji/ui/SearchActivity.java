package com.mzs.guaji.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.CircleSearch;
import com.mzs.guaji.entity.ToPicSearch;
import com.mzs.guaji.entity.UserSearch;
import com.mzs.guaji.fragment.SearchCircleFragment;
import com.mzs.guaji.fragment.SearchLabelFragment;
import com.mzs.guaji.fragment.SearchToPicFragment;
import com.mzs.guaji.fragment.SearchUserFragment;

/**
 * 搜索 activity
 * @author lenovo
 *
 */
public class SearchActivity extends GuaJiActivity {

	private Context context = SearchActivity.this;
	private String circleSearch = DOMAIN + "group/search.json";
	private String topicSearch = DOMAIN+"topic/search.json";
	private String userSearch = DOMAIN +"user/search.json";
	private Map<String, String> headers;
	private TextView mCircleText;
	private TextView mToPicText;
	private TextView mUserText; 
	private FragmentManager mFragmentManager;
	public AutoCompleteTextView mAutoEdit;
	private boolean requestSucceed = false;
	public CircleSearch mCircleSearch;
	public UserSearch mUserSearch;
	public ToPicSearch mToPicSearch;
	private boolean isChage = true;
	/**
	 * 搜索用户 fragment
	 */
	public SearchUserFragment mSearchUserFragment;
	/**
	 * 搜索圈子 fragment
	 */
	public SearchCircleFragment mSearchCircleFragment;
	/**
	 * 搜索话题 fragment
	 */
	public SearchToPicFragment mSearchToPicFragment;
	/**
	 * 搜索标签 fragment
	 */
	public SearchLabelFragment mSearchLabelFragment;
	
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.search_layout);
		mFragmentManager = getSupportFragmentManager();
		headers = new HashMap<String, String>();
		mCircleText = (TextView) findViewById(R.id.search_circle_text);
		mToPicText = (TextView) findViewById(R.id.search_toppic_text);
		mUserText = (TextView) findViewById(R.id.search_user_text);
		mAutoEdit = (AutoCompleteTextView) findViewById(R.id.search_auto_edit);
		searchRequested();
		ImageButton cancelButton = (ImageButton) findViewById(R.id.search_cancel_button);
		
		mSearchLabelFragment = (SearchLabelFragment) mFragmentManager.findFragmentById(R.id.search_label_fragment);
		mSearchCircleFragment = (SearchCircleFragment) mFragmentManager.findFragmentById(R.id.search_circle_fragment);
		mSearchToPicFragment = (SearchToPicFragment) mFragmentManager.findFragmentById(R.id.search_topic_fragment);
		mSearchUserFragment = (SearchUserFragment) mFragmentManager.findFragmentById(R.id.search_user_fragment);
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		final int color = getResources().getColor(R.color.search_tab_selector_color);
		final int norColor = getResources().getColor(R.color.search_tab_color);
		//圈子按钮点击事件
		mCircleText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCircleText.setTextColor(color);
				mToPicText.setTextColor(norColor);
				mUserText.setTextColor(norColor);
				mCircleText.setBackgroundResource(R.drawable.bdg_tabactive_center_tj);
				mToPicText.setBackgroundResource(R.drawable.bdg_tab_center_tj);
				mUserText.setBackgroundResource(R.drawable.bdg_tab_center_tj);
				
				if(requestSucceed) {
					if(mCircleSearch != null) {
						FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
						if(mSearchCircleFragment.isHidden()) {
							mTransaction.show(mSearchCircleFragment);
							mTransaction.hide(mSearchToPicFragment);
							mTransaction.hide(mSearchLabelFragment);
							mTransaction.hide(mSearchUserFragment);
						}else {
							mTransaction.hide(mSearchToPicFragment);
							mTransaction.hide(mSearchLabelFragment);
							mTransaction.hide(mSearchUserFragment);
						}
						mTransaction.commitAllowingStateLoss();
						mSearchCircleFragment.addGroupList(mCircleSearch);
					}
				}else {
					FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
					if(mSearchLabelFragment.isHidden()) {
						mTransaction.show(mSearchLabelFragment);
						mTransaction.hide(mSearchToPicFragment);
						mTransaction.hide(mSearchCircleFragment);
						mTransaction.hide(mSearchUserFragment);
					}else {
						mTransaction.hide(mSearchToPicFragment);
						mTransaction.hide(mSearchCircleFragment);
						mTransaction.hide(mSearchUserFragment);
					}
					mTransaction.commitAllowingStateLoss();
				}
			}
		});
		//话题按钮点击事件
		mToPicText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mToPicText.setTextColor(color);
				mCircleText.setTextColor(norColor);
				mUserText.setTextColor(norColor);
				mToPicText.setBackgroundResource(R.drawable.bdg_tabactive_center_tj);
				mCircleText.setBackgroundResource(R.drawable.bdg_tab_center_tj);
				mUserText.setBackgroundResource(R.drawable.bdg_tab_center_tj);
				
				FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
				if(mSearchToPicFragment.isHidden()) {
					mTransaction.show(mSearchToPicFragment);
					mTransaction.hide(mSearchCircleFragment);
					mTransaction.hide(mSearchLabelFragment);
					mTransaction.hide(mSearchUserFragment);
				}else {
					mTransaction.hide(mSearchCircleFragment);
					mTransaction.hide(mSearchLabelFragment);
					mTransaction.hide(mSearchUserFragment);
				}
				mSearchToPicFragment.addToPicList(mToPicSearch);
				mTransaction.commitAllowingStateLoss();
			}
		});
		//用户按钮点击事件
		mUserText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mUserText.setTextColor(color);
				mCircleText.setTextColor(norColor);
				mToPicText.setTextColor(norColor);
				mUserText.setBackgroundResource(R.drawable.bdg_tabactive_center_tj);
				mCircleText.setBackgroundResource(R.drawable.bdg_tab_center_tj);
				mToPicText.setBackgroundResource(R.drawable.bdg_tab_center_tj);
				
				FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
				if(mSearchUserFragment.isHidden()) {
					mTransaction.show(mSearchUserFragment);
					mTransaction.hide(mSearchCircleFragment);
					mTransaction.hide(mSearchLabelFragment);
					mTransaction.hide(mSearchToPicFragment);
				}else {
					mTransaction.hide(mSearchCircleFragment);
					mTransaction.hide(mSearchLabelFragment);
					mTransaction.hide(mSearchToPicFragment);
				}
				
				mSearchUserFragment.addUserList(mUserSearch);
				mTransaction.commitAllowingStateLoss();
			}
		});
	}

	/**
	 * 点击搜索按钮时的点击事件
	 */
	public void searchRequested() {
		mAutoEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH) {
					final String content = mAutoEdit.getText().toString();
					if(TextUtils.isEmpty(content)) {
						Toast.makeText(context, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
						return true;
					}
					InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					methodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					searchCircle(content);
					searchToPic(content);
					searchUser(content);
				}
				return false;
			}
		});
	}
	
	/**
	 * 搜索圈子
	 * @param content
	 */
	private void searchCircle(final String content) {
		headers.put("key", content);
		headers.put("p", page+"");
		headers.put("cnt", count+"");
		
		mApi.requestPostData(circleSearch, CircleSearch.class, headers, new Response.Listener<CircleSearch>() {
			@Override
			public void onResponse(CircleSearch response) {
				mCircleSearch = response;
				if(isChage) {
					FragmentTransaction mTransaction = mFragmentManager.beginTransaction();
					if(mSearchCircleFragment.isHidden()) {
						mTransaction.show(mSearchCircleFragment);
						mTransaction.hide(mSearchToPicFragment);
						mTransaction.hide(mSearchLabelFragment);
						mTransaction.hide(mSearchUserFragment);
					}else {
						mTransaction.hide(mSearchToPicFragment);
						mTransaction.hide(mSearchLabelFragment);
						mTransaction.hide(mSearchUserFragment);
					}
					mTransaction.commitAllowingStateLoss();
					isChage = false;
					mSearchCircleFragment.addGroupList(response);
				}
				requestSucceed = true;
			}
		}, null);
	}
	
	/**
	 * 搜索话题
	 * @param content
	 */
	private void searchToPic(final String content) {
		headers.put("key", content);
		headers.put("p", page+"");
		headers.put("cnt", count+"");
		
		mApi.requestPostData(topicSearch, ToPicSearch.class, headers, new Response.Listener<ToPicSearch>() {
			@Override
			public void onResponse(ToPicSearch response) {
				mToPicSearch = response;
				mSearchToPicFragment.addToPicList(response);
			}
		}, null);
	}
	
	/**
	 * 搜索用户
	 * @param content
	 */
	private void searchUser(final String content) {
		headers.put("key", content);
		headers.put("p", page+"");
		headers.put("cnt", count+"");
		
		mApi.requestPostData(userSearch, UserSearch.class, headers, new Response.Listener<UserSearch>() {
			@Override
			public void onResponse(UserSearch response) {
				mUserSearch = response;
				mSearchUserFragment.addUserList(response);
			}
		}, null);
	}
}
