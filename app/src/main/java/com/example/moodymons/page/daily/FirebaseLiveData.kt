package com.example.moodymons.page.daily

import androidx.lifecycle.LiveData
import com.google.firebase.database.*

class FirebaseLiveData(ref: DatabaseReference) : LiveData<DataSnapshot>() {
    private var query: Query

    private val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            value = snapshot
        }

        override fun onCancelled(error: DatabaseError) {
//            Log.w("LiveData:onCancelled", error.toException())
        }
    }

    init {
        query = ref.orderByKey()
    }

    override fun onActive() {
        super.onActive()
        query.addValueEventListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        query.removeEventListener(listener)
    }


}