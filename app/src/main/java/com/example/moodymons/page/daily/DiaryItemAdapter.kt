package com.example.moodymons.page.daily

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.moodymons.R
import com.example.moodymons.makediary.Emotion

class DiaryItemAdapter(val context: Context, var diaryPerDayList: ArrayList<Emotion>):
    BaseAdapter() {

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private val emotionFace: Map<String, Int> = mapOf(
        "angry" to R.drawable.ic_angry_face,
        "neutral" to R.drawable.ic_neutral_face,
        "sad" to R.drawable.ic_sad_face,
        "frustration" to R.drawable.ic_frustration_face,
        "surprise" to R.drawable.ic_surprise_face,
        "happy" to R.drawable.ic_happy_face
    )

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
        val diaryView = inflater.inflate(R.layout.diary_item_view, parent, false)
        val image: ImageView = diaryView.findViewById(R.id.emotionImage)
        val diary: TextView = diaryView.findViewById(R.id.diaryText)

        val diaryItem = getItem(position) as Emotion
        image.setImageResource(emotionFace[diaryItem.predict]!!)
        diary.text = diaryItem.diary

        return diaryView
    }
}