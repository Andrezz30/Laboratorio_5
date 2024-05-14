package cr.ac.menufragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cr.ac.una.controlfinanciero.adapter.MovimientoAdapter
import cr.ac.una.controlfinancierocamera.EditControlFinancieroFragment
import cr.ac.una.controlfinancierocamera.MainActivity
import cr.ac.una.controlfinancierocamera.R
import cr.ac.una.controlfinancierocamera.dao.MovimientoDAO
import cr.ac.una.controlfinancierocamera.db.AppDatabase
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import cr.ac.una.controlfinancierocamera.viewmodel.MovimientoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"


class ListControlFinancieroFragment : Fragment() {
    private lateinit var movimientoDao: MovimientoDAO
    companion object {
        private const val TAG = "ListControlFinancieroFragment" // Definir TAG como una constante en el companion object
    }
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    //lateinit var adapter: MovimientoAdapter
    //val movimientoController = MovimientoController()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list_control_financiero, container, false)

        movimientoDao = AppDatabase.getInstance(requireContext()).ubicacionDao()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.listaMovimientos)
        var movientosViewModel = ViewModelProvider(requireActivity()).get(MovimientoViewModel::class.java)
        var movimientos = mutableListOf<Movimiento>()
        var adapter = MovimientoAdapter(requireContext(), movimientos as ArrayList<Movimiento>)
        listView.adapter = adapter
        movientosViewModel.movimientos.observe(requireActivity()){
            (listView.adapter as? MovimientoViewModel)?.updateMovimiento(movimientos)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val ubicaciones = withContext(Dispatchers.Default) {
                    movimientoDao.getAll() // Obtener los datos de la base de datos
                }
                val adapter = MovimientoAdapter(requireContext(), ubicaciones as List<Movimiento>)
                listView.adapter = adapter
            } catch (e: Exception) {
                // Manejar errores adecuadamente, como mostrar un mensaje de error al usuario
                Log.e(TAG, "Error al cargar datos desde la base de datos: ${e.message}")
            }
        }

        val botonNuevo = view.findViewById<Button>(R.id.botonNuevo)
        botonNuevo.setOnClickListener {
            insertEntity()
        }



    /*-----------------------------Para las apis-----------------------
    lifecycleScope.launch {
        withContext(Dispatchers.Main) {
            movimientoController.listMovimientos()
            val list = view.findViewById<ListView>(R.id.listaMovimientos)
            adapter = MovimientoAdapter(requireContext(), movimientoController.listMovimientos())
            list.adapter = adapter
        }
    }*/

        /*val listView = view.findViewById<ListView>(R.id.listaMovimientos)
        // Verificar si la lista de movimientos está vacía antes de realizar la consulta a la base de datos
        if (listView.adapter == null || (listView.adapter as MovimientoAdapter).isEmpty()) {
            lifecycleScope.launch {
                withContext(Dispatchers.Default) {
                    var ubicaciones = movimientoDao.getAll() as List<Movimiento>
                    val adapter = MovimientoAdapter(requireContext(), ubicaciones)
                    listView.adapter = adapter
                }
            }
        }*/

    }

    private fun insertEntity() {
        val movimiento = Movimiento(null,null,0.0,"","")//, Bitmap.createBitmap(1,1,Bitmap.Config.ALPHA_8))
        val fragment = EditControlFinancieroFragment()
        val fragmentManager = (context as MainActivity).supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        fragment.movimientoModificar = movimiento
        transaction.replace(R.id.home_content, fragment)
        transaction.addToBackStack(null) // Agrega la transacción a la pila de retroceso
        transaction.commit()

    }

    fun actualizarData() {
        val mainActivity = context as MainActivity
        //mainActivity.adapter.notifyDataSetChanged()
    }


}