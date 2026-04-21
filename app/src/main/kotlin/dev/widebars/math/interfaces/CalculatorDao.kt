package dev.goodwy.math.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.goodwy.math.models.History

@Dao
interface CalculatorDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT :limit")
    fun getHistory(limit: Int = 20): List<History>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(history: History): Long

    @Query("DELETE FROM history")
    fun deleteHistory()

    @Query("DELETE FROM history WHERE id = :id")
    fun deleteHistoryItem(id: Long)
}
