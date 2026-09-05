package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QRCodeDao {
    @Query("SELECT * FROM qr_codes ORDER BY timestamp DESC")
    fun getAllQRCodes(): Flow<List<QRCodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQRCode(qrCode: QRCodeEntity)

    @Delete
    suspend fun deleteQRCode(qrCode: QRCodeEntity)

    @Query("UPDATE qr_codes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM qr_codes")
    suspend fun clearAll()
}
