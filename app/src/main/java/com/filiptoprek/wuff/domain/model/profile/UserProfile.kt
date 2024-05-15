package com.filiptoprek.wuff.domain.model.profile

data class UserProfile(
    val balance: Double = 0.0,
    val aboutUser: String = "",
    val numOfWalks: Int = 0,
    val dateCreated: Long = System.currentTimeMillis(),
    val dateUpdated: Long = System.currentTimeMillis(),
    val transactions: List<Transaction> = listOf(),
    val walks: List<Walk> = listOf(),
    val walker: List<Walker> = listOf()
)

// TODO: finish walker profile
data class Walker(
    val isApproved: Boolean,
)

data class Transaction(
    val id: String,
    val date: Long,
    val amount: Double,
    val isSuccessful: Boolean
)

data class Walk(
    val walkerUserId: String,
    val dateOfWalk: Long,
    val isCompleted: Boolean = false,
    val price: Double
)