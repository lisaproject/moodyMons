package com.example.moodymons.page.weekly

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.moodymons.R
import com.example.moodymons.makediary.Emotion


class DiaryPerEmotionAdapter(val context: Context, var diaryPerDayList: ArrayList<Pair<String,
        ArrayList<Emotion>>>):
    BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return  diaryPerDayList.size
    }

    override fun getItem(position: Int): Any {
        return diaryPerDayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val listView = inflater.inflate(R.layout.diary_per_day_2_view, parent, false)
        val dateView: TextView = listView.findViewById(R.id.dateText)
        val diaryView: ListView = listView.findViewById(R.id.diaryPerDay2)
        val listItemView: ConstraintLayout = listView.findViewById(R.id.listItem)

        val date: String = diaryPerDayList[position].first
        val yearStr = date.subSequence(0,4).toString()
        val monStr = date.subSequence(4,6).toString()
        val dayStr = date.subSequence(6,8).toString()
        val dateStr: String = yearStr.plus('/').plus(monStr).plus('/').plus(dayStr)
        dateView.text = dateStr

        val adapter = WeeklyPerDiaryAdapter(listView.context, diaryPerDayList[position].second)
        diaryView.adapter = adapter

        var totalHeight = 0
        val bottomPaddingHeight = 80
        for (i in 0 until adapter.count) {
            val listItem = adapter.getView(i, null, diaryView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        dateView.measure(0, 0)
        totalHeight += dateView.measuredHeight
        totalHeight += bottomPaddingHeight
        val params: ViewGroup.LayoutParams = listItemView.layoutParams
        params.height = totalHeight
        listItemView.layoutParams = params

//        Log.i("DiaryPerEmotion", "item height$position  $totalHeight")

        return listView
    }
}
