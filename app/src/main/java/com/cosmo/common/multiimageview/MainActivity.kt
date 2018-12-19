package com.cosmo.common.multiimageview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.cosmo.common.multiimageview.rxtest.RxTestActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 *Author:ruchao.jiang
 *Created: 2018/12/19 13:19
 *Email:ruchao.jiang@uama.com.cn
 */
class MainActivity: AppCompatActivity() {
    private val adapter by lazy { MainListAdapter() }
    private val list:MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        recyclerview.addOnItemTouchListener(object :OnItemClickListener(){
            override fun SimpleOnItemClick(p0: BaseQuickAdapter<*>?, p1: View?, p2: Int) {
                when(p2){
                    0->{start(MutaImagePickerActivity::class)}
                    1->{start(RxTestActivity::class)}
                }
            }
        })
        list.add("ImageSelect")
        list.add("RxTestActivity")
        adapter.setNewData(list)
        adapter.notifyDataSetChanged()


    }
}