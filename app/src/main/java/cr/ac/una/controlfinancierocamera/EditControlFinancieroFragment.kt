package cr.ac.una.controlfinancierocamera

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import cr.ac.una.controlfinancierocamera.entity.Movimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar


class EditControlFinancieroFragment() : Fragment() {
    lateinit var movimientoModificar: Movimiento
    lateinit var captureButton : Button
    lateinit var imageView : ImageView
    lateinit var elementoSeleccionado : String
    lateinit var datePickerButton : Button

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            // Permiso denegado, manejar la situación aquí si es necesario
        }
    }

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageView.setImageBitmap(imageBitmap)
        } else {
            // Manejar el caso en el que no se haya podido capturar la imagen
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var monto = view.findViewById<TextView>(R.id.textMonto)
        monto.setText(movimientoModificar.monto.toString())


        imageView = view.findViewById(R.id.imageView)
        if(imageView != null){

            imageView.setImageBitmap(movimientoModificar.imagen)
        }





        val spinner: Spinner = view.findViewById(R.id.tipoMovimientoSpinner)

        ArrayAdapter.createFromResource(
            view.context,
            R.array.tiposMovimiento,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Obtiene el valor seleccionado del array de recursos
                val elementos = resources.getStringArray(R.array.tiposMovimiento)
                elementoSeleccionado = elementos[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Se llama cuando no hay ningún elemento seleccionado
            }
        }


        var fecha = view.findViewById<TextView>(R.id.calendario)
        fecha.setText(movimientoModificar.fecha)

        fecha.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->fecha.setText("$dayOfMonth/${monthOfYear + 1}/$year")
            }, year, month, day)

            dpd.show()
        }


        val botonNuevo = view.findViewById<Button>(R.id.saveMovimientoButton)

        botonNuevo.setOnClickListener {
            var montoFinal = decimales(monto)

            val movimiento = Movimiento(null, montoFinal, elementoSeleccionado, fecha.text.toString(), imageView.drawToBitmap())

            if(movimientoModificar.monto== 0.0||movimientoModificar.tipo==""||movimientoModificar.fecha==""||movimientoModificar.imagen==(Bitmap.createBitmap(1,1,Bitmap.Config.ALPHA_8))){
                val actividad = activity as MainActivity
                GlobalScope.launch(Dispatchers.IO) {
                    actividad.movimientoController.insertMovimiento(movimiento)
                    // Regresar al fragmento anterior
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.popBackStack()
                }
            }else{
                val montoNuevo = view.findViewById<TextView>(R.id.textMonto)
                movimientoModificar.monto = decimales(montoNuevo)
                val fechaNuevo = view.findViewById<TextView>(R.id.calendario)
                movimientoModificar.fecha = fecha.text.toString()
                val tipoNuevo = view.findViewById<Spinner>(R.id.tipoMovimientoSpinner)
                movimientoModificar.tipo = elementoSeleccionado
                val imageViewNuevo = view.findViewById<ImageView>(R.id.imageView)
                /*movimientoModificar.imagen = imageView.drawToBitmap()*/

                val actividad = activity as MainActivity
                GlobalScope.launch(Dispatchers.Main) {
                    actividad.movimientoController.updateTransaction(movimientoModificar)
                    // Regresar al fragmento anterior
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.popBackStack()
                }
            }

        }

        val salir = view.findViewById<Button>(R.id.exitButton)

        salir.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }


        captureButton = view.findViewById(R.id.captureButton)

        captureButton.setOnClickListener {
            if (checkCameraPermission()) {
                dispatchTakePictureIntent()
            } else {
                requestCameraPermission()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_control_financiero, container, false)
    }private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }
    private fun decimales(sMonto: TextView): Double{
        val montoStr = sMonto.text.toString()
        val montoDouble = montoStr.toDoubleOrNull() ?: 0.0
        return String.format("%.2f", montoDouble).toDouble()
    }

}