package com.example.moodymons.page.weekly

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.moodymons.R
import com.example.moodymons.databinding.FragmentWeeklyDiaryBinding
import com.example.moodymons.makediary.Emotion
import com.example.moodymons.page.daily.SERViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class WeeklyDiaryFragment : Fragment() {
    private var _binding: FragmentWeeklyDiaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SERViewModel by activityViewModels()
    private lateinit var adapter: WeeklyDiaryAdapter


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeeklyDiaryBinding.inflate(inflater, container, false)
        adapter = WeeklyDiaryAdapter()
        binding.weeklyContent.adapter = adapter

        //設定下拉式(spinner)選單
        val time: Time = setSpinner()
        var startDate: String = time.filterTime[0]
        val spinnerAdapter = ArrayAdapter(this.requireContext(), R.layout
        .spinner_item, time.spinnerTime)
        binding.weeklySpinner.adapter = spinnerAdapter

        //偵測viewModel diary的變動
        var diary: ArrayList<Pair<String, ArrayList<Emotion>>> = arrayListOf()
        viewModel.diary.observe(viewLifecycleOwner, { newDiary ->
//            Log.i("WeeklyDiaryFragment", "Observe diary change")
            newDiary?.let {
                diary = it
                val dataList: ArrayList<DataItem>? = setWeeklyData(it, startDate)
                if (dataList == null)
                {
                    binding.weeklyContent.visibility = View.GONE
                    binding.noDiary.visibility = View.VISIBLE
                }
                else
                {
                    binding.weeklyContent.visibility = View.VISIBLE
                    binding.noDiary.visibility = View.GONE
                    adapter.submitList(dataList)
                }
            }
        })

        //偵測spinner的變動
        binding.weeklySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                startDate = time.filterTime[position]
                val dataList: ArrayList<DataItem>? = setWeeklyData(diary, startDate)
                if (dataList == null)
                {
                    binding.weeklyContent.visibility = View.GONE
                    binding.noDiary.visibility = View.VISIBLE
                }
                else
                {
                    binding.weeklyContent.visibility = View.VISIBLE
                    binding.noDiary.visibility = View.GONE
                    adapter.submitList(dataList)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setWeeklyData(diary: ArrayList<Pair<String, ArrayList<Emotion>>>, startDate:String):
            ArrayList<DataItem>? {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val startParsed = LocalDate.parse(startDate, formatter)
        val endParsed = startParsed.plusWeeks(1)
        val diaryInWeek = diary.filter {
            val testDate = LocalDate.parse(it.first, formatter)
            testDate >= startParsed && testDate < endParsed
        }

        if (diaryInWeek.isEmpty())
        {
            return null
        }
        else {
            val emotionMap: MutableMap<String, ArrayList<Pair<String, ArrayList<Emotion>>>> =
                mutableMapOf()
            val emotionName: ArrayList<String> = arrayListOf(
                "neutral", "angry", "sad", "frustration",
                "surprise", "happy"
            )
            val chartData: ArrayList<Pair<String, Int>> = arrayListOf()

            for (emotion in emotionName) {
                var count = 0
                val data: ArrayList<Pair<String, ArrayList<Emotion>>> = arrayListOf()
                for (pair in diaryInWeek)
                {
                    val diaryPerDay: ArrayList<Emotion> = arrayListOf()
                    for (element in pair.second)
                    {
                        if (element.predict == emotion)
                        {
                            diaryPerDay.add(element)
                        }
                    }
                    if (diaryPerDay.isNotEmpty())
                    {
                        data.add(Pair(pair.first, diaryPerDay))
                        count += diaryPerDay.size
                    }
                }
                if (count != 0) {
                    chartData.add(Pair(emotion, count))
                    emotionMap[emotion] = data
                }
            }

            val sortedData =
                chartData.toList().sortedByDescending { (_, value) -> value }
            var sum = 0
            for (item in sortedData) {
                sum += item.second
            }
            val weeklyData: ArrayList<DataItem> = arrayListOf()
            weeklyData.add(DataItem.ChartData(sortedData))
            val index = 0
            for (item in sortedData) {
                val num = item.second.toFloat().div(sum)
                val diaryArrayList: ArrayList<Pair<String, ArrayList<Emotion>>> = arrayListOf()
                for (pair in emotionMap[item.first]!!)
                {
                    val emotionArrayList: ArrayList<Emotion> = arrayListOf()
                    emotionArrayList.addAll(pair.second)
                    diaryArrayList.add(Pair(pair.first, emotionArrayList))
                }

                weeklyData.add(
                    DataItem.DiaryData(
                        Pair(num, diaryArrayList), index
                    )
                )
                index.plus(1)
            }
            return weeklyData
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setSpinner(): Time {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val formatter2 = DateTimeFormatter.ofPattern("MM/dd")
        val today = viewModel.getToday()
        val todayParsed = LocalDate.parse(today, formatter)
        val thisWeekParsed = todayParsed.minusDays(6)
        var dateCount = todayParsed.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

        val spinnerArray: ArrayList<String> = arrayListOf()
        val filterArray: ArrayList<String> = arrayListOf()
        spinnerArray.add("本周")
        filterArray.add(thisWeekParsed.format(formatter))
        for (i in 0..8)
        {
            var dateItem = ""
            dateCount = dateCount.minusDays(1)
            dateItem = dateCount.format(formatter2)
            dateCount = dateCount.minusDays(6)
            dateItem = dateCount.format(formatter2) + "~" + dateItem
            spinnerArray.add(dateItem)
            filterArray.add(dateCount.format(formatter))
        }

        return Time(spinnerArray, filterArray)
    }
}