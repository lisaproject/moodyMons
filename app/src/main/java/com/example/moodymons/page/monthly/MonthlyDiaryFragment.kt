package com.example.moodymons.page.monthly

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.moodymons.R
import com.example.moodymons.databinding.FragmentMonthlyDiaryBinding
import com.example.moodymons.makediary.Emotion
import com.example.moodymons.page.daily.SERViewModel
import com.example.moodymons.page.weekly.Time
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import org.nield.kotlinstatistics.countBy
import org.nield.kotlinstatistics.sumBy
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
class MonthlyDiaryFragment : Fragment() {
    private var _binding: FragmentMonthlyDiaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var lineChart: LineChart
    private val viewModel: SERViewModel by activityViewModels()
    private lateinit var adapter: EmotionPerWeekAdapter

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private val formatter2: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyDiaryBinding.inflate(inflater, container, false)
        lineChart = binding.lineChart

        //設定下拉式(spinner)選單
        val time: Time = setSpinner()
        var startDate: String = time.filterTime[0]
        val spinnerAdapter = ArrayAdapter(this.requireContext(), R.layout
            .spinner_item2, time.spinnerTime)
        binding.monthlySpinner.adapter = spinnerAdapter

        //偵測viewModel diary的變動
        var diary: ArrayList<Pair<String, ArrayList<Emotion>>> = arrayListOf()
        viewModel.diary.observe(viewLifecycleOwner, { newDiary ->
            diary = newDiary
//            Log.i("MonthlyDiaryFragment", "Observe diary change")
            binding.monthlyContent.visibility = View.VISIBLE
            binding.noDiary.visibility = View.GONE
            setMonthlyData(newDiary, startDate)
        })

