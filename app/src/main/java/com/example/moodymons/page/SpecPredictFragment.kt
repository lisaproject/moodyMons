package com.example.moodymons.page

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.media.AudioRecord
import android.media.AudioRecord.READ_BLOCKING
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.HandlerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.moodymons.App
import com.example.moodymons.R
import com.example.moodymons.databinding.FragmentRecordingBinding
import com.example.moodymons.feature.MFCC
import com.example.moodymons.ml.Metadata2dCnnLstmRelu


import com.example.moodymons.viewpager.ViewPagerFragmentDirections
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.task.audio.classifier.AudioClassifier


class SpecPredictFragment : Fragment() {
    private var isPredictFinish = false
    private var _binding: FragmentRecordingBinding? = null
    private val binding get() = _binding!!
    private var classificationInterval = 500L // how often should classification run in milli-secs
    private lateinit var handler: Handler // background thread handler to run classification
    private lateinit var handlerThread:HandlerThread
    private var audioClassifier: AudioClassifier? = null
    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val model = Metadata2dCnnLstmRelu.newInstance(App.mContext)
    private var currentLabels: MutableList<String>  = mutableListOf()
    private var confidenceArr = FloatArray(6)
    private var confidenceArrPerSeg = FloatArray(0)
    private var audioData: FloatArray = FloatArray(0)
    private var mfccData: FloatArray = FloatArray(0)
    private var delayTime:Long = 0L
    private lateinit var run : Runnable
    private var startRecordingTime = 0L
    private var currentRecordingTime = 0L
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordingBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Create a handler to run classification in a background thread
        handlerThread = HandlerThread("backgroundThread")
        handlerThread.start()
        handler = HandlerCompat.createAsync(handlerThread.looper)

        binding.micButton.setOnClickListener {
            if (!isRecording) {
                // Initialize the Timer
                binding.micButton.setBackgroundResource(R.drawable.ic_mic_gray)
                binding.recordTimer.base = SystemClock.elapsedRealtime()
                startRecordingTime = binding.recordTimer.base
                binding.recordTimer.start()
                startAudioClassification()
                isRecording = true
            } else {
                isRecording = false
                binding.micButton.setBackgroundResource(R.drawable.ic_mic)
                currentRecordingTime = SystemClock.elapsedRealtime()
                audioRecord?.stop()
                scope.launch {
                    binding.progress.visibility = View.VISIBLE
                    delay(delayTime)
                    while(!isPredictFinish){
//                        Log.d(TAG, "還沒結束")
                        delay(1000L)
                    }
                    stopAudioClassification()
                    binding.progress.visibility = View.INVISIBLE
                }
                binding.recordTimer.stop()
            }
        }
//
//        binding.logoutButton.setOnClickListener {
//            val name: String? = Firebase.auth.currentUser?.displayName
//            showToast("登出$name")
//            AuthUI.getInstance()
//                .signOut(this.requireContext())
//                .addOnCompleteListener {
//                    requireActivity().viewModelStore.clear()
//                    Firebase.auth.signOut()
//                    val navController = NavHostFragment.findNavController(this)
//                    navController.navigate(R.id.action_viewPagerFragment_to_signInFragment)
//                }
//        }

