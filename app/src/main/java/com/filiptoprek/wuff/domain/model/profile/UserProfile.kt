package com.filiptoprek.wuff.domain.model.profile

import com.filiptoprek.wuff.domain.model.rating.Review
import com.google.firebase.Timestamp

data class UserProfile(
    val user: UserData = UserData(),
    val balance: Double = 0.0,
    val pendingBalance: Double = 0.0,
    val aboutUser: String = "",
    val numOfWalks: Int = 0,
    val dateCreated: Long = System.currentTimeMillis(),
    val dateUpdated: Long = System.currentTimeMillis(),
    val transactions: List<Transaction> = listOf(),
    val walker: Walker? = null,
    val city: String = ""
)

data class UserData(
    val uid: String = "",
    val name: String = "",
    val profilePhotoUrl: String = ""
)

data class Walker(
    val approved: Boolean = false,
    val phoneNumber: String = "",
    val address: String = "",
    val averageRating: Double = 0.0,
    val reviews: List<Review> = listOf(),
)

data class Transaction(
    val id: String = "",
    val date: Timestamp = Timestamp.now(),
    val amount: Double = 0.0,
    val isSuccessful: Boolean = false
)