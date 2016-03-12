package com.socialtv.shop;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.volley.GsonUtils;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.core.Utils;
import com.socialtv.shop.entity.Item;

/**
 * Created by wlanjie on 14-5-20.
 */
public class ShopAdapter extends MultiTypeAdapter {


    private final Gson gson;

    private final ImageLoader imageLoader;

    private final Activity activity;

    private final int width;
    private static final int NO_SHOP_INFORMATION = 0;
    private static final int SHOP_LIST = 1;
    private int count = 1;

    /**
     * Create adapter
     *
     * @param activity
     */
    public ShopAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        gson = GsonUtils.createGson();
        imageLoader = ImageLoader.getInstance();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.width = metrics.widthPixels;

    }

    /**
     * Get layout id for type
     *
     * @param type
     * @return layout id
     */
    @Override
    protected int getChildLayoutId(int type) {
        return R.layout.shop_single_goods_item;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }


    public void addItemObject(Object object) {
        if (object != null) {
            if (object instanceof Item)
                addItem((Item) object);
            if (object instanceof String)
                addItem((String) object);

        }
    }

    private void addItem(String string) {
        addItem(NO_SHOP_INFORMATION, string);
    }

    private void addItem(Item item) {
        addItem(SHOP_LIST, item);
    }


    /**
     * Get child view ids for type
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @param type
     * @return array of view ids
     */
    @Override
    protected int[] getChildViewIds(int type) {
        return new int[]{
                R.id.single_goods_item_layout,//商品布局的layout 0
                R.id.goods_pic,              //商品图片   1
                R.id.googs_price,            //商品价格   2
                R.id.goods_name,              //商品名称   3
                R.id.margin_layout          //边距layout  4
        };
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    protected View initialize(int type, View view) {
        return super.initialize(type, view);

    }

    /**
     * Update view for item
     *
     * @param position
     * @param item
     * @param type
     */
    @Override
    protected void update(int position, Object item, int type) {
        int intenalmWidrh = (width - Utils.dip2px(activity, 36)) / 2;
        FrameLayout.LayoutParams params=new FrameLayout.LayoutParams(intenalmWidrh,(int)(intenalmWidrh/0.88));
        view(1).setLayoutParams(params);

        LinearLayout.LayoutParams marginPara = new LinearLayout.LayoutParams(intenalmWidrh, ViewGroup.LayoutParams.MATCH_PARENT);
        if (position % 2 == 0) {
            marginPara.leftMargin = Utils.dip2px(activity, 12);
            marginPara.rightMargin = Utils.dip2px(activity, 6);
        } else {
            //position % 2 == 1
            marginPara.leftMargin = Utils.dip2px(activity, 6);
            marginPara.rightMargin = Utils.dip2px(activity, 12);
        }
        view(4).setLayoutParams(marginPara);

        if (item instanceof Item) {
            switch (type) {
                case SHOP_LIST:
                    setTopicContent((Item) item);
            }
        }
        if (item instanceof String) {
            switch (type) {
                case NO_SHOP_INFORMATION:
                    setNoStarDynamicInformation((String) item);
            }

        }
    }

    private void setTopicContent(Item entry) {
        if (entry != null) {
            ImageLoader.getInstance().displayImage(entry.getImg(), imageView(1)/*, ImageUtils.imageLoader(activity, 0)*/);
            setText(3, entry.getName());
            if (entry.getRefer() != null)
                setText(2, entry.getRefer().getPrice());
//                setText(2, entry.getRefer().getPrice()+"||"+count);
//            count=count+1;
        }


    }

    private void setNoStarDynamicInformation(String string) {

    }

}