        //偵測spinner的變動
        binding.monthlySpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.monthlyContent.visibility = View.VISIBLE
                binding.noDiary.visibility = View.GONE
                startDate = time.filterTime[position]
                setMonthlyData(diary, startDate)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun setMonthlyData(diaryList: ArrayList<Pair<String, ArrayList<Emotion>>>, startDate:
    String) {
        val startParsed = LocalDate.parse(startDate, formatter)
        val endParsed = startParsed.plusMonths(1)
        var dateCount = startParsed
        val dateArray = ArrayList<Int>()
        var weekNum = 0
        val emotionList = ArrayList<ArrayList<Emotion>>()
        val chartInput = ArrayList<MutableMap<String, Int>>()

        val emotionColor: MutableMap<String, Int> = mutableMapOf(
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


        //在 dateArray 加入各周日期
        while (dateCount <= endParsed) {
            weekNum = weekNum.plus(1)
            dateArray.add(dateCount.format(formatter).toInt())
            dateCount = dateCount.plusDays(6)
            if (dateCount <= endParsed)
            {
                dateArray.add(dateCount.format(formatter).toInt())
            }
            else
            {
                dateArray.add(endParsed.format(formatter).toInt())
            }
            dateCount = dateCount.plusDays(1)
        }
//        Log.i("MonthlyDiaryFragment", weekNum.toString())

        for(i in 0 until weekNum)
        {
            emotionList.add(ArrayList())
            chartInput.add(mutableMapOf(
                "angry" to 0,
                "neutral" to 0,
                "sad" to 0,
                "frustration" to 0,
                "surprise" to 0,
                "happy" to 0
            ))
        }

        //取出當個月的日記
        val diaryInMonth = diaryList.filter {
            val testDate = LocalDate.parse(it.first, formatter)
            testDate in startParsed..endParsed
        }

        //若當月沒有日記
        if (diaryInMonth.isEmpty())
        {
            binding.monthlyContent.visibility = View.GONE
            binding.noDiary.visibility = View.VISIBLE
        }

        diaryInMonth.forEach { item->
            var week = 0

            for (i in 0..dateArray.size-2 step 2)
            {
                if (item.first.toInt() in dateArray[i]..dateArray[i+1])
                {
                    week = i / 2
                    break
                }
            }
            for (emotion in item.second)
            {
                emotionList[week].add(emotion)
            }
        }

        //統計每週的情緒
        for (i in 0 until weekNum) {
            val countOfWeek = emotionList[i].countBy { it.predict.toString() }
            countOfWeek.entries.forEach {
                chartInput[i][it.key] = it.value
            }
        }

        //把資料放進折線圖
        val dataSetS = ArrayList<ILineDataSet>()
        //按情緒一一放入
        emotionInChinese.entries.forEach {
            val lineEntry = ArrayList<Entry>()
            for (week in 0 until weekNum) {
                lineEntry.add(Entry(week.toFloat(), chartInput[week][it.key]!!.toFloat()))
            }

            val lineDataSet = LineDataSet(lineEntry, emotionInChinese[it.key])

            //line dataset style
            lineDataSet.color = ContextCompat.getColor(
                requireContext().applicationContext,
                emotionColor[it.key]!!
            )
            lineDataSet.setCircleColor(
                ContextCompat.getColor(
                    requireContext().applicationContext,
                    emotionColor[it.key]!!
                )
            )
            lineDataSet.setDrawCircleHole(false)
            lineDataSet.circleRadius = 4f
            lineDataSet.lineWidth = 5f
            lineDataSet.setDrawValues(false)
            lineDataSet.axisDependency = YAxis.AxisDependency.LEFT

            dataSetS.add(lineDataSet)
        }

        val lineData = LineData(dataSetS)
        lineChart.data = lineData
        setLineChartStyle(dateArray)

        lineChart.invalidate()

        //圖表下的每週情緒統計
        val emotionPerWeek: ArrayList<Pair<String, String>> = arrayListOf()
        val listView: ListView = binding.mostEmotionList
        var totalHeight = listView.paddingTop + listView.paddingBottom
        val dateArraySize = dateArray.size - 1
        for (i in 0 until weekNum)
        {
            val sortedCount = chartInput[weekNum - 1 - i].toList().sortedByDescending { (_, value)
                -> value}
            if (sortedCount[0].second > 0) {
                val date0 = intToDate(dateArray[dateArraySize - 1 - 2 * i])
                val date1 = intToDate(dateArray[dateArraySize - 2 * i])
                val dateOfWeek = "$date0 ~ $date1"
                val emotion: String = sortedCount[0].first
                emotionPerWeek.add(Pair(dateOfWeek, emotion))
            }
        }
        adapter = EmotionPerWeekAdapter(this.requireContext(), emotionPerWeek)
        listView.adapter = adapter
        for (i in 0 until adapter.count)
        {
            val listItem = adapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params: ViewGroup.LayoutParams = listView.layoutParams
        params.height = totalHeight
        listView.layoutParams = params


        //把各週的情緒統計編成一個清單
        var emotionTotal: Sequence<Map.Entry<String, Int>> = chartInput[0]
            .asSequence()
        for (week in 1 until weekNum)
        {
            emotionTotal += chartInput[week].asSequence()
        }
        //整月的統計
        val countOfMonth = emotionTotal.sumBy(keySelector = { it.key }, intSelector
        = { it.value } )
        val sortedCount = countOfMonth.toList().sortedByDescending { (_, value) -> value}

        val mostEmotion = ArrayList<String>()
        for (pair in sortedCount)
        {
            if (pair.second == sortedCount[0].second) {
                mostEmotion.add(pair.first)
            }
        }
        //統計結果的顯示
        var str = "本月你最常紀錄的是\n" + emotionInChinese[mostEmotion[0]]
        for (i in 1 until mostEmotion.size)
        {
            str += "、" + emotionInChinese[mostEmotion[i]]
        }
        binding.monthlyMostEmotion.text = str + "的情緒動態"
    }

    private fun setLineChartStyle(dateArray: ArrayList<Int>) {
        //line chart style
        lineChart.setBorderWidth(2f)
        lineChart.setBorderColor(R.color.dark_gray)
        lineChart.description.isEnabled = false
        lineChart.extraBottomOffset = 16f

        //XAxis
        val xaxis = lineChart.xAxis
        xaxis.granularity = 1f
        xaxis.valueFormatter = MyXAxisFormatter(dateArray)
        xaxis.position = XAxis.XAxisPosition.BOTTOM
        xaxis.textSize = 15f
        xaxis.setDrawGridLines(false)
        xaxis.setDrawAxisLine(true)

        //YAxis
        val yaxisLeft = lineChart.axisLeft
        yaxisLeft.granularity = 1f
        yaxisLeft.axisMinimum = 0f

        val yaxisRight = lineChart.axisRight
        yaxisRight.isEnabled = true
        yaxisRight.setDrawAxisLine(false)
        yaxisRight.setDrawGridLines(false)
        yaxisRight.textColor = Color.TRANSPARENT
        yaxisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        yaxisRight.xOffset = 10f

        //legend(折線圖下的圖例)
        val lineLegend = lineChart.legend
        lineLegend.textSize = 13f
        //lineLegend.textColor = R.color.dark_gray
        lineLegend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        lineLegend.formToTextSpace = 8f
        lineLegend.xEntrySpace = 20f
        lineLegend.maxSizePercent = 0.70f
        lineLegend.isWordWrapEnabled = true
    }

    class MyXAxisFormatter(private val date : ArrayList<Int>) : ValueFormatter() {
//        private val formatterTW = SimpleDateFormat("MMMd", Locale.TAIWAN)
//        @SuppressLint("SimpleDateFormat")
//        private val formatter = SimpleDateFormat("yyyyMMdd")
        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        private val formatter3: DateTimeFormatter = DateTimeFormatter.ofPattern("M/d")

        private val weekDate = ArrayList<String>()

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            for (i in 0 until date.size step 2)
            {
                val dateInFormat = LocalDate.parse(date[i].toString(), formatter)
                weekDate.add(dateInFormat.format(formatter3))
            }
            return weekDate.getOrNull(value.toInt()) ?: value.toString()
        }
    }

    private fun setSpinner(): Time {
        val today = viewModel.getToday()
        val todayParsed = LocalDate.parse(today, formatter)
        val thisMonthParsed = todayParsed.minusMonths(1).plusDays(1)
        val day1Parsed = todayParsed.withDayOfMonth(1)
        var dateCount = day1Parsed

        val spinnerArray: ArrayList<String> = arrayListOf()
        val filterArray: ArrayList<String> = arrayListOf()
        spinnerArray.add("本月")
        filterArray.add(thisMonthParsed.format((formatter)))
        for (i in 0..11)
        {
            dateCount = dateCount.minusMonths(1)
            spinnerArray.add(dateCount.monthValue.toString() + "月")
            filterArray.add(dateCount.format(formatter))
        }

        return Time(spinnerArray, filterArray)
    }

    private fun intToDate(date: Int): String {
        return LocalDate.parse(date.toString(), formatter).format(formatter2)
    }
}




