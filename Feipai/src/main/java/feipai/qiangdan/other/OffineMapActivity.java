package feipai.qiangdan.other;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.google.inject.Inject;

import java.util.ArrayList;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.util.AnimationHelper;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by 51wanh on 2015/1/25.
 */
@ContentView(R.layout.activity_offine_map)
public class OffineMapActivity extends DialogFragmentActivity implements MKOfflineMapListener {

    @Inject
    private Activity activity;

    @InjectView(R.id.rg)
    private RadioGroup radioGroup;
    @InjectView(R.id.return_back)
    private View returnBack;

    @InjectView(R.id.vf)
    private ViewFlipper viewFlipper;
    //热门城市列表
    @InjectView(R.id.hotcitylist)
    private ListView hotCityList;
    //全部城市列表
    @InjectView(R.id.city_list_view)
    private ListView allCityList;
    //城市输入框
    @InjectView(R.id.city)
    private EditText cityNameView;
    //已下载城市列表
    @InjectView(R.id.localmaplist)
    ListView localMapListView;

    ArrayList<MKOLSearchRecord> records1;
    /**
     * 已下载的离线地图信息列表
     */
    private ArrayList<MKOLUpdateElement> localMapList = null;
    private LocalMapAdapter lAdapter = null;


    private MKOfflineMap mOffline = null;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setListener();
        cityList();
        setOnClickList();
    }

    private void setOnClickList() {
        hotCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int postion, long l) {
                int cityid = records1.get(postion).cityID;
                mOffline.start(cityid);
                radioGroup.check(R.id.rb02);
                updateView();
            }
        });
    }

    private void setListener() {
        viewFlipper.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);
        viewFlipper.setFlipInterval(300);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                checkedChange(checkedId);

            }
        });

    }

    private void cityList() {
        mOffline = new MKOfflineMap();
        mOffline.init(this);

        ArrayList<String> hotCities = new ArrayList<String>();
        // 获取热闹城市列表
        records1 = mOffline.getHotCityList();
        if (records1 != null) {
            for (MKOLSearchRecord r : records1) {
                hotCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
                        + this.formatDataSize(r.size));
            }
        }
//        ListAdapter hAdapter = (ListAdapter) new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, hotCities);
        MapListAdapter hAdapter = new MapListAdapter(this);
        hAdapter.setItems(hotCities);
        hotCityList.setAdapter(hAdapter);
/*        // 获取所有支持离线地图的城市
        ArrayList<String> allCities = new ArrayList<String>();
        ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();
        if (records1 != null) {
            for (MKOLSearchRecord r : records2) {
                allCities.add(r.cityName + "(" + r.cityID + ")" + "   --"
                        + this.formatDataSize(r.size));
            }
        }
  *//*      ListAdapter aAdapter = (ListAdapter) new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, allCities);*//*
        MapListAdapter aAdapter=new MapListAdapter(this);
        aAdapter.setItems(allCities);
        allCityList.setAdapter(aAdapter);*/

        // 获取已下过的离线地图信息
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }

        ListView localMapListView = (ListView) findViewById(R.id.localmaplist);
        lAdapter = new LocalMapAdapter();
        localMapListView.setAdapter(lAdapter);
    }

    private void checkedChange(int id) {
        switch (id) {
            case R.id.rb01:
                viewFlipper.setInAnimation(AnimationHelper.inFromRight());
                viewFlipper.setOutAnimation(AnimationHelper.outToLeft());
                viewFlipper.setDisplayedChild(0);
                break;
            case R.id.rb02:
                viewFlipper.setInAnimation(AnimationHelper.inFromLeft());
                viewFlipper.setOutAnimation(AnimationHelper.outToRight());
                viewFlipper.setDisplayedChild(1);
                break;
        }
    }

    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    /**
     * 开始下载
     *
     * @param view
     */
    public void start(View view) {
        ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityNameView.getText().toString());

        if (records == null || records.size() != 1)
            return;
        int cityid = Integer.parseInt(String.valueOf(records.get(0).cityID));
        mOffline.start(cityid);
        radioGroup.check(R.id.rb02);
//        clickLocalMapListButton(null);
        if (cityName != null) {
            ToastUtils.show(activity, "开始下载离线地图.: " + cityName);
        }
     /*   Toast.makeText(this, "开始下载离线地图. cityid: " + cityid, Toast.LENGTH_SHORT)
                .show();*/
        updateView();
    }

    /**
     * 暂停下载
     *
     * @param view
     */
    public void stop(View view) {
        ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityNameView.getText().toString());

        if (records == null || records.size() != 1)
            return;
        int cityid = Integer.parseInt(String.valueOf(records.get(0).cityID));
        mOffline.pause(cityid);
        if (cityName != null) {
            ToastUtils.show(activity, "暂停下载离线地图.: " + cityName);
        }
    /*    Toast.makeText(this, "暂停下载离线地图. cityid: " + cityid, Toast.LENGTH_SHORT)
                .show();*/
        updateView();
    }

    /**
     * 删除离线地图
     *
     * @param view
     */
    public void remove(View view) {
        ArrayList<MKOLSearchRecord> records = mOffline.searchCity(cityNameView.getText().toString());

        if (records == null || records.size() != 1)
            return;
        int cityid = Integer.parseInt(String.valueOf(records.get(0).cityID));
        mOffline.remove(cityid);
        if (cityName != null) {
            ToastUtils.show(activity, "删除离线地图.: " + cityName);
        }
      /*  Toast.makeText(this, "删除离线地图. cityid: " + cityid, Toast.LENGTH_SHORT)
                .show();*/
        updateView();
    }

    /**
     * 更新状态显示
     */
    public void updateView() {
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }
        lAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        /**
         * 退出时，销毁离线地图模块
         */
        mOffline.destroy();
        super.onDestroy();
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                cityName = update.cityName;
                // 处理下载进度更新提示
                if (update != null) {
    /*                stateView.setText(String.format("%s : %d%%", update.cityName,
                            update.ratio));*/
                    updateView();
                }
            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);

                break;
        }
    }

    /**
     * 离线地图管理列表适配器
     */
    public class LocalMapAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return localMapList.size();
        }

        @Override
        public Object getItem(int index) {
            return localMapList.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int index, View view, ViewGroup arg2) {
            MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
            view = View.inflate(activity,
                    R.layout.offline_localmap_list, null);
            initViewItem(view, e);
            return view;
        }

        void initViewItem(View view, final MKOLUpdateElement e) {
            Button display = (Button) view.findViewById(R.id.display);
            Button remove = (Button) view.findViewById(R.id.remove);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView update = (TextView) view.findViewById(R.id.update);
            TextView ratio = (TextView) view.findViewById(R.id.ratio);
            ratio.setText(e.ratio + "%");
            title.setText(e.cityName);
            if (e.update) {
                update.setText("可更新");
            } else {
                update.setText("最新");
            }
            if (e.ratio != 100) {
                display.setEnabled(false);
            } else {
                display.setEnabled(true);
            }
            remove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mOffline.remove(e.cityID);
                    updateView();
                }
            });
/*            display.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
*//*                    Intent intent = new Intent();
                    intent.putExtra("x", e.geoPt.longitude);
                    intent.putExtra("y", e.geoPt.latitude);
//                    intent.setClass(activity, BaseMapDemo.class);
                    startActivity(intent);*//*
                }
            });*/
        }

    }
}
