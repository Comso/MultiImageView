package com.cosmo.common.multiimageview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.TypedArray
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.lvman.uamautil.permission.PermissionUtils
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy


/**
 * Author:ruchao.jiang
 * Created: 2018/8/13 13:58
 * Email:ruchao.jiang@uama.com.cn
 */
open class MutableImagePicker : LinearLayout {
    // columns之间的间距
    private var pickerSpacing:Int = 0
    // columns一行显示的数量
    private var pickerNumColumns:Int = 0
    // 占位图1
    private var pickerPlaceHolderOne:Int =0
    // 占位图2
    private var pickerPlaceHolderTwo:Int =0
    private var pickerMaxNum:Int = 0
    private var pickerShowAllSpan = true
    private var recyclerView: RecyclerView
    private val mContext:Context
    private val list:MutableList<Any> = ArrayList()
    private val adapter by lazy { MutableImagePickerAdapter() }
    var imagePickerItemClickListener:ImagePickerItemClickListener? = null



    @SuppressLint("Recycle")
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        recyclerView  = View.inflate(context,R.layout.widget_mutable_image_picker,null) as RecyclerView
        val a:TypedArray = context.obtainStyledAttributes(attrs,R.styleable.MutableImagePicker)
        pickerSpacing = a.getDimensionPixelSize(R.styleable.MutableImagePicker_pickerSpacing,dpToPx(mContext,15.0f))
        pickerNumColumns = a.getInteger(R.styleable.MutableImagePicker_pickerNumColumns,4)
        pickerMaxNum = a.getInteger(R.styleable.MutableImagePicker_pickerMaxNum,4)
        pickerShowAllSpan = a.getBoolean(R.styleable.MutableImagePicker_pickerShowAllSpan,true)
        pickerPlaceHolderOne = a.getResourceId(R.styleable.MutableImagePicker_pickerPlaceHolderOne,0)
        pickerPlaceHolderTwo = a.getResourceId(R.styleable.MutableImagePicker_pickerPlaceHolderTwo,0)
        if (pickerPlaceHolderOne == 0 && pickerPlaceHolderTwo == 0){
            pickerPlaceHolderOne = R.drawable.mutable_placeholder_two
        }
        initRoot()
        initList()
        setAdapter()
    }

    private fun initRoot(){
        addView(recyclerView)
        recyclerView.addItemDecoration(SpacesItemDecoration(pickerNumColumns,pickerSpacing,true))
    }



        private fun setAdapter(){
        recyclerView.layoutManager = GridLayoutManager(mContext,pickerNumColumns)
        adapter.setNewData(list)
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
         recyclerView.addOnItemTouchListener(object :OnItemClickListener(){
             override fun SimpleOnItemClick(adapte: BaseQuickAdapter<*>, view: View, position: Int) {
                 PermissionUtils.checkCameraPermission(context,""){
                     Matisse.from(context as Activity)
                             .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                             .countable(true)
                             .capture(true)
                             .captureStrategy(CaptureStrategy(true,"com.cosmo.common.multiimageview.fileprovider"))
                             .maxSelectable(5)//可选的最大数
                             .imageEngine(GlideEngine())//图片加载引擎
                             .forResult(100)

                 }
             }
         })
    }

    private fun initList(){
        when(pickerShowAllSpan){
            true->{
                for (index in 0 until  pickerMaxNum){
                    list.add( if (index==0) pickerPlaceHolderOne else pickerPlaceHolderTwo)
                }
            }
            else->{
                if (pickerPlaceHolderOne != 0) list.add(pickerPlaceHolderOne)
                if (pickerPlaceHolderTwo != 0) list.add(pickerPlaceHolderTwo)
            }
        }
    }

    private  fun dpToPx(context: Context, dp: Float): Int {
        return (context.resources.displayMetrics.density * dp + 0.5f).toInt()
    }

}


