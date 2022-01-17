package com.example.moodymons.makediary

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.example.moodymons.R
import com.example.moodymons.databinding.FrgmentMakeDiaryBinding
import com.example.moodymons.page.daily.SERViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


class MakeDiaryFragment:  Fragment(){
    private val args: MakeDiaryFragmentArgs by navArgs()
    private var _binding: FrgmentMakeDiaryBinding? = null
    private val binding get() = _binding!!
    private lateinit var database : DatabaseReference
    private val viewModel: SERViewModel by activityViewModels()
    private var EngFeedback: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FrgmentMakeDiaryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val navController = NavHostFragment.findNavController(this)
        val confidenceArr = args.confidenceArr
        val label = args.label
        EngFeedback = args.label
        changeViewByEmo(label, binding)
        var singleChoiceIndex:AtomicInteger = AtomicInteger(labelNum(label))
        // Set the focus to the edit text.
        binding.diaryEditText.requestFocus()
        // Show the keyboard.
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.diaryEditText, 0)
        binding.imgChange.setOnClickListener{
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            userFeedBackPopUp(singleChoiceIndex)
        }
        binding.btnDone.setOnClickListener {
            database = FirebaseDatabase.getInstance().getReference("Emotions")
            val userID = viewModel.getUID()
            val date = binding.dateAndTime.text.toString()
            val diary = binding.diaryEditText.text.toString()
            var confidenceList: MutableList<Float>  = mutableListOf()
            for(item in confidenceArr){
                confidenceList.add(item)
            }
//            var confidenceArrStr = ""
//            for(item in confidenceArr){
//                confidenceArrStr = "$confidenceArrStr,$item"
//            }
//            confidenceArrStr = confidenceArrStr.subSequence(1,confidenceArrStr.length).toString()
            val emotion = Emotion(label,EngFeedback,diary,confidenceList)
            val postRef = database.child(userID).child(date)
            val newRef = postRef.push()
            newRef.setValue(emotion).addOnSuccessListener {
                Toast.makeText(activity?.applicationContext,"moodyMons收到！",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener{
                Toast.makeText(activity?.applicationContext,"Failed",Toast.LENGTH_SHORT).show()
            }
            //make csv
//            Thread {
//                var csvText: ByteArray = byteArrayOf()
//                var text = ""
//                //audio data
//                for (i in audioData.indices) {
//                    if (i.mod(16000) == 0)
//                    {
//                        csvText += text.toByteArray()
//                        text = ""
//                    }
//                    text += audioData[i].toString() + "\n"
//                }
//                csvText += text.toByteArray()
////                Log.i("makeDiaryFragment", "audioDataSize: " + audioData.size.toString())
////                Log.i("makeDiaryFragment", "csvTextSize: " + csvText.size.toString())
//
//                //upload to firebase storage
//                val keyName = newRef.key.toString()
//                val storage = Firebase.storage
//                val storageRef = storage.reference.child(userID).child(date).child(keyName)
//                val audioRef = storageRef.child("audio.csv")
//                val uploadTaskA = audioRef.putBytes(csvText)
//                uploadTaskA.addOnFailureListener {
//                    Toast.makeText(requireContext(), "upload audio failed", Toast.LENGTH_SHORT)
//                        .show()
//                }.addOnSuccessListener {
//                    Toast.makeText(requireContext(), "upload audio succeed", Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//                //confidence array
//                csvText = byteArrayOf()
//                text = ""
//                for (i in 0 until confidenceArrPerSeg.size / 6)
//                {
//                    for (j in 0..5)
//                    {
//                        text += confidenceArrPerSeg[i * 6 + j].toString() + ","
//                    }
//                    text += "\n"
//                    csvText += text.toByteArray()
//                    text = ""
//                }
////                Log.i("makeDiaryFragment", "confidenceArrSize: " + confidenceArrPerSeg.size.toString())
////                Log.i("makeDiaryFragment", "csvTextSize: " + csvText.size.toString())
//
//                val confidenceRef = storageRef.child("confidenceArrPerSeg.csv")
//                val uploadTaskC = confidenceRef.putBytes(csvText)
//                uploadTaskC.addOnFailureListener {
//                    Toast.makeText(requireContext(), "upload confidenceArrPerSeg failed", Toast.LENGTH_SHORT)
//                        .show()
//                }.addOnSuccessListener {
//                    Toast.makeText(requireContext(), "upload confidenceArrPerSeg succeed", Toast.LENGTH_SHORT)
//                        .show()
//                }
//
//                //mfcc
//                if (mfccData.isNotEmpty()) {
//                    csvText = byteArrayOf()
//                    text = ""
//                    val mfccSize = mfccData.size / 8064 - 1
//                    for (i in 0..mfccSize)
//                    {
//                        for (j in 0..8063)
//                        {
//                            text += mfccData[i * 8064 + j].toString() + ","
//                        }
////                        Log.i("make", "mfccData[-1]: " + mfccData[i * 8064 + 8063])
//                        text += "\n"
//                        csvText += text.toByteArray()
//                        text = ""
//                    }
////                    Log.i("makeDiaryFragment", "mfccDataSize: " + mfccData.size.toString())
////                    Log.i("makeDiaryFragment", "csvTextSize: " + csvText.size.toString())
//
//                    val mfccRef = storageRef.child("mfcc.csv")
//                    val uploadTaskM = mfccRef.putBytes(csvText)
//                    uploadTaskM.addOnFailureListener {
//                        Toast.makeText(requireContext(), "upload mfcc failed", Toast.LENGTH_SHORT)
//                            .show()
//                    }.addOnSuccessListener {
//                        Toast.makeText(requireContext(), "upload mfcc succeed", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//
//                }
//
//            }.start()

            navController.navigate(R.id.action_makeDiaryFragment_to_viewPagerFragment)
        }
    }

    private fun labelNum(label: String): Int {
        var num = 0
        when (label) {
            "angry" -> {
                num = 0
            }
            "sad" -> {
                num = 1
            }
            "happy" -> {
                num = 2
            }
            "frustration" -> {
                num = 3
            }
            "neutral" -> {
                num = 4
            }
            "surprise" ->{
                num = 5
            }
        }
        return num
    }

    private fun userFeedBackPopUp(singleChoiceIndex: AtomicInteger) {
        var feedback: String = ""
        var checkedEmo = singleChoiceIndex.toInt()
        val emoArray = arrayOf("生氣","難過","高興","挫折", "無感","驚喜")
        val EngEmoArr = arrayOf("angry","sad","happy" ,"frustration","neutral","surprise")
        //popup window
        AlertDialog.Builder(requireActivity())
            .setSingleChoiceItems(emoArray, checkedEmo
            ) { _, which -> checkedEmo = which }
            .setTitle("你剛剛的感覺其實是?")
            .setPositiveButton("確認") { dialog, _ ->
                feedback = emoArray[checkedEmo]
                EngFeedback = EngEmoArr[checkedEmo]
                changeViewByEmo(feedback, binding)
                Toast.makeText(requireActivity(), "現在你覺得$feedback", Toast.LENGTH_SHORT).show()
                singleChoiceIndex.set(checkedEmo)
                dialog.cancel()
            }
            .show() //呈現對話視窗
    }

    private fun changeViewByEmo( emo:String , binding: FrgmentMakeDiaryBinding){
        val tv: TextView = binding.tvResult
        val face: ImageView = binding.imgFace
        val note: ImageView = binding.imgNote
        val banner: ImageView = binding.banner
        val cl: ConstraintLayout = binding.clMD
        val textFront:String = tv.text.toString().subSequence(0,22) as String
        when (emo) {
            "驚喜","surprise" -> {
                tv.text = textFront + "驚喜"
                face.setImageResource(R.drawable.ic_surprise_big_face)
                note.setImageResource(R.drawable.ic_surprise_note)
                banner.setImageResource(R.drawable.ic_surprise_banner)
                cl.setBackgroundResource(R.color.surprise_orange)
            }
            "高興","happy" -> {
                tv.text = textFront + "高興"
                face.setImageResource(R.drawable.ic_happy_big_face)
                note.setImageResource(R.drawable.ic_happy_note)
                banner.setImageResource(R.drawable.ic_happy_banner)
                cl.setBackgroundResource(R.color.happy_yellow)
            }
            "難過","sad" -> {
                tv.text = textFront + "難過"
                face.setImageResource(R.drawable.ic_sad_big_face)
                note.setImageResource(R.drawable.ic_sad_note)
                banner.setImageResource(R.drawable.ic_sad_banner)
                cl.setBackgroundResource(R.color.sad_blue)
            }
            "無感","neutral" -> {
                tv.text = textFront + "無感"
                face.setImageResource(R.drawable.ic_neutral_big_face)
                note.setImageResource(R.drawable.ic_neutral_note)
                banner.setImageResource(R.drawable.ic_neutral_banner)
                cl.setBackgroundResource(R.color.neutral_green)
            }
            "挫折","frustration" -> {
                tv.text = textFront + "挫折"
                face.setImageResource(R.drawable.ic_frustration_big_face)
                note.setImageResource(R.drawable.ic_frustration_note)
                banner.setImageResource(R.drawable.ic_frustration_banner)
                cl.setBackgroundResource(R.color.frustration_violet)
            }
            "生氣","angry" ->{
                tv.text = textFront + "生氣"
                face.setImageResource(R.drawable.ic_angry_big_face)
                note.setImageResource(R.drawable.ic_angry_note)
                banner.setImageResource(R.drawable.ic_angry_banner)
                cl.setBackgroundResource(R.color.angry_pink)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

