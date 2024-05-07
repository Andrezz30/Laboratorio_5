package cr.ac.una.controlfinancierocamera.entity
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Movimiento(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    var _uuid: String?,
    var monto: Double,
    var tipo: String,
    var fecha: String
    //var imagen: Bitmap
) : Serializable


