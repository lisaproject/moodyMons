package com.example.moodymons.page.weekly


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.moodymons.R
import com.example.moodymons.databinding.DiaryPerEmotionViewBinding
import com.example.moodymons.makediary.Emotion


class StatisticPerEmotionViewHolder private constructor(private val _binding:
                                                    DiaryPerEmotionViewBinding): RecyclerView
.ViewHolder(_binding.root) {

    private val emotionFace: Map<String, Int> = mapOf(
        "angry" to R.drawable.ic_angry_face_only,
        "neutral" to R.drawable.ic_neutral_face_only,
        "sad" to R.drawable.ic_sad_face_only,
        "frustration" to R.drawable.ic_frustration_face_only,
        "surprise" to R.drawable.ic_surprise_face_only,
        "happy" to R.drawable.ic_happy_face_only
    )

    private val emotionBar: Map<String, Int> = mapOf(
        "angry" to R.drawable.ic_bar_angry,
        "neutral" to R.drawable.ic_bar_neutral,
        "sad" to R.drawable.ic_bar_sad,
        "frustration" to R.drawable.ic_bar_frustration,
        "surprise" to R.drawable.ic_bar_surprise,
        "happy" to R.drawable.ic_bar_happy
    )

    companion object {
        fun from(parent: ViewGroup): StatisticPerEmotionViewHolder {
//            Log.i("StatisticPerEmotionViewHolder", "Create ViewHolder")
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DiaryPerEmotionViewBinding
                .inflate(layoutInflater, parent, false)
            return StatisticPerEmotionViewHolder(binding)
        }
    }

    fun setView(data: Pair<Float, ArrayList<Pair<String, ArrayList<Emotion>>>>,position: Int,
                itemCount: Int){
        val statisticView: ImageView = _binding.weeklyStatistic
        val faceView: ImageView = _binding.weeklyFace
        val barView: ImageView = _binding.weeklyEmotionBar
        val countView: TextView = _binding.weeklyCount
        val diaryView: ListView = _binding.weeklyDiaryList
        val recyclerItemView: ConstraintLayout = _binding.recyclerItem
        val emotion: String = data.second[0].second[0].predict!!
        emotionFace[emotion]?.let { faceView.setImageResource(it) }
        emotionBar[emotion]?.let { barView.setImageResource(it) }
        val barParams: ViewGroup.LayoutParams = barView.layoutParams
        val whiteBarParams: ViewGroup.LayoutParams = _binding.weeklyWhiteBar.layoutParams
        barParams.width = (whiteBarParams.width * data.first).toInt()
        barParams.height = whiteBarParams.height
        barView.layoutParams = barParams

        var count = 0
        for (pair in data.second)
        {
            count += pair.second.size
        }
        countView.text = count.toString()

        val adapter = DiaryPerEmotionAdapter(diaryView.context, data.second)
        diaryView.adapter = adapter

        val params: ViewGroup.MarginLayoutParams = recyclerItemView.layoutParams as ViewGroup.MarginLayoutParams

        if (position == itemCount - 1)
        {
            recyclerItemView.setBackgroundResource(R.drawable.rounded_diary_bottom)
            params.setMargins(0, 0, 0, 50)
        }
        else
        {
            recyclerItemView.setBackgroundResource(R.drawable.rounded_diary_middle)
            params.setMargins(0, 0, 0, 0)
        }

        var totalHeight = 0
        for (i in 0 until adapter.count) {
            val listItem = adapter.getView(i, null, diaryView)
            val date: TextView = listItem.findViewById(R.id.dateText)
            val diary: ListView = listItem.findViewById(R.id.diaryPerDay2)
            date.measure(0, 0)
            var listTotalHeight = date.measuredHeight
            for (j in 0 until diary.adapter.count)
            {
                val diaryItem = diary.adapter.getView(j, null, diary)
                diaryItem.measure(0, 0)
                listTotalHeight += diaryItem.measuredHeight
            }
            totalHeight += listTotalHeight + 80
        }
        statisticView.measure(0, 0)
        totalHeight += statisticView.measuredHeight + recyclerItemView.paddingBottom

        //初始狀態
        statisticView.measure(0, 0)
        params.height = statisticView.measuredHeight + recyclerItemView.paddingBottom
        recyclerItemView.layoutParams = params
        diaryView.visibility = View.GONE

        val toggle: ToggleButton = _binding.weeklyButton
        toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                diaryView.visibility = View.VISIBLE
                params.height = totalHeight
                recyclerItemView.layoutParams = params
                _binding.weeklyArrow.setImageResource(R.drawable.ic_arrow_up)
                statisticView.setImageResource(R.drawable.ic_rectangle_weekly_statistic2)
                buttonView.setBackgroundResource(R.drawable.ic_rectangle_weekly_button2)
            }
            else {
                statisticView.measure(0, 0)
                params.height = statisticView.measuredHeight + recyclerItemView.paddingBottom
                recyclerItemView.layoutParams = params
                diaryView.visibility = View.GONE
                _binding.weeklyArrow.setImageResource(R.drawable.ic_arrow_down)
                statisticView.setImageResource(R.drawable.ic_rectangle_weekly_statistic)
                buttonView.setBackgroundResource(R.drawable.ic_rectangle_weekly_button)
            }
        }


    }

}