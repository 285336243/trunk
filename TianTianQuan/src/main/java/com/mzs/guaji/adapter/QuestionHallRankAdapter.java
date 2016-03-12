package com.mzs.guaji.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.QuestionRank;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by sunjian on 13-12-25.
 */
public class QuestionHallRankAdapter extends BaseAdapter {

    private List<QuestionRank> questionFriendRankList;
    private List<QuestionRank> questionAllRankList;
    private boolean friendRankList = true;
    private Context context;
    private ImageLoader mImageLoader;

    public QuestionHallRankAdapter(Context context, List<QuestionRank> questionFriendRankList, List<QuestionRank> questionAllRankList){
        this.questionFriendRankList = questionFriendRankList;
        this.questionAllRankList = questionAllRankList;
        this.context = context;
        mImageLoader = ImageLoader.getInstance();
    }

    public void covert(boolean isFriendRankList){
        friendRankList = isFriendRankList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return getAdapterSource() == null?0:getAdapterSource().size();
    }

    @Override
    public Object getItem(int position) {
        return getAdapterSource().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        QuestionHallRankHolder holder = null;
        if(v == null){
            v = View.inflate(context, R.layout.scan_ranking_list_item, null);
            holder = new QuestionHallRankHolder();
            holder.rankImageView = (ImageView)v.findViewById(R.id.rank_no_img);
            holder.rankTextView = (TextView)v.findViewById(R.id.rank_no_text);
            holder.userAvatar = (ImageView)v.findViewById(R.id.rank_user_avatar);
            holder.userNickname = (TextView)v.findViewById(R.id.rank_user_nickname);
            holder.coins = (TextView)v.findViewById(R.id.rank_coins);
            v.setTag(holder);
        }else{
            holder = (QuestionHallRankHolder)v.getTag();
        }
        holder.coins.setText("" + getAdapterSource().get(position).getCoins());
        holder.userNickname.setText(getAdapterSource().get(position).getUserNickname());
        mImageLoader.displayImage(getAdapterSource().get(position).getUserAvatar(), holder.userAvatar, ImageUtils.imageLoader(context, 4));

        if(position<3){
            holder.rankTextView.setVisibility(View.GONE);
            holder.rankImageView.setVisibility(View.VISIBLE);
            switch (position){
                case 0:
                    holder.rankImageView.setBackgroundResource(R.drawable.icon_nofir_tj);
                    break;
                case 1:
                    holder.rankImageView.setBackgroundResource(R.drawable.icon_nosec_tj);
                    break;
                case 2:
                    holder.rankImageView.setBackgroundResource(R.drawable.icon_nothr_tj);
                    break;
            }
        }else{
            holder.rankTextView.setVisibility(View.VISIBLE);
            holder.rankImageView.setVisibility(View.GONE);
            holder.rankTextView.setText(""+(position+1));
        }



        return v;
    }

    private List<QuestionRank> getAdapterSource(){
        return friendRankList?questionFriendRankList:questionAllRankList;
    }

    public static class QuestionHallRankHolder{
        public ImageView rankImageView;
        public TextView rankTextView;
        public ImageView userAvatar;
        public TextView userNickname;
        public TextView coins;
    }
}
