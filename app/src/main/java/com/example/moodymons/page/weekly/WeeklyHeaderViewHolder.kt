package com.example.moodymons.page.weekly

import android.annotation.SuppressLint
import android.os.Build

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moodymons.R
import com.example.moodymons.databinding.WeeklyHeaderViewBinding


import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class WeeklyHeaderViewHolder private constructor(private val _binding: WeeklyHeaderViewBinding):
    RecyclerView
.ViewHolder(_binding.root) {
    private lateinit var pieChart: PieChart
    private val mostEmotion = ArrayList<String>()

    companion object {
        fun from(parent: ViewGroup): WeeklyHeaderViewHolder {
//            Log.i("WeeklyHeaderViewHolder", "Create ViewHolder")
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = WeeklyHeaderViewBinding
                .inflate(layoutInflater, parent, false)
            return WeeklyHeaderViewHolder(binding)
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun setView(data: List<Pair<String, Int>>) {
        pieChart = _binding.pieChart

        val emotionColor: Map<String, Int> = mapOf(
            "angry" to R.color.angry,
            "neutral" to R.color.neutral,
            "sad" to R.color.sad,
            "frustration" to R.color.frustration,
            "surprise" to R.color.surprise,
            "happy" to R.color.happy
        )

        val emotionInChinese: Map<String, String> = mapOf(
            "angry" to "生氣",
            "neutral" to "無感",
            "sad" to "難過",
            "frustration" to "挫折",
            "surprise" to "驚訝",
            "happy" to "快樂"
        )
        //把數字放進圓餅圖
        val pieEntries = ArrayList<PieEntry>()

        for (pair in data) {
            pieEntries.add(PieEntry(pair.second.toFloat(), emotionInChinese[pair.first]))
        }

        val pieDataSet = PieDataSet(pieEntries, "")

        //設定圓餅圖各區段顏色
        val colors = ArrayList<Int>()
        for (pair in data) {
            colors.add(
                ContextCompat.getColor(pieChart.context.applicationContext,
                emotionColor[pair.first]!!
            ))
        }
        pieDataSet.colors = colors

        val pieData = PieData(pieDataSet)
        pieData.setDrawValues(false)

        pieChart.data = pieData

        //其他的style設定
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 45f
        pieChart.setUsePercentValues(false)
        pieChart.description.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.transparentCircleRadius = 0f
        pieChart.isRotationEnabled = false
        pieChart.isHighlightPerTapEnabled = true

        val pieLegend = pieChart.legend
        pieLegend.textSize = 13f
        //lineLegend.textColor = R.color.dark_gray
        pieLegend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        pieLegend.formToTextSpace = 6f
        pieLegend.xEntrySpace = 12f
        //pieLegend.maxSizePercent = 0.75f
        //pieLegend.isWordWrapEnabled = true
        pieChart.invalidate()

        //顯示統計文字
        mostEmotion.clear()
        var total = 0
        for (pair in data)
        {
            if (pair.second == data[0].second) {
                mostEmotion.add(pair.first)
            }
            total += pair.second
        }
        val mostAmount: String = String.format("%.2f", data[0].second * 100.0f / total)


        var str = "上周你最常記錄的情緒為" + emotionInChinese[mostEmotion[0]]
        if (mostEmotion.size > 1)
        {
            for (i in 1 until mostEmotion.size)
            {
                str += "、" + emotionInChinese[mostEmotion[i]]
            }

        }
        _binding.weeklyMostEmotion.text = "$str\n（佔情緒動態的$mostAmount%）"
    }
}