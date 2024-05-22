package com.filiptoprek.wuff.data.repository.reload

import com.filiptoprek.wuff.data.utils.await
import com.filiptoprek.wuff.domain.model.Reload
import com.filiptoprek.wuff.domain.model.auth.Resource
import com.filiptoprek.wuff.domain.model.profile.Transaction
import com.filiptoprek.wuff.domain.model.profile.UserProfile
import com.filiptoprek.wuff.domain.repository.reload.ReloadRepository
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID
import javax.inject.Inject

class ReloadRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) :ReloadRepository {
    override suspend fun reloadBalance(reload: Reload): Resource<Unit> {
        return try {
            val user = firebaseFirestore.collection("users").document(reload.reloadUser).get().await().toObject(UserProfile::class.java)!!
            firebaseFirestore.collection("users").document(reload.reloadUser)
                .update("balance", user.balance.plus(reload.reloadAmount)).await()
            addTransactoin(reload)
        } catch (e: Exception) {
            Resource.Failure(e)
        }
    }

    private suspend fun addTransactoin(reload: Reload): Resource<Unit>
    {
        val transactionList: List<Transaction> = firebaseFirestore.collection("users").document(reload.reloadUser).get().await().toObject(UserProfile::class.java)?.transactions ?: emptyList()
        try{
            firebaseFirestore.collection("users").document(reload.reloadUser).update("transactions", transactionList.plus(Transaction(
                UUID.randomUUID().toString(), reload.reloadDate, reload.reloadAmount, true),
            )).await()
            return Resource.Success(Unit)
        }catch (e: Exception)
        {
            firebaseFirestore.collection("users").document(reload.reloadUser).update("transactions", transactionList.plus(Transaction(
                UUID.randomUUID().toString(), reload.reloadDate, reload.reloadAmount, false),
            )).await()
            return Resource.Failure(e)
        }

    }
}