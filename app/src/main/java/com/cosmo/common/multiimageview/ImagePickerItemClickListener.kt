package com.cosmo.common.multiimageview

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter

/**
 *Author:ruchao.jiang
 *Created: 2018/8/14 14:51
 *Email:ruchao.jiang@uama.com.cn
 */
interface ImagePickerItemClickListener {
    fun  onItemClick(adapter: BaseQuickAdapter<*>, view: View, position:Int)

}