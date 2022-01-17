package com.example.moodymons.page.weekly

import android.annotation.SuppressLint
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moodymons.makediary.Emotion


private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class WeeklyDiaryAdapter: ListAdapter<DataItem,
        RecyclerView.ViewHolder>(WeeklyDiaryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> WeeklyHeaderViewHolder.from(parent)
            ITEM_VIEW_TYPE_ITEM -> StatisticPerEmotionViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is WeeklyHeaderViewHolder -> {
                val item = getItem(position) as DataItem.ChartData
                holder.setView(item.chartData) }
            is StatisticPerEmotionViewHolder -> {
                val item = getItem(position) as DataItem.DiaryData
                holder.setView(item.diaryData, position, itemCount)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.ChartData -> ITEM_VIEW_TYPE_HEADER
            is DataItem.DiaryData -> ITEM_VIEW_TYPE_ITEM
            else -> -1
        }
    }

}

class WeeklyDiaryDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(
        oldItem: DataItem,
        newItem: DataItem
    ): Boolean {
        return oldItem.id == newItem.id
    }
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: DataItem,
        newItem: DataItem
    ): Boolean {
        return oldItem == newItem
    }
}


sealed class DataItem {
    abstract val id: Long

    data class ChartData(val chartData: List<Pair<String, Int>>): DataItem() {
        override val id = Long.MIN_VALUE
    }

    data class DiaryData(val diaryData: Pair<Float, ArrayList<Pair<String, ArrayList<Emotion>>>>,
                         val index: Int):
        DataItem
        () {
        override val id: Long = index.toLong()
    }
}