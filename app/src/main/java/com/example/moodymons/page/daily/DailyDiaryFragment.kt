package com.example.moodymons.page.daily

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.moodymons.databinding.FragmentDailyDiaryBinding

class DailyDiaryFragment : Fragment() {
    private var _binding : FragmentDailyDiaryBinding?=null
    private val binding get() = _binding!!
    private val viewModel: SERViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyDiaryBinding.inflate(inflater, container, false)
//        Log.i("DailyDiaryFragment", "Called ViewModelProvider.get")

        val adapter = DailyDiaryAdapter()
        binding.diaryList.adapter = adapter

        viewModel.diary.observe(viewLifecycleOwner, { newDiary ->
//            Log.i("DailyDiaryFragment", "Observe diary change")
            newDiary?.let {
                adapter.submitList(it)
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

