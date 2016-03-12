package com.socialtv.personcenter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.Keyboard;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.Intents;
import com.socialtv.core.MultiTypeRefreshListFragment;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ThrowableLoader;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.InviteMobile;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-8-14.
 * 邀请手机通讯录中的好友
 */
public class InviteMobileFragment extends MultiTypeRefreshListFragment<InviteMobile> {

    private final static int INVITE = 100;

    private List<String> phoneNumbers;

    private Map<String, List<String>> phoneAndNames;

    @Inject
    private UserService service;

    @Inject
    private InviteFriendAdapter adapter;

    @InjectView(R.id.list_empty_layout)
    private View listEmptyLayout;

    @InjectView(R.id.list_empty_text)
    private TextView emptyText;

    @InjectView(R.id.invite_friend_list_layout)
    private View listLayout;

    @InjectView(R.id.invite_friend_no_binding_layout)
    private View noBindingLayout;

    @InjectView(R.id.invite_friend_binding_mobile)
    private View bindingMobileView;

    @InjectView(R.id.invite_friend_search_edit)
    private EditText searchEditText;

    @InjectView(R.id.invite_friend_search_cancel)
    private TextView searchCancel;

    @InjectView(R.id.invite_friend_search_edit_layout)
    private View searchLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneNumbers = new ArrayList<String>();
        phoneAndNames = new HashMap<String, List<String>>();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final String mobile = LoginUtil.getUserMobile(getActivity());
        if (TextUtils.isEmpty(mobile)) {
            show(noBindingLayout);
            hide(listLayout);
            bindingMobileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intents(getActivity(), BindingMobileActivity.class).add(IConstant.IS_BACK, false).toIntent(), 0);
                }
            });
        } else {
            show(listLayout);
            hide(noBindingLayout);
        }
        refreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
        emptyText.setText("暂无任何内容");
        hasMore = true;
        listView.setDividerHeight(Utils.dip2px(getActivity(), 1));
    }

    @Override
    protected int getContentView() {
        return R.layout.invite_friend_list;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            final String mobile = LoginUtil.getUserMobile(getActivity());
            if (TextUtils.isEmpty(mobile)) {
                show(noBindingLayout);
                hide(listLayout);
            } else {
                show(listLayout);
                hide(noBindingLayout);
            }
        }
    }

    @Override
    public Loader<InviteMobile> onCreateLoader(int id, Bundle bundle) {
        return new ThrowableLoader<InviteMobile>(getActivity()) {
            @Override
            public InviteMobile loadData() throws Exception {
                final ContentResolver resolver = getActivity().getContentResolver();
                final Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "sort_key");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        final String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        final String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        final Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                        if (phoneCursor != null) {
                            while (phoneCursor.moveToNext()) {
                                final String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                phoneNumbers.add(phoneNumber);
                                List<String> items = phoneAndNames.get(phoneNumber);
                                if (items == null) {
                                    List<String> names = new ArrayList<String>();
                                    names.add(name);
                                    phoneAndNames.put(phoneNumber, names);
                                } else {
                                    List<String> names = new ArrayList<String>();
                                    names.add(name);
                                    names.add(items.get(0));
                                    phoneAndNames.put(phoneNumber, names);
                                }
                            }
                        }
                        phoneCursor.close();
                    }
                    cursor.close();
                }
                return (InviteMobile) HttpUtils.doRequest(createMobileRequest(phoneNumbers)).result;
            }
        };
    }

    private Request<InviteMobile> createMobileRequest(final List<String> mobiles) {
        return service.createMobiles(mobiles);
    }

    @Override
    public void onLoadFinished(Loader<InviteMobile> loader, InviteMobile data) {
        super.onLoadFinished(loader, data);
        loadingIndicator.setVisible(false);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (!data.getMatches().isEmpty()) {
                    this.items = data.getMatches();
                    adapter.setItem(data.getMatches(), phoneAndNames);
                }
                if (!data.getUnmatches().isEmpty()) {
                    this.items = data.getUnmatches();
                    adapter.setInviteItem(data.getUnmatches(), phoneAndNames);
                }
                show(searchLayout);
                searchEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                searchCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchEditText.setText("");
                        adapter.getFilter().filter("");
                        Keyboard.hideSoftInput(searchEditText);
                    }
                });
            }
        }
        showList();
        if (this.items.isEmpty()) {
            show(listEmptyLayout);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        final String mobile = LoginUtil.getUserMobile(getActivity());
        if (!TextUtils.isEmpty(mobile)) {
            menu.add(Menu.NONE, INVITE, 0, "邀请")
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == INVITE) {
            new ProgressDialogTask<Response>(getActivity()){
                /**
                 * Execute task with an authenticated account
                 *
                 * @param data
                 * @return result
                 * @throws Exception
                 */
                @Override
                protected Response run(Object data) throws Exception {
                    return (Response) HttpUtils.doRequest(service.createInviteFriend(adapter.getCheckPhoneNumbers())).result;
                }

                @Override
                protected void onSuccess(Response response) throws Exception {
                    super.onSuccess(response);
                    if (response != null) {
                        if (response.getResponseCode() == 0) {
                            ToastUtils.show(getActivity(), "邀请成功");
                            adapter.clearChecks();
                        } else {
                            ToastUtils.show(getActivity(), response.getResponseMessage());
                        }
                    }
                }
            }.start("请稍候");
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Is add adapter header
     *
     * @return
     */
    @Override
    protected boolean isAddAdapterHeader() {
        return false;
    }

    /**
     * Adapter header view
     *
     * @return
     */
    @Override
    protected View adapterHeaderView() {
        return null;
    }

    /**
     * Create adapter to display items
     *
     * @return adapter
     */
    @Override
    protected MultiTypeAdapter createAdapter() {
        return adapter;
    }

    /**
     * Create Request
     *
     * @return
     */
    @Override
    protected Request<InviteMobile> createRequest() {
        return null;
    }

    /**
     * Get error message to display for exception
     *
     * @param exception
     * @return string resource id
     */
    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error;
    }

    /**
     * Get resource id of {@link String} to display when loading
     *
     * @return string resource id
     */
    @Override
    protected int getLoadingMessage() {
        return R.string.loading;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        phoneNumbers.clear();
        phoneNumbers = null;
        phoneAndNames.clear();
        phoneAndNames = null;
    }
}
