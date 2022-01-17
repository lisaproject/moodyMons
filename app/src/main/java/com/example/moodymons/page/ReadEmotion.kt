package com.example.moodymons.page

import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.moodymons.databinding.FragmentReadDataBinding
import com.example.moodymons.makediary.Emotion
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.nield.kotlinstatistics.countBy
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReadEmotion : Fragment() {
    private var _binding: FragmentReadDataBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadDataBinding.inflate(inflater, container, false)
        binding.readdataBtn.setOnClickListener {
            val userID: String = binding.etuserID.text.toString()
            val date: String = binding.etdate.text.toString()
//            val num: String = binding.etnum.text.toString()
            if (userID.isNotEmpty()) {
                readData(userID, date)

            } else {

                Toast.makeText(requireActivity(),"PLease enter the Username", Toast.LENGTH_SHORT).show()

            }

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun readData(userID: String, date: String) {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val dateConverted = LocalDate.parse(date, formatter)
        val date0Converted : LocalDate = dateConverted.minusDays(6)
        val date0 : String = date0Converted.format(formatter)
//        binding.etuserID.text.clear()
//        binding.etdate.text.clear()
//        binding.etnum.text.clear()
        binding.showpredict.text = date0
        binding.showfeedback.text = date


        database = FirebaseDatabase.getInstance().getReference("Emotions")
        database.child(userID).orderByKey().startAt(date0).endAt(date).get().addOnSuccessListener { snapshot ->

            if (snapshot.exists()) {
                val emotionList = ArrayList<Emotion>()

                val children = snapshot.children
                for(dateKey in children)
                {
                    val emoChildren = dateKey.children
                    for(emotionKey in emoChildren)
                    {
                        val emotionObject = emotionKey.getValue(Emotion::class.java)
                        if (emotionObject != null) {
                            emotionList.add(emotionObject)
                        }
                    }
                }

                val countOfEmotion = emotionList.countBy{it.predict.toString()}

                var statistic = ""
                countOfEmotion.entries.forEach{ statistic = statistic + it.key + it.value + "\n"}

                binding.showdiary.text = statistic

//                binding.showdiary.text = it.child(date0).child("001").child("diary").value.toString()
//                val predict = it.child("predict").value
//                val feedback = it.child("feedback").value
//                val diary = it.child("diary").value
//                Toast.makeText(activity?.applicationContext,"Successfully Read",Toast.LENGTH_SHORT).show()
//                binding.etuserID.text.clear()
//                binding.etdate.text.clear()
//                binding.etnum.text.clear()
//                binding.showpredict.text = predict.toString()
//                binding.showfeedback.text = feedback.toString()
//                binding.showdiary.text = diary.toString()
//
            } else {
                Toast.makeText(requireActivity(), "Emotion Doesn't Exist", Toast.LENGTH_SHORT).show()

            }

        }.addOnFailureListener{

            Toast.makeText(requireActivity(),"Failed",Toast.LENGTH_SHORT).show()

        }

    }
}