package feipai.qiangdan.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import feipai.qiangdan.R;
import feipai.qiangdan.core.TabPagerActivity;
import feipai.qiangdan.crashcatch.AppManager;
import feipai.qiangdan.map.MapFragment;
import feipai.qiangdan.util.IConstant;

/**
 * 主页
 */
public class HomeActivity extends TabPagerActivity<HomePagerAdapter> {

    private long firstTime = 0;
    private MapBroadcastReceiver mMapReceiver;
    //检查更新对话框
    public boolean isShowUpdateDialog=false;

    public HomeActivity() {
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureTabPager();

        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
     /*       Intent intent = new Intent(this, RoborderActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);*/
            int index = getIntent().getIntExtra(IConstant.MAP_GUID, 0);
            host.setCurrentTab(index);
            pager.setCurrentItem(index);
        }
        mMapReceiver = new MapBroadcastReceiver();
        IntentFilter rFilter = new IntentFilter();
        rFilter.addAction(IConstant.MAP_BROADCAST);
        registerReceiver(mMapReceiver, rFilter);

    }


    /**
     * Create pager adapter
     *
     * @return pager adapter
     */
    @Override
    protected HomePagerAdapter createAdapter() {
        return new HomePagerAdapter(this);
    }


    @Override
    protected int getLeftBackground() {
        return R.drawable.tab_selector;
    }

    @Override
    protected int getCentreBackground() {
        return R.drawable.tab_selector;
    }

    @Override
    protected int getRightBackground() {
        return R.drawable.tab_selector;
    }

    @Override
    protected int getTabIcon(int position) {
        if (position == 0) {
            return R.drawable.order_selectedxml;
        } else if (position == 1) {
            return R.drawable.map_selectedxml;
        } else if (position == 2) {
            return R.drawable.my_selectedxml;
        } else if (position == 3) {
            return R.drawable.other_selectedxml;
        }
        return super.getTabIcon(position);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                AppManager.getAppManager().AppExit(this);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMapReceiver != null) {
            this.unregisterReceiver(mMapReceiver);
        }
    }
    private class MapBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (IConstant.MAP_BROADCAST.equals(intent.getAction()) ) {
                    //转到三是为了在地图刷新，不然不刷新
                    host.setCurrentTab(3);
                    pager.setCurrentItem(3);
                    //转到地图页面
                    host.setCurrentTab(1);
                    pager.setCurrentItem(1);
                }

            }
        }
    }

}
