package com.example.moodymons.page.daily

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.moodymons.makediary.Emotion
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class SERViewModel: ViewModel() {
    private var user: FirebaseUser = Firebase.auth.currentUser!!
    private val uid = user.uid
    private val databaseRef = FirebaseDatabase.getInstance().getReference("Emotions/$uid")
    private var firebaseLiveData = FirebaseLiveData(databaseRef)
    val diary: LiveData<ArrayList<Pair<String, ArrayList<Emotion>>>>
        get() = getDiary()

    init {
//        Log.i("SERViewModel", "SERViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
//        Log.i("SERViewModel", "SERViewModel destroyed!")
    }

    @JvmName("getDiary1")
    fun getDiary(): LiveData<ArrayList<Pair<String, ArrayList<Emotion>>>> {
        return Transformations.map(firebaseLiveData){snapshot->
            val list: ArrayList<Pair<String, ArrayList<Emotion>>> = arrayListOf()
            val children: MutableIterable<DataSnapshot> = snapshot.children
            for (dateKey in children) {
                val item: Pair<String, ArrayList<Emotion>> = Pair(dateKey.key.toString(),
                    arrayListOf())
                for (emotionKey in dateKey.children) {
                    val emotionObject = emotionKey.getValue(Emotion::class.java)
                    if (emotionObject != null) {
                        item.second.add(emotionObject)
                    }
                }
                list.add(0, item)
            }
            return@map list
        }
    }

    @JvmName("getToday1")
    fun getToday(): String {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val date: LocalDate = LocalDate.now()
        return date.format(formatter)
    }

    fun getUID(): String {
        return uid
    }

    fun setUser(newUser: FirebaseUser){
        user = newUser
    }
}