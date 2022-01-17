package com.example.moodymons.page.daily

import android.annotation.SuppressLint
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moodymons.makediary.Emotion


class DailyDiaryAdapter: ListAdapter<Pair<String, ArrayList<Emotion>>,
        RecyclerView.ViewHolder>(DailyDiaryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return DiaryPerDayViewHolder.from(parent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DiaryPerDayViewHolder -> {
                val item = getItem(position)
                holder.setView(item)
            }
        }
    }
}

class DailyDiaryDiffCallback : DiffUtil.ItemCallback<Pair<String, ArrayList<Emotion>>>() {
    override fun areItemsTheSame(
        oldItem: Pair<String, ArrayList<Emotion>>,
        newItem: Pair<String, ArrayList<Emotion>>
    ): Boolean {
        return oldItem.first == newItem.first
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: Pair<String, ArrayList<Emotion>>,
        newItem: Pair<String, ArrayList<Emotion>>
    ): Boolean {
        return oldItem == newItem
    }
}