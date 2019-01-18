package com.cosmo.common.multiimageview

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.cosmo.common.multiimageview.edittext.EditTextActivity
import com.cosmo.common.multiimageview.rxtest.RxTestActivity
import com.cosmo.library.widget.SelectLocationActivity
import com.yanzhenjie.permission.AndPermission
import kotlinx.android.synthetic.main.activity_main.*

/**
 *Author:ruchao.jiang
 *Created: 2018/12/19 13:19
 *Email:ruchao.jiang@uama.com.cn
 */
class MainActivity: AppCompatActivity() {
    private val adapter by lazy { MainListAdapter() }
    private val list:MutableList<String> = mutableListOf()
    private var context:Context?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        recyclerview.addOnItemTouchListener(object :OnItemClickListener(){
            override fun SimpleOnItemClick(p0: BaseQuickAdapter<*>?, p1: View?, p2: Int) {
                when(p2){
                    0->{start(MutaImagePickerActivity::class)}
                    1->{start(RxTestActivity::class)}
                    2->{start(EditTextActivity::class)}
                    3->{
                        context?.let {
                            AndPermission.with(it)
                                    .runtime()
                                    .permission(Manifest.permission.READ_PHONE_STATE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION)
                                    .onGranted { start(SelectLocationActivity::class) }
                                    .onDenied {  }
                                    .start()


                        }

                    }
                }
            }
        })
        list.add("ImageSelect")
        list.add("RxTestActivity")
        list.add("EditTextActivity")
        list.add("SelectLocationActivity")
        adapter.setNewData(list)
        adapter.notifyDataSetChanged()
    }
}