package com.example.moodymons.makediary

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties

data class Emotion(val predict : String? = null,val feedback : String? = null,val diary : String? = null,val confidenceList : List<Float>? = null)
{

}