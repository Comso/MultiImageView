package com.cosmo.common.multiimageview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import kotlin.reflect.KClass

/**
 *Author:ruchao.jiang
 *Created: 2018/12/19 13:32
 *Email:ruchao.jiang@uama.com.cn
 */

fun <T : FragmentActivity> Context.start(activity: KClass<T>){
    startActivity(Intent(this, activity.java))
}