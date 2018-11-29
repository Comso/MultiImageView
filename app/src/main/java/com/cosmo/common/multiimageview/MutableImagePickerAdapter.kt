package com.cosmo.common.multiimageview

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

/**
 *Author:ruchao.jiang
 *Created: 2018/8/13 15:20
 *Email:ruchao.jiang@uama.com.cn
 */
class MutableImagePickerAdapter: BaseQuickAdapter<Any>(R.layout.widget_mutable_image_picker_item,null){
    override fun convert(holder: BaseViewHolder, item: Any) {
        val image:ImageView = holder.getView(R.id.widget_mutable_image)
        when (item) {
            is String -> Glide.with(mContext).load(item).dontAnimate().thumbnail(0.1f).into(image)
            is Int ->  image.setImageResource(item)
            else->{}
        }
    }
}