package com.cosmo.library.widget;

/**
 * Created by DWCloud on 2016/4/27.
 */

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by DWCloud on 2016/4/1.
 */
public class RecycleCommonViewHolder extends RecyclerView.ViewHolder{
    private final SparseArray<View> mViews;
    private View mConvertView;
    private OnRecycleItemClickListener myItemClickListener;
    private int viewType;

    public RecycleCommonViewHolder(View itemView,int viewType) {
        super(itemView);
        this.mViews = new SparseArray<View>();
        this.mConvertView = itemView;
        this.viewType = viewType;
        mConvertView.setTag(this);
    }


    public <T extends View> T getView(int viewId) {

        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public RecycleCommonViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public RecycleCommonViewHolder getConvertView() {
        return this;
    }

    //设置字体颜色
    public RecycleCommonViewHolder setTextColor(Context context, int viewId, @ColorRes int res){
        TextView view = getView(viewId);
        view.setTextColor(ContextCompat.getColor(context,res));
        return this;
    }

    public void setOnItemClickListener(final OnRecycleItemClickListener myItemClickListener){
        this.myItemClickListener = myItemClickListener;
        mConvertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myItemClickListener.itemClick();
            }
        });
    }

    public RecycleCommonViewHolder setViewVisible(int viewId,boolean visible){
        if(visible){
            getView(viewId).setVisibility(View.VISIBLE);
            return this;
        }
        getView(viewId).setVisibility(View.GONE);
        return this;
    }
    public RecycleCommonViewHolder setImageResource(int viewId, @DrawableRes int res) {
        ImageView view = getView(viewId);
        view.setImageResource(res);
        return this;
    }



    public RecycleCommonViewHolder setImageBackgroundRes(int viewId, @DrawableRes int res) {
        ImageView imageView = getView(viewId);
        imageView.setBackgroundResource(res);
        return this;
    }
}
