package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.QRCodeEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QRCodeViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).qrCodeDao()

    val qrCodes: StateFlow<List<QRCodeEntity>> = dao.getAllQRCodes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insert(content: String, type: String, category: String) {
        viewModelScope.launch {
            dao.insertQRCode(
                QRCodeEntity(
                    content = content,
                    type = type,
                    category = category,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun delete(qrCode: QRCodeEntity) {
        viewModelScope.launch {
            dao.deleteQRCode(qrCode)
        }
    }

    fun toggleFavorite(id: Long, currentFavorite: Boolean) {
        viewModelScope.launch {
            dao.updateFavorite(id, !currentFavorite)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }
}
