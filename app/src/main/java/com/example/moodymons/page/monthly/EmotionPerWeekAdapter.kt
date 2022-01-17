package com.example.moodymons.page.monthly

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.moodymons.R

class EmotionPerWeekAdapter(val context: Context, private var emotionPerWeekList:
ArrayList<Pair<String,
        String>>):
    BaseAdapter() {

    private val emotionFace: Map<String, Int> = mapOf(
        "angry" to R.drawable.ic_angry_face_colored,
        "neutral" to R.drawable.ic_neutral_face_colored,
        "sad" to R.drawable.ic_sad_face_colored,
        "frustration" to R.drawable.ic_frustration_face_colored,
        "surprise" to R.drawable.ic_surprise_face_colored,
        "happy" to R.drawable.ic_happy_face_colored
    )

    private val emotionBackground: Map<String, Int> = mapOf(
        "angry" to R.drawable.ic_rectangle_angry,
        "neutral" to R.drawable.ic_rectangle_neutral,
        "sad" to R.drawable.ic_rectangle_sad,
        "frustration" to R.drawable.ic_rectangle_frustration,
        "surprise" to R.drawable.ic_rectangle_surprise,
        "happy" to R.drawable.ic_rectangle_happy
    )

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return  emotionPerWeekList.size
    }

    override fun getItem(position: Int): Any {
        return emotionPerWeekList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val listItemView = inflater.inflate(R.layout.emotion_per_week_view, parent, false)
        val bgView: ImageView = listItemView.findViewById(R.id.emotionBackground)
        val dateView: TextView = listItemView.findViewById(R.id.emotionDate)
        val faceView: ImageView = listItemView.findViewById(R.id.emotionFace)

        val emotion: String = emotionPerWeekList[position].second
        emotionBackground[emotion]?.let { bgView.setImageResource(it)}
        emotionFace[emotion]?.let { faceView.setImageResource(it) }
        dateView.text = emotionPerWeekList[position].first
        return listItemView
    }
}