package cr.ac.una.controlfinancierocamera.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cr.ac.una.controlfinancierocamera.entity.Movimiento


class MovimientoViewModel: ViewModel() {
    private val _movimientos: MutableLiveData<List<Movimiento>> = MutableLiveData()
    val movimientos: LiveData<List<Movimiento>> = _movimientos


    fun updateMovimiento(movimientos: List<Movimiento>) {
        _movimientos.value = movimientos
    }

}