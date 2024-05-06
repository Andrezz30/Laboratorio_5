package cr.ac.una.controlfinancierocamera.entity
import android.graphics.Bitmap
import android.view.View
import java.io.Serializable

data class Movimiento(
    var _uuid: String?,
    var monto: Double,
    var tipo: String,
    var fecha: String,
    var imagen: Bitmap
) : Serializable