        binding.logoutBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("確定要登出嗎")
            builder.setPositiveButton("是的") { _: DialogInterface?, _: Int ->
                val name: String? = Firebase.auth.currentUser?.displayName
                Toast.makeText(requireActivity(), "登出 moodyMons", Toast.LENGTH_SHORT).show()
                AuthUI.getInstance()
                    .signOut(this.requireContext())
                    .addOnCompleteListener {
                        requireActivity().viewModelStore.clear()
                        Firebase.auth.signOut()
                        val navController = NavHostFragment.findNavController(this)
                        navController.navigate(R.id.action_viewPagerFragment_to_signInFragment)
                    }
            }
            builder.setNegativeButton("稍後") { _: DialogInterface?, _: Int -> }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun startAudioClassification() {
        // If the audio classifier is initialized and running, do nothing.
        if (audioClassifier != null) return
        // Initialize the audio classifier
        val classifier = AudioClassifier.createFromFile(requireContext(), MODEL_FILE)

        // Initialize the audio recorder
        val record = classifier.createAudioRecord()
        record.startRecording()
        var startTime = System.currentTimeMillis()
        // Define the classification runnable
        var isFirstSeg = true
        var offsetFrame = 0
        val stepFrame:Int = 4000// 250ms
        val segmentFrameSize:Int = 8000 // 500ms
        var segmentFrontHalf:FloatArray = FloatArray(stepFrame)
        var segmentEndHalf:FloatArray = FloatArray(stepFrame)
        var segment: FloatArray
        //var floatSignal: FloatArray = FloatArray(frameSize)// 2000ms
        var doubleSignal: DoubleArray = DoubleArray(segmentFrameSize)
        run = object : Runnable {
            override fun run() {
                var runStartTime = SystemClock.elapsedRealtime()
                if (isFirstSeg) {
                    record.read(segmentFrontHalf, 0, stepFrame, READ_BLOCKING)
                    isFirstSeg = false
                }
                record.read(segmentEndHalf,0,stepFrame,READ_BLOCKING)
                segment = segmentFrontHalf + segmentEndHalf
                audioData += segment
                segmentFrontHalf = segmentEndHalf
//                Log.d(TAG, "segment size: ${segment.size}")
                // Load the latest audio sample
                for (i in 0 until segmentFrameSize) {
                    doubleSignal[i] = segment[i].toDouble()
                }
                //MFCC java library.
                val mfccConvert = MFCC()
                var mfccInput = transposeMatrix(mfccConvert.getMelSpec(doubleSignal))
                var mfccInput1D = FloatArray(8064)//63*128
                if (mfccInput != null) {
                    for (i in mfccInput.indices) {
                        for (j in 0 until (mfccInput[0].size)) {
                            mfccInput1D[i*128+j] = ((mfccInput[i][j]+50.0) / 50.0).toFloat()
                        }
                    }
                }
//                Log.i("spec", "mfccInput1D[4034]: " + mfccInput1D[4034])
                mfccData += mfccInput1D
                // Creates inputs for reference.
                var audioClip =
                    TensorBuffer.createFixedSize(intArrayOf(1, 63, 128, 1), DataType.FLOAT32)
                audioClip.loadArray( mfccInput1D,intArrayOf(1, 63, 128, 1))

                // Runs model inference and gets result.
                val outputs = model.process(audioClip)
                val probability = outputs.probabilityAsTensorBuffer.floatArray

                //get the result
//                Log.d(TAG, "probability: ${probability[0]},${probability[1]},${probability[2]},${probability[3]},${probability[4]},${probability[5]}")
                for(i in 0 until 6){
                    confidenceArr[i] = confidenceArr[i] + probability[i]
                }
                confidenceArrPerSeg += probability

//                val output = classifier.classify(audioTensor)
//                for (result in output[0].categories){
//                    val label = result.label.subSequence(0,3).toString()
//                    val confidence = result.score
//                    //Log.d(TAG, "label = ${label},confidence = ${confidenceArr[labelNum(label)]}+${confidence}")
//                    confidenceArr[labelNum(label)] = confidenceArr[labelNum(label)] + confidence
//                }
                var segmentStartTime = (offsetFrame)/16000F
                var segmentEndTime = (offsetFrame+segmentFrameSize)/16000F
//                Log.d(TAG, "segment time: ${segmentStartTime}~${segmentEndTime}s")
                // Log.d(TAG, "exact record time: ${(record?.metrics?.getDouble("android.media.audiorecord.durationMs") ?: 0.0) / 1000.0}")
//                var recordingTime = (record?.metrics?.getLong("android.media.audiorecord.durationMs"))?.div(
//                    1000F
//                )

                //get current record time
                if(isRecording){
                    currentRecordingTime = SystemClock.elapsedRealtime()
                }

                var recordingTime = (currentRecordingTime - startRecordingTime)/1000F
//                Log.d(TAG, "exact record time: ${recordingTime}s")

                offsetFrame += stepFrame
                var remainTime = recordingTime?.minus(segmentEndTime)
//                Log.d(TAG, "Remain record time: ${remainTime}s")

                if (remainTime != null) {
                    if(isRecording||(!isRecording && remainTime >= 0.5)){
                        // Rerun the classification after a certain interval
                        handler.postDelayed(this, classificationInterval/2)
                    }else{
                        isPredictFinish = true
                    }
                    var remainSegCount = ((remainTime - 0.5)/0.25) + 1
                    var procTimePerSeg = 300
                    delayTime = (remainSegCount * procTimePerSeg).toLong()
//                    Log.d(TAG, "delayTime: ${delayTime}L")
                }
                // estimate run obj exec time
                var perRunTime = SystemClock.elapsedRealtime() - runStartTime
//                Log.d(TAG, "perRunTime: ${perRunTime}L")
            }
        }
        // Start the classification process
        handler.post(run)

        // Save the instances we just created for use later
        audioClassifier = classifier
        audioRecord = record
    }
    private fun transposeMatrix(matrix: Array<FloatArray>): Array<FloatArray>? {
        val m = matrix.size
        val n: Int = matrix[0].size
        val transposedMatrix = Array(n) { FloatArray(m) }
        for (x in 0 until n) {
            for (y in 0 until m) {
                transposedMatrix[x][y] = matrix[y][x]
            }
        }
        return transposedMatrix
    }
    private fun labelNum(label: String): Int {
        var num = 0
        when (label) {
            "ang" -> {
                num = 0
            }
            "sad" -> {
                num = 1
            }
            "hap" -> {
                num = 2
            }
            "fru" -> {
                num = 3
            }
            "neu" -> {
                num = 4
            }
            "sur" ->{
                num = 5
            }
        }
        return num
    }
    private fun stopAudioClassification() {

        val navController = NavHostFragment.findNavController(this)
        //find the maximum value of label confidence
        var max:Float = 0F
        var index = 0
        var maxindex = 0
        for(item in confidenceArr){
            if(item > max){
                max = item
                maxindex = index
            }
            index++
        }
        val emoArr = arrayOf("angry","sad","happy" ,"frustration","neutral","surprise")
        //pass data to MakeDiaryFragment
//        Log.d(TAG, "confidenceArr = [${confidenceArr[0]},${confidenceArr[1]},${confidenceArr[2]},${confidenceArr[3]},${confidenceArr[4]},${confidenceArr[5]}]")
        val label = emoArr[maxindex]
//        Log.d(TAG, "audioData size in Frame: ${audioData.size}")
        val action = ViewPagerFragmentDirections.actionViewPagerFragmentToMakeDiaryFragment(label,confidenceArr)
        navController.navigate(action)
        confidenceArr = FloatArray(6)
        audioData = FloatArray(0)
        delayTime = 0L
        handler.removeCallbacksAndMessages(null)
        audioRecord = null
        audioClassifier = null
        isPredictFinish = false
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        private var SAMPLE_RATE = 16000 //Hz
        const val REQUEST_RECORD_AUDIO = 1337
        private const val TAG = "specFragment"
        private const val MODEL_FILE = "Metadata_2d_cnn_lstm_relu.tflite"
        private const val MINIMUM_DISPLAY_THRESHOLD: Float = 0.3f
    }
}
