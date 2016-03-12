package com.shengzhish.xyj.activity.precioushouse;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;

import com.radiusnetworks.ibeacon.IBeacon;
import com.radiusnetworks.ibeacon.IBeaconConsumer;
import com.radiusnetworks.ibeacon.IBeaconManager;
import com.radiusnetworks.ibeacon.MonitorNotifier;
import com.radiusnetworks.ibeacon.RangeNotifier;
import com.radiusnetworks.ibeacon.Region;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.ActivityDetails;
import com.shengzhish.xyj.activity.entity.CodeItem;
import com.shengzhish.xyj.activity.entity.ScannerResponse;
import com.shengzhish.xyj.activity.precioushouse.ibeancon.BeaconServiceUtility;
import com.shengzhish.xyj.activity.precioushouse.zxing.activity.CaptureActivity;
import com.shengzhish.xyj.core.DialogFragmentActivity;
import com.shengzhish.xyj.core.ToastUtils;
import com.shengzhish.xyj.http.HttpboLis;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.ScreenUtil;
import com.shengzhish.xyj.util.Utils;
import com.shengzhish.xyj.view.AdapterView;
import com.shengzhish.xyj.view.HorizontalAbsListView;
import com.shengzhish.xyj.view.HorizontalListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * 宝藏室
 */
@ContentView(R.layout.precious_layout)
public class PreciousActivity extends DialogFragmentActivity implements IBeaconConsumer {

    private static final int TOTAL_COUNT = 5;
    @InjectView(R.id.back_imageview)
    private View back;
    @InjectView(R.id.scanner_qr_code)
    private ImageView scannerQrCode;

    @InjectView(R.id.picture_list)
    private HorizontalListView pictureHorizList;

    @InjectView(R.id.count_textview)
    private TextView countTextView;

    @InjectView(R.id.total_count_textview)
    private TextView totalCountTextview;

    @InjectView(R.id.note_message)
    private TextView noteMessage;


    @InjectView(R.id.pager_layout)
    private RelativeLayout viewPagerContainer;


    @InjectView(R.id.hint_arrow)
    private ImageButton hintArrow;

    private Context context;
    private String id;
    private int currentPosition;
    private List<CodeItem> pics;
    private String code;
    private PictureHozonAdapter adapter;
    private int dividerWidth = 12;
    private BeaconServiceUtility beaconUtill = null;
    private IBeaconManager iBeaconManager = null;
    private ArrayList<IBeacon> arrayL = new ArrayList<IBeacon>();
    private int intCode;
    //listHistoryPos记录当前可见的List顶端一行的位置
    public int listHistoryPos = 0;
    private int scrollLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();*/
        verifyBluetooth();
        context = PreciousActivity.this;
        id = getIntent().getStringExtra(IConstant.ACTIVITY_ID);

//        inatiaViewPager();
        getPictures();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        scannerQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCameraIntent = new Intent(PreciousActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });

        HttpboLis.getInstance().getHttp(this, ActivityDetails.class, String.format("activity/detail.json?id=%s", id), new HttpboLis.OnCompleteListener<ActivityDetails>() {
            @Override
            public void onComplete(ActivityDetails response) {
                if (response.getActivity() != null /*&& dialogIsShowing*/) {
                    if (!TextUtils.isEmpty(response.getActivity().getRule())) {
                        final Dialog dialog = new Dialog(PreciousActivity.this, R.style.DialogAnimation);
                        dialog.setContentView(R.layout.activity_news_rule_dialog);
                        TextView textView = (TextView) dialog.findViewById(R.id.activity_rule_text);
                        View closeView = dialog.findViewById(R.id.close_activity_rule_dialog);
                        closeView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        });
                        textView.setText(response.getActivity().getRule() + "\n");
                        if (!dialog.isShowing()) {
                            dialog.show();
                        }
                    }
                }
            }
        });

    }

    private void getPictures() {
        String url = code == null ? String.format("activity/code.json?id=%s", id) :
                String.format("activity/code.json?id=%s&code=%s", id, code);
        HttpboLis.getInstance().getHttpDialog(context, ScannerResponse.class,
                url, "正在加载", new HttpboLis.OnCompleteListener<ScannerResponse>() {
                    @Override
                    public void onComplete(ScannerResponse response) {
                        countTextView.setText(response.getGotTotal());
                        totalCountTextview.setText("/" + response.getTotal());
                        pics = response.getPics();
                        CodeItem rezult = response.getResult();
                        int currentPosition = 1;
                        if (null != rezult) {
                            for (int i = 0; i <= pics.size(); i++) {
                                if (pics.get(i).getId().equals(rezult.getId())) {
                                    currentPosition = Integer.valueOf(rezult.getId());
                                    break;
                                }
                            }
                        }
                        inatiaViewPager(pics, currentPosition);
                    }
                });
    }

    private void inatiaViewPager(List<CodeItem> items, int position) {
        noteMessage.setText(pics.get(position - 1).getDesc());
        int widthArrow = ScreenUtil.getViewWidth(hintArrow);
        int width = ScreenUtil.getScreenWidth(this) - widthArrow * 2 - Utils.dip2px(context, 16);
        int pageWidth = (width - Utils.dip2px(context, dividerWidth) * 2) / 3;


//        RelativeLayout.LayoutParams viewPagerPara = new RelativeLayout.LayoutParams(pageWidth, pageWidth * 2);
//        viewPager.setLayoutParams(viewPagerPara);

        RelativeLayout.LayoutParams imageViewPara = new RelativeLayout.LayoutParams(width, pageWidth * 2);
//        imageViewPara.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        pictureHorizList.setLayoutParams(imageViewPara);


        pictureHorizList.setDividerHeight(dividerWidth);


        adapter = new PictureHozonAdapter(this, pageWidth);
        pictureHorizList.setAdapter(adapter);
        adapter.addItem(items);
        adapter.setPosition(position);
        pictureHorizList.setSelection(position);
        pictureHorizList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setPosition(position);
                pictureHorizList.setSelectionFromTop(listHistoryPos, scrollLeft);
                noteMessage.setText(pics.get(position - 1).getDesc());
            }
        });
        pictureHorizList.setOnScrollListener(new HorizontalAbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(HorizontalAbsListView view, int scrollState) {
                if (scrollState == HorizontalAbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    listHistoryPos = pictureHorizList.getFirstVisiblePosition();
//                    Log.v("person", "listHistoryPos == " + listHistoryPos);
                }
                View v = pictureHorizList.getChildAt(0);
                int scrollTop = (v == null) ? 0 : v.getTop();
                scrollLeft = (v == null) ? 0 : v.getLeft();
//                Log.v("person", "scrollTop == " + scrollTop+", scrollLeft = "+scrollLeft);
            }

            @Override
            public void onScroll(HorizontalAbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            code = scanResult;
            getPictures();
        }
    }

    /**
     * Called when the iBeacon service is running and ready to accept your commands through the IBeaconManager
     */
    @Override
    public void onIBeaconServiceConnect() {
        if (iBeaconManager != null) {
            iBeaconManager.setRangeNotifier(new RangeNotifier() {
                @Override
                public void didRangeBeaconsInRegion(Collection<IBeacon> iBeacons, Region region) {
                    Log.v("person", "iBeacons  === " + iBeacons);
                    Log.v("person", "service run...");
                    if (iBeacons.size() == 0 || iBeacons == null) {
                        return;
                    }
                    arrayL.clear();
                    arrayL.addAll((ArrayList<IBeacon>) iBeacons);

                    if (arrayL != null && arrayL.size() > 0) {
                        for (int i = 0; i < arrayL.size(); i++) {
                            int major = arrayL.get(i).getMajor();
                            Log.v("person", "major == " + major);
                            if (intCode != major) {
                                code = String.valueOf(major);
                                getPictures();
//                                Log.v("person", "code == " + code);
                            }
                            intCode = major;


                        }
                    }
                }

            });

            iBeaconManager.setMonitorNotifier(new MonitorNotifier() {
                @Override
                public void didEnterRegion(Region region) {
                    Log.e("BeaconDetactorService", "didEnterRegion");
                    // logStatus("I just saw an iBeacon for the first time!");
                }

                @Override
                public void didExitRegion(Region region) {
//                    Log.e("BeaconDetactorService", "didExitRegion");
                    // logStatus("I no longer see an iBeacon");
                }

                @Override
                public void didDetermineStateForRegion(int state, Region region) {
//                    Log.e("BeaconDetactorService", "didDetermineStateForRegion");
                    // logStatus("I have just switched from seeing/not seeing iBeacons: " + state);
                }

            });

            try {
                iBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            try {
                iBeaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("NewApi")
    private void verifyBluetooth() {

        try {
            if (!IBeaconManager.getInstanceForApplication(this).checkAvailability()) {
                new AlertDialog.Builder(this)
                        .setTitle("Bluetooth not enabled")
                       /* .setMessage("Please enable bluetooth in settings and restart this application.")*/
                        .setMessage("Please enable bluetooth in settings or Click the OK button.")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //确定按钮事件
//                                finish();
//                                System.exit(0);
                                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                if (mBluetoothAdapter == null) {
                                    ToastUtils.show(PreciousActivity.this, "您的设备不支持蓝牙");
                                } else {
                                    if (!mBluetoothAdapter.isEnabled()) {
                                        mBluetoothAdapter.enable();
                                    }
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            while (true) {
                                                if (IBeaconManager.getInstanceForApplication(PreciousActivity.this).checkAvailability()) {
                                                    iBeaconManager = IBeaconManager.getInstanceForApplication(PreciousActivity.this);
                                                    beaconUtill = new BeaconServiceUtility(PreciousActivity.this);
                                                    if (iBeaconManager != null)
                                                        beaconUtill.onStart(iBeaconManager, PreciousActivity.this);
                                                    break;
                                                }
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
                                    }).start();
                                }

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //取消按钮事件
                                dialog.dismiss();
                            }
                        })
                        .show();

            } else {
                iBeaconManager = IBeaconManager.getInstanceForApplication(this);
                beaconUtill = new BeaconServiceUtility(this);
            }
        } catch (RuntimeException e) {
/*            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();*/

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
      /*  if (iBeaconManager != null)
            beaconUtill.onStart(iBeaconManager, this);*/


        if (iBeaconManager != null) {
            new Thread() {
                @Override
                public void run() {
                    beaconUtill.onStart(iBeaconManager, PreciousActivity.this);
                }
            }.start();

        }
    }

    @Override
    protected void onStop() {
        if (iBeaconManager != null)
            beaconUtill.onStop(iBeaconManager, this);
        super.onStop();
    }
}
