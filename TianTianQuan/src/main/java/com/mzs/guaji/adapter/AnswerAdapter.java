package com.mzs.guaji.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.Answer;
import com.mzs.guaji.entity.AnswerResponse;
import com.mzs.guaji.entity.Question;

import java.util.List;

/**
 * Created by wlanjie on 13-12-23.
 */
public class AnswerAdapter extends BaseAdapter {

    private Context context;
    private List<Answer> mAnswers;
    private Question mQuestion;
    private AnswerResponse response;

    public AnswerAdapter(Context context, AnswerResponse response, Question mQuestion, List<Answer> mAnswers) {
        this.context = context;
        this.response = response;
        this.mQuestion = mQuestion;
        this.mAnswers = mAnswers;
    }

    @Override
    public int getCount() {
        return mAnswers.size();
    }

    @Override
    public Object getItem(int position) {
        return mAnswers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        AnswerViewHolder mHolder;
        if(v == null) {
            mHolder = new AnswerViewHolder();
            v = View.inflate(context, R.layout.answer_item_layout, null);
            mHolder.mRelativeLayout = (RelativeLayout) v.findViewById(R.id.answer_item_root_layout);
            mHolder.mImageView = (ImageView) v.findViewById(R.id.answer_item_image);
            mHolder.mTextView = (TextView) v.findViewById(R.id.answer_item_text);
            mHolder.mAnswerId = mAnswers.get(position).getId();
            mHolder.mQuestion = mQuestion;
            mHolder.response = response;
            v.setTag(mHolder);
        }else {
            mHolder = (AnswerViewHolder) v.getTag();
        }
        mHolder.mTextView.setText(mAnswers.get(position).getAnswer());
        return v;
    }

    public static class AnswerViewHolder {
        public RelativeLayout mRelativeLayout;
        public ImageView mImageView;
        public TextView mTextView;
        public Question mQuestion;
        public long mAnswerId;
        public AnswerResponse response;
    }
}
