package com.cosmo.library.widget

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.cosmo.library.R

/**
 * Author:ruchao.jiang
 * Created: 2018/12/21 09:11
 * Email:ruchao.jiang@uama.com.cn
 */
class ExtensionEditText : LinearLayout {
    private var line: View? = null
    private var leftImg: ImageView? = null
    private var rightImg: ImageView? = null
    private var editText: EditText? = null
    private var passwordVisiable = false

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }


    private fun init(context: Context, attrs: AttributeSet?) {
        val view = View.inflate(context, R.layout.widget_extension_edittext, null)
        val a = context.obtainStyledAttributes(attrs, R.styleable.ExtensionEditText)
        try{
        line = view.findViewById(R.id.line)
        leftImg = view.findViewById(R.id.leftImg)
        rightImg = view.findViewById(R.id.rightImg)
        editText = view.findViewById(R.id.inputView)
        val hasLine = a.getBoolean(R.styleable.ExtensionEditText__ExtensionEditTextHasLine, true)
        val lineColor = a.getColor(R.styleable.ExtensionEditText__ExtensionEditTextLineColor, Color.parseColor("#dadada"))
        val pwdType = a.getBoolean(R.styleable.ExtensionEditText__ExtensionEditTextPwdType, false)
        val leftImgRes = a.getResourceId(R.styleable.ExtensionEditText__ExtensionEditTextLeftImg, 0)
        val rightImgRes = a.getResourceId(R.styleable.ExtensionEditText__ExtensionEditTextRightImg, 0)
        val hintText = a.getString(R.styleable.ExtensionEditText__ExtensionEditTextHintText)
        val textColor = a.getColor(R.styleable.ExtensionEditText__ExtensionEditTextColor, Color.parseColor("#333333"))
        val textSize = a.getDimension(R.styleable.ExtensionEditText__ExtensionEditTextSize,16.0f)

        val pwdVisibleImg = a.getResourceId(R.styleable.ExtensionEditText__ExtensionEditTextPwdVisibleImg, 0)
        val pwdInVisibleImg = a.getResourceId(R.styleable.ExtensionEditText__ExtensionEditTextPwdVisibleImg, 0)

        line?.setBackgroundColor(lineColor)
        leftImg?.setImageResource(leftImgRes)
        rightImg?.setImageResource(rightImgRes)
        editText?.hint = hintText
        editText?.setTextColor(textColor)
        editText?.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize)
        line?.visibility = if (hasLine) View.VISIBLE else View.GONE
        rightImg?.visibility = View.GONE

        if (pwdType) {
            rightImg?.visibility = View.VISIBLE
            rightImg?.setImageResource(pwdInVisibleImg)
            editText?.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            editText?.inputType = InputType.TYPE_CLASS_TEXT
        }


        rightImg?.setOnClickListener {
            if (pwdType) {
                passwordVisiable = !passwordVisiable
                if (passwordVisiable){
                    rightImg?.setImageResource(pwdVisibleImg)
                    editText?.let {
                        it.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        it.setSelection(it.length())
                    }

                }else{
                    rightImg?.setImageResource(pwdInVisibleImg)
                    editText?.let {
                        it.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        it.setSelection(it.length())
                    }
                }
            } else {
                clearText()
            }
        }


        editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                val length = editable.toString().length
                if (pwdType || length > 0){
                    rightImg?.visibility = View.VISIBLE
                }else{
                    rightImg?.visibility = View.GONE
                }
            }
        })

        }catch (e :Exception){
            e.printStackTrace()
        }finally {
            a.recycle()
        }
        this.addView(view)
    }




    private fun clearText() {
        editText?.setText("")
    }


}
