package com.timecard76a8f.myapplication

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

data class TimeCard(val work_idx: Int=-1, val date: Long=-1)
