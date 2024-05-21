package com.filiptoprek.wuff.domain.model.profile

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

data class Review(
    val rating: Int,
    val userId: String,
    val dateOfReview: Long
)

data class Transaction(
    val id: String,
    val date: Long,
    val amount: Double,
    val isSuccessful: Boolean
)