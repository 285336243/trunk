package com.socialtv.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.R;
import com.socialtv.activity.ActivityDetailActivity;
import com.socialtv.core.Intents;
import com.socialtv.home.entity.Entries;
import com.socialtv.home.entity.Entry;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.program.ProgramActivity;
import com.socialtv.shop.ShopActivity;
import com.socialtv.shop.ShopDetailsActivity;
import com.socialtv.star.StarActivity;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;
import com.socialtv.view.AdapterView;
import com.socialtv.view.HeaderGridView;
import com.socialtv.view.HorizontalListView;

/**
 * Created by wlanjie on 14-6-22.
 * 发现页面的Adapter
 */
public class HomeListAdapter extends MultiTypeAdapter {

    private final static int ITEM_ICON = 0;
    private final static int ITEM_TEXT = 1;
    private final static int ITEM_LIST = 2;
    private final static int LOOK_ALL = 3;

    private final static int ADV_IMAGE = 0;

    private final static int GROUP = 0;
    private final static int ACTIVITY = 1;
    private final static int CELEBRITY = 2;
    private final static int ADV = 3;
    private final static int SHOP = 4;
    private final static int NEWS = 5;

    private final Activity activity;
    private final LayoutInflater inflater;

    private final int width;
    private boolean isRefresh;
    /**
     * Create adapter
     *
     * @param activity
     */
    public HomeListAdapter(Activity activity) {
        super(activity);
        this.activity = activity;
        this.inflater = activity.getLayoutInflater();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
    }

    public HomeListAdapter addItem(final Entries entries, final boolean isRefresh) {
        this.isRefresh = isRefresh;
        if ("GROUP".equals(entries.getType())) {
            addItem(GROUP, entries);
        } else if ("ACTIVITY".equals(entries.getType())) {
            addItem(ACTIVITY, entries);
        } else if ("CELEBRITY".equals(entries.getType())) {
            addItem(CELEBRITY, entries);
        } else if ("ADV".equals(entries.getType())) {
            addItem(ADV, entries);
        } else if ("SHOP".equals(entries.getType())) {
            addItem(SHOP, entries);
        } else if ("NEWS".equals(entries.getType()) || "VIDEO".equals(entries.getType())) {
            addItem(NEWS, entries);
        }
        return this;
    }

    /**
     * Get layout id for type
     *
     * @param type
     * @return layout id
     */
    @Override
    protected int getChildLayoutId(int type) {
        if (type == GROUP) {
            return R.layout.home_item_program;
        } else if (type == ACTIVITY) {
            return R.layout.home_item_activity;
        } else if (type == CELEBRITY) {
            return R.layout.home_item_star;
        } else if (type == ADV) {
            return R.layout.home_item_adv;
        } else if (type == SHOP) {
            return R.layout.home_item_shop;
        } else if (type == NEWS) {
            return R.layout.home_item_news;
        }
        return -1;
    }

    @Override
    public int getViewTypeCount() {
        return 7;
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
        if (type == GROUP) {
            return new int[]{
                    R.id.item_icon,
                    R.id.item_text,
                    R.id.program_list
            };
        } else if (type == ACTIVITY) {
            return new int[] {
                    R.id.activity_icon,
                    R.id.activity_text,
                    R.id.activity_list
            };
        } else if (type == CELEBRITY) {
            return new int[] {
                    R.id.star_item_icon,
                    R.id.star_item_text,
                    R.id.star_list
            };
        } else if (type == ADV) {
            return new int[] {
                R.id.home_item_adv_image
            };
        } else if (type == SHOP) {
            return new int[] {
                    R.id.shop_icon,
                    R.id.shop_text,
                    R.id.shop_list,
                    R.id.lookup_all
            };
        } else if (type == NEWS) {
            return new int[]{
                    R.id.news_icon,
                    R.id.news_text,
                    R.id.news_list,
                    R.id.news_lookup_all
            };
        }
        return new int[0];
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
        final Entries entries = (Entries) item;
        if (entries != null) {
            if (type == GROUP) {
                HorizontalListView listView = view(ITEM_LIST);
                ImageLoader.getInstance().displayImage(entries.getTagImg(), imageView(ITEM_ICON));
                setText(ITEM_TEXT, entries.getName());
                ProgramAdapter adapter = (ProgramAdapter) listView.getAdapter();
                adapter.clear();
                adapter.addItem(entries.getEntry());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (entries.getEntry() != null) {
                            Entry entry = entries.getEntry().get(position - 1);
                            if (entry != null && entry.getRefer() != null)
                                activity.startActivity(new Intents(activity, ProgramActivity.class).add(IConstant.PROGRAM_ID, entry.getRefer().getId()).toIntent());
                        }
                    }
                });
            } else if (type == ACTIVITY) {
                HeaderGridView gridView = view(ITEM_LIST);
                ImageLoader.getInstance().displayImage(entries.getTagImg(), imageView(ITEM_ICON));
                setText(ITEM_TEXT, entries.getName());
                ActivityAdapter activityAdapter = (ActivityAdapter) gridView.getAdapter();
                activityAdapter.setItems(entries.getEntry());
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (entries.getEntry() != null) {
                            Entry entry = entries.getEntry().get(position);
                            if (entry != null && entry.getRefer() != null)
                                activity.startActivity(new Intents(activity, ActivityDetailActivity.class).add(IConstant.ACTIVITY_ID, entry.getRefer().getId()).toIntent());
                        }
                    }
                });
            } else if (type == CELEBRITY) {
                HorizontalListView listView = view(ITEM_LIST);
                ImageLoader.getInstance().displayImage(entries.getTagImg(), imageView(ITEM_ICON));
                setText(ITEM_TEXT, entries.getName());
                StarAdapter starAdapter = (StarAdapter) listView.getAdapter();
                starAdapter.clear();
                starAdapter.addItem(entries.getEntry());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (entries.getEntry() != null) {
                            Entry entry = entries.getEntry().get(position - 1);
                            if (entry != null && entry.getRefer() != null) {
                                activity.startActivity(new Intents(activity, StarActivity.class).add(IConstant.STAR_ID, entry.getRefer().getId()).toIntent());
                            }
                        }
                    }
                });
            } else if (type == ADV) {
                if (entries.getEntry() != null && entries.getEntry().get(0) != null) {
                    ImageLoader.getInstance().displayImage(entries.getEntry().get(0).getImg(), imageView(ADV_IMAGE), ImageUtils.imageLoader(activity, 0));
                }
                view(ADV_IMAGE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (entries.getEntry() != null) {
                            if (entries.getEntry().get(0) != null) {
                                if (entries.getEntry().get(0).getRefer() != null) {
                                    if ("WEB_VIEW".equals(entries.getEntry().get(0).getRefer().getOpenType())) {
                                        if (entries.getEntry().get(0).getRefer().getRequireLogin() == 1 && !LoginUtil.isLogin(activity)) {
                                            activity.startActivity(new Intents(activity, LoginActivity.class).toIntent());
                                        } else {
                                            activity.startActivity(new Intents(activity, WebViewActivity.class).add(IConstant.URL, entries.getEntry().get(0).getRefer().getUrl())
                                                    .add(IConstant.TITLE, entries.getEntry().get(0).getRefer().getTitle())
                                                    .add(IConstant.HIDE_STATUS, entries.getEntry().get(0).getRefer().getHideStatus())
                                                    .add(IConstant.HIDE_TITLE, entries.getEntry().get(0).getRefer().getHideTitle()).toIntent());
                                        }
                                    } else {
                                        Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entries.getEntry().get(0).getRefer().getUrl()));
                                        activity.startActivity(mIntent);
                                    }
                                }
                            }
                        }
                    }
                });
            } else if (type == SHOP) {
                HorizontalListView listView = view(ITEM_LIST);
                ImageLoader.getInstance().displayImage(entries.getTagImg(), imageView(ITEM_ICON));
                setText(ITEM_TEXT, entries.getName());
                ShopAdapter shopAdapter = (ShopAdapter) listView.getAdapter();
                shopAdapter.clear();
                shopAdapter.addItem(entries.getEntry());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (entries.getEntry() != null) {
                            Entry entry = entries.getEntry().get(position-1);
                            if (entry != null && entry.getRefer() != null)
                                activity.startActivity(new Intent(activity, ShopDetailsActivity.class).putExtra(IConstant.SHOP_ID,entry.getRefer().getId()));

                        }
                    }
                });
            } else if (type == NEWS) {
                HorizontalListView listView = view(ITEM_LIST);
                ImageLoader.getInstance().displayImage(entries.getTagImg(), imageView(ITEM_ICON));
                setText(ITEM_TEXT, entries.getName());
                NewsAdapter newsAdapter = (NewsAdapter) listView.getAdapter();
                newsAdapter.clear();
                newsAdapter.addItem(entries.getEntry());
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (entries.getEntry() != null) {
                            Entry entry = entries.getEntry().get(position - 1);
                            if (entry != null && entry.getRefer() != null)
                                activity.startActivity(new Intents(activity, WebViewActivity.class).add(IConstant.URL, entry.getRefer().getUrl())
                                        .add(IConstant.TITLE, entry.getRefer().getTitle()).add(IConstant.HIDE_TITLE, entry.getRefer().getHideTitle())
                                        .add(IConstant.HIDE_STATUS, entry.getRefer().getHideStatus()).toIntent());
                        }
                    }
                });

                view(LOOK_ALL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intents(activity, NewsActivity.class).add(IConstant.TYPE, entries.getType()).toIntent());
                    }
                });
            }
        }
    }

    public View getView(final int position, View convertView,
                        final ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            convertView = initialize(type, inflater.inflate(getChildLayoutId(type), null));
            final Entries entries = (Entries) getItem(position);
            if (entries != null) {
                if (type == GROUP) {
                    HorizontalListView listView = view(ITEM_LIST);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, (int) (width * 0.3));
                    listView.setLayoutParams(params);
                    listView.setDividerHeight(12);
                    listView.setAdapter(new ProgramAdapter(activity, width));

                } else if (type == ACTIVITY) {
                    HeaderGridView gridView = view(ITEM_LIST);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, (int) (width * 0.425));
                    gridView.setLayoutParams(params);
                    gridView.setHorizontalSpacing(12);
                    gridView.setVerticalSpacing(12);

                    gridView.setAdapter(new ActivityAdapter(activity, width));
                } else if (type == CELEBRITY) {
                    HorizontalListView listView = view(ITEM_LIST);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, (int) (width * 0.2));
                    listView.setLayoutParams(params);
                    listView.setDividerHeight(12);
                    listView.setAdapter(new StarAdapter(activity, width));
                } else if (type == ADV) {
                    AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, width / 2);
                    imageView(ADV_IMAGE).setLayoutParams(params);
                } else if (type == SHOP) {
                    final TextView lookupAll = view(LOOK_ALL);
                    lookupAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.startActivity(new Intent(activity, ShopActivity.class));
                        }
                    });
                    HorizontalListView listView = view(ITEM_LIST);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, (int) (width * 0.425));
                    listView.setLayoutParams(params);
                    listView.setDividerHeight(12);
                    listView.setAdapter(new ShopAdapter(activity, width));
                } else if (type == NEWS) {
                    HorizontalListView listView = view(ITEM_LIST);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, (int) (width * 0.3));
                    listView.setLayoutParams(params);
                    listView.setDividerHeight(12);
                    listView.setAdapter(new NewsAdapter(activity, width));
                }
            }
        }
        update(position, convertView, getItem(position), type);
        return convertView;
    }
}
