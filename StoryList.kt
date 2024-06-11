package com.dicoding.appstory.data.prefence

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryList(
    val id: String,
    val name: String,
    val photoUrl: String,
    val description: String,
) : Parcelable

