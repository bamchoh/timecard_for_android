package com.timecard76a8f.myapplication

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

val workStateColor =listOf("#FD5F00", "#76B39D")
val workState = listOf("出社", "退社")

class TimeCardView : LinearLayout {
    private val illegalColor = "#DDDDDD"
    private val illegalWorkState = "***"

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        LayoutInflater.from(context).inflate(R.layout.timecard, this, true)
    }

    fun setParams(tc: TimeCard) {
        var cardColor: Int
        var textView = findViewById<TextView>(R.id.state)
        val tcTod = findViewById<TextView>(R.id.time_of_day)
        val tcDate = findViewById<TextView>(R.id.date)

        if(tc.work_idx >= 0) {
            cardColor = Color.parseColor(workStateColor[tc.work_idx])
            textView.text = workState[tc.work_idx]
            textView.setBackgroundColor(cardColor)
            var date = Date(tc.date)
            val sdf_tod = java.text.SimpleDateFormat("HH:mm", Locale.JAPAN)
            val sdf_date = java.text.SimpleDateFormat("yyyy/MM/dd(EEE)", Locale.JAPAN)
            tcTod.text = sdf_tod.format(date)
            tcTod.setBackgroundColor(cardColor)
            tcDate.text = sdf_date.format(date)
            tcDate.setBackgroundColor(cardColor)
        } else {
            cardColor = Color.parseColor(illegalColor)
            textView.text = illegalWorkState
            textView.setBackgroundColor(cardColor)
            tcTod.text = "--:--"
            tcTod.setBackgroundColor(cardColor)
            tcDate.text = "----/--/--(*)"
            tcDate.setBackgroundColor(cardColor)
        }
    }
}