package com.example.moodymons.viewpager

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import com.example.moodymons.R
import com.example.moodymons.databinding.FragmentViewPagerBinding
import com.example.moodymons.page.InformationFragment
import com.example.moodymons.page.PredictFragment
import com.example.moodymons.page.SpecPredictFragment
import com.example.moodymons.page.daily.DailyDiaryFragment
import com.example.moodymons.page.monthly.MonthlyDiaryFragment
import com.example.moodymons.page.weekly.WeeklyDiaryFragment
import com.google.android.material.tabs.TabLayoutMediator


class ViewPagerFragment : Fragment() {
    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewPagerBinding.inflate(inflater, container, false)
        val view = binding.root

        val fragmentList = arrayListOf<Fragment>(
            //RecordingFragment(),
            //PredictFragment(),
            SpecPredictFragment(),
            DailyDiaryFragment(),
            WeeklyDiaryFragment(),
            MonthlyDiaryFragment(),
            InformationFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        TabLayoutMediator(tabLayout,viewPager){ tab,position ->
            when (position) {
                0 -> {
                    tab.text = "首頁"
                    tab.icon = getDrawable(requireContext(),R.drawable.ic_tab_home)
                }
                1 -> {
                    tab.text = "日記"
                    tab.icon = getDrawable(requireContext(),R.drawable.ic_tab_diarylist)
                }
                2 -> {
                    tab.text = "周分析"
                    tab.icon = getDrawable(requireContext(),R.drawable.ic_tab_piechart)
                }
                3 -> {
                    tab.text = "月分析"
                    tab.icon = getDrawable(requireContext(),R.drawable.ic_tab_trendline)
                }
                4 -> {
                    tab.text = "諮詢專線"
                    tab.icon = getDrawable(requireContext(),R.drawable.ic_tab_telephone)
                }
            }
        }.attach()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}