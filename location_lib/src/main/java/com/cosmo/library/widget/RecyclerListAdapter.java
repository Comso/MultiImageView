package com.cosmo.library.widget;

import android.content.Context;
import java.util.List;


/**
 * Author:ruchao.jiang
 * Created: 2019/1/17 16:05
 * Email:ruchao.jiang@uama.com.cn
 */
public abstract class RecyclerListAdapter<T> extends RecycleCommonAdapter<T> {

    protected List<T> mDatas;

    public RecyclerListAdapter(Context context, List mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);

        this.mDatas = mDatas;
    }

    /**
     * 刷新数据
     */
    public void setNewData(List<T> datas) {


        this.mDatas.clear();
        this.mDatas.addAll(datas);
        super.notifyDataSetChanged();
    }

    /**
     * 加载更多数据
     */
    public void loadMoreData(List<T> datas) {


        this.mDatas.addAll(datas);
        super.notifyDataSetChanged();
    }

    /**
     * 清空数据
     */
    public void clearData() {
        this.mDatas.clear();
        super.notifyDataSetChanged();
    }
}
