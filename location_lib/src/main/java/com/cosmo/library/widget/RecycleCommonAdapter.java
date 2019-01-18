package com.cosmo.library.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DWCloud on 2016/4/1.
 */
public abstract class RecycleCommonAdapter<T> extends RecyclerView.Adapter<RecycleCommonViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    protected Context mContext;
    protected List<T> mDatas = new ArrayList<>();
    protected final int mItemLayoutId;
    private View mHeaderView;

    public RecycleCommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        this.mContext = context;
        if (mDatas != null) {
            this.mDatas = mDatas;
        }
        this.mItemLayoutId = itemLayoutId;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @Override
    public RecycleCommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER)
            return new RecycleCommonViewHolder(mHeaderView, TYPE_HEADER);
        View view = LayoutInflater.from(mContext).inflate(mItemLayoutId, parent, false);
        return new RecycleCommonViewHolder(view, TYPE_NORMAL);
    }

    @Override
    public void onBindViewHolder(RecycleCommonViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) return;
        int itemPosition = position - (mHeaderView != null ? 1 : 0);
        convert(holder, mDatas.get(itemPosition), itemPosition);
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + (mHeaderView != null ? 1 : 0);
    }

    public abstract void convert(RecycleCommonViewHolder helper, T item, int position);




}