package feipai.qiangdan.other;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.util.IConstant;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * 参考手册
 */
@ContentView(R.layout.layout_reference)
public class ReferenceActivity extends DialogFragmentActivity {
    @InjectView(R.id.return_back)
    private View returnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        int[] images = new int[]
                {R.drawable.icon_service, R.drawable.icon_manage,
                        R.drawable.icon_ready, R.drawable.icon_avoid,
                        R.drawable.icon_event, R.drawable.icon_punish,
                        R.drawable.icon_cooperate, R.drawable.icon_action,
                        R.drawable.icon_award, R.drawable.icon_safe};
        String[] texts = new String[]
                {"服务流程", "违约管理",
                        "岗前准备", "风险规避",
                        "事故处理", "处罚标准",
                        "合作协议", "行为规范",
                        "奖励政策", "安全策略"};

        GridView gridview = (GridView) findViewById(R.id.gridview);
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < images.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", images[i]);
            map.put("itemText", texts[i]);
            lstImageItem.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this,
                lstImageItem,// 数据源
                R.layout.gridview_item,// 显示布局
                new String[]{"itemImage", "itemText"},
                new int[]{R.id.itemImage, R.id.itemText});
        gridview.setAdapter(adapter);
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selected(position);
            }
        });
    }

    private void selected(int position) {
        switch (position) {
            case 0:
                //服务流程
                WebViewActivity.launch(this, "抢单流程", IConstant.DOMAIN + "api/v1/newbie");
                break;
            default:
                Toast.makeText(this, "暂无内容", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
