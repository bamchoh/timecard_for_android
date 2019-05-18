package com.timecard76a8f.myapplication

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*

class TimeCardAdapter(val context: Context, val timecardShelf: List<TimeCardSet>) : BaseAdapter() {
    var inflater: LayoutInflater
    val timecards = timecardShelf

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return timecards.count()
    }

    override fun getItem(position: Int): Any {
        return timecards[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val i = position
        var convertView = inflater.inflate(R.layout.timecard_set,parent,false)

        var startTimeCard = convertView.findViewById<TimeCardView>(R.id.start)
        startTimeCard.setParams(timecards[i].start)

        var endTimeCard = convertView.findViewById<TimeCardView>(R.id.end)
        endTimeCard.setParams(timecards[i].end)

        return convertView
    }
}