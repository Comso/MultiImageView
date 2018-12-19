package com.cosmo.common.multiimageview

import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 *Author:ruchao.jiang
 *Created: 2018/8/13 15:20
 *Email:ruchao.jiang@uama.com.cn
 */
class MainListAdapter: BaseQuickAdapter<String>(R.layout.main_list_item,null){
    override fun convert(holder: BaseViewHolder, position: String) {
        val text:TextView = holder.getView(R.id.textView)
        text.text = position
    }
}