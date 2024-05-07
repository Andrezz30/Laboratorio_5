package cr.ac.una.controlfinancierocamera.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.controlfinancierocamera.entity.Movimientos
import retrofit2.http.*

@Dao
interface MovimientoDAO {
        @Insert
        fun insert(entity: Movimiento)

        @Query("SELECT * FROM movimiento")
        fun getAll(): List<Movimiento?>?
}
