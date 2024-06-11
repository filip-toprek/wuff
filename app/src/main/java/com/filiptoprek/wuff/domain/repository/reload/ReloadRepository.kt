package com.filiptoprek.wuff.domain.repository.reload

import com.filiptoprek.wuff.domain.model.reload.Reload
import com.filiptoprek.wuff.domain.model.auth.Resource

interface ReloadRepository {
    suspend fun reloadBalance(reload: Reload): Resource<Unit>
}