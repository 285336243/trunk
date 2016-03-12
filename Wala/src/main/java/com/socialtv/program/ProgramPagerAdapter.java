package com.socialtv.program;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.inject.Inject;
import com.socialtv.util.IConstant;

/**
 * Created by wlanjie on 14-7-25.
 * 节目组ViewPager的Adapter
 */
public class ProgramPagerAdapter extends FragmentStatePagerAdapter {

    private final String programId;
    private final BannerView bannerView;

    @Inject
    public ProgramPagerAdapter(FragmentManager fm, final String programId, final BannerView bannerView) {
        super(fm);
        this.programId = programId;
        this.bannerView = bannerView;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            ProgramChatFragment chatFragment = new ProgramChatFragment();
            Bundle bundle = new Bundle();
            bundle.putString(IConstant.PROGRAM_ID, programId);
            chatFragment.setArguments(bundle);
            chatFragment.setBannerView(bannerView);
            return chatFragment;
        } else if (position == 1) {
            ProgramTopicFragment topicFragment = new ProgramTopicFragment();
            Bundle bundle = new Bundle();
            bundle.putString(IConstant.PROGRAM_ID, programId);
            topicFragment.setArguments(bundle);
            topicFragment.setBannerView(bannerView);
            return topicFragment;
        } else if (position == 2) {
            ProgramNewsFragment newsFragment = new ProgramNewsFragment();
            Bundle bundle = new Bundle();
            bundle.putString(IConstant.PROGRAM_ID, programId);
            newsFragment.setArguments(bundle);
            newsFragment.setBannerView(bannerView);
            return newsFragment;
        }
        return null;
    }



    @Override
    public int getCount() {
        return 3;
    }
}
