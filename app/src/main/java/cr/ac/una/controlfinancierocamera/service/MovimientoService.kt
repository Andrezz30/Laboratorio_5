package cr.ac.una.controlfinancierocamera.service

import com.google.gson.GsonBuilder
import cr.ac.una.controlfinancierocamera.AuthInterceptor
//import cr.ac.una.jsoncrud.dao.MovimientoDAO
import cr.ac.una.controlfinancierocamera.dao.MovimientoDAO
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovimientoService {
    val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor("K3WLJSaDBshpYg-DIpVrZ2qUmZu8QUCdGT6eE9E9VMHtcwzdBQ"))
        .build()

    val gson = GsonBuilder().setPrettyPrinting().create()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://crudapi.co.uk/api/v1/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val apiService = retrofit.create(MovimientoDAO::class.java)

}