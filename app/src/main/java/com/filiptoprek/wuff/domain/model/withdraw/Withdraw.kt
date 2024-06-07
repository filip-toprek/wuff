package com.filiptoprek.wuff.domain.model.withdraw

import com.google.firebase.Timestamp

data class Withdraw(
    val amount: Double = 0.0,
    val dateCreated: Timestamp = Timestamp.now(),
    val completed: Boolean = false
)

data class Withdrawals(
    val withdrawals: List<Withdraw> = emptyList(),
    val withdrawProfile: WithdrawProfile = WithdrawProfile()
)

data class WithdrawProfile(
    val iban: String = "",
    val swift: String = "",
)
