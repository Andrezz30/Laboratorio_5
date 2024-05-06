package cr.ac.una.controlfinanciero.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import cr.ac.menufragment.CameraFragment
import cr.ac.una.controlfinancierocamera.EditControlFinancieroFragment
import cr.ac.una.controlfinancierocamera.MainActivity
import cr.ac.una.controlfinancierocamera.R

import cr.ac.una.controlfinancierocamera.entity.Movimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovimientoAdapter (context:Context, movimientos:List<Movimiento>):
    ArrayAdapter<Movimiento>(context,0,movimientos){


    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = LayoutInflater.from(context)
            .inflate(R.layout.list_item, parent, false)
        val monto = view.findViewById<TextView>(R.id.monto)
        val tipo = view.findViewById<TextView>(R.id.tipo)
        val fecha = view.findViewById<TextView>(R.id.fecha)


        var movimiento = getItem(position)
        monto.text = movimiento?.monto.toString()
        tipo.text = movimiento?.tipo.toString()
        fecha.text = movimiento?.fecha.toString()


        var bottonDelete = view.findViewById<ImageButton>(R.id.button_delete)
        bottonDelete.setOnClickListener{
            AlertDialog.Builder(context)
                .setTitle("Confirmar eliminación")
                .setMessage("Esta accion es irrebersible, ¿Desea eliminar esta transacción?")
                .setPositiveButton("Sí") { dialog, which ->
                    val mainActivity = context as MainActivity
                    GlobalScope.launch(Dispatchers.Main) {
                        movimiento?.let { it1 -> mainActivity.movimientoController.deleteMovimiento(it1) }
                        clear()
                        addAll(mainActivity.movimientoController.listMovimientos())
                        notifyDataSetChanged()
                        notifyDataSetChanged()
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
        var bottonUpdate = view.findViewById<ImageButton>(R.id.button_update)
        bottonUpdate.setOnClickListener{
            val fragment = EditControlFinancieroFragment()
            val fragmentManager = (context as MainActivity).supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.home_content, fragment)
            transaction.addToBackStack(null) // Agrega la transacción a la pila de retroceso
            transaction.commit()
            if (movimiento != null) {
                fragment.movimientoModificar = movimiento
            }
        }



        return view
    }
}