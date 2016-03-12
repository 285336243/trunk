package com.jiujie8.choice.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiujie8.choice.R;
import com.jiujie8.choice.core.DialogFragment;

/**
 * Created by wlanjie on 14/12/17.
 * 主页中的加载更多的Loading Fragment
 */
public class LoadingFragment extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_loading, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
