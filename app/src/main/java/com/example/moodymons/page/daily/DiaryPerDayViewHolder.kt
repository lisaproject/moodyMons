package com.example.moodymons.page.daily

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.moodymons.databinding.DiaryPerDayViewBinding
import com.example.moodymons.makediary.Emotion

class DiaryPerDayViewHolder private constructor(private val _binding: DiaryPerDayViewBinding): RecyclerView
.ViewHolder(_binding.root) {

    companion object {
        fun from(parent: ViewGroup): DiaryPerDayViewHolder {
//            Log.i("DiaryPerDayViewHolder", "Create ViewHolder")
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DiaryPerDayViewBinding
                .inflate(layoutInflater, parent, false)
            return DiaryPerDayViewHolder(binding)
        }
    }

    fun setView(data: Pair<String, ArrayList<Emotion>>){
        val dateView: TextView = _binding.dateText
        val listView: ListView = _binding.diaryPerDay
        val recyclerItemView: ConstraintLayout = _binding.recyclerItem
        val yearStr = data.first.subSequence(0,4).toString()
        val monStr = data.first.subSequence(4,6).toString()
        val dayStr = data.first.subSequence(6,8).toString()
        val dateStr: String = yearStr.plus('/').plus(monStr).plus('/').plus(dayStr)
        dateView.text = dateStr
        val adapter = DiaryItemAdapter(listView.context, data.second)
        listView.adapter = adapter

        var totalHeight = 0
        val bottomPaddingHeight = 110
        for (i in 0 until adapter.count) {
            val listItem = adapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        dateView.measure(0, 0)
        totalHeight += dateView.measuredHeight
        totalHeight += bottomPaddingHeight
        val params: ViewGroup.LayoutParams = recyclerItemView.layoutParams
        params.height = totalHeight
        recyclerItemView.layoutParams = params
    }

}