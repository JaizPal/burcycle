package com.example.burparking.ui.view

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.burparking.databinding.FragmentBuscarDireccionBinding
import com.example.burparking.domain.model.Direccion
import com.example.burparking.domain.model.Parking
import com.example.burparking.ui.view.adapter.ParkingAdapter
import com.example.burparking.ui.viewModel.BuscarDireccionViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BuscarDireccionFragment : Fragment() {

    private var _binding: FragmentBuscarDireccionBinding? = null
    private val binding get() = _binding!!

    private var permisosConcedidos = false
    private val CODIGO_PERMISOS_UBICACION_SEGUNDO_PLANO = 1

    private lateinit var localizacionUsuario: FusedLocationProviderClient

    private val buscarDireccionViewModel: BuscarDireccionViewModel by viewModels()
    private lateinit var adapterDirecciones: ArrayAdapter<Direccion>

    private var direccionActual: Direccion? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buscarDireccionViewModel.onCreate()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBuscarDireccionBinding.inflate(inflater, container, false)
        verificarPermisos()
        localizacionUsuario =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        localizacionUsuario.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        )
        val autoCompletado = binding.autoCompleteDirecciones
        /*
         * Muestra el progessBar dependiendo del valor de isLoading del ViewModel
         */
        buscarDireccionViewModel.isLoading.observe(requireActivity()) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
            binding.progressBar.isVisible = it
        }

        /*
         * Cuando cambia el valor de direcciones del ViewModel, si es distinto
         * de null, crea el adapter para el autocompletado
         */
        buscarDireccionViewModel.direcciones.observe(requireActivity()) {
            if (it != null) {
                adapterDirecciones = ArrayAdapter<Direccion>(
                    requireActivity(),
                    android.R.layout.simple_dropdown_item_1line,
                    buscarDireccionViewModel.direcciones.value!!.toList()
                )
                autoCompletado.setAdapter(adapterDirecciones)
            }
        }

        /*
         * Cuando se pulsa sobre alguna de las direcciones se ejecuta
         * el proceso de búsqueda de los aparcamientos más cercanos
         */
        autoCompletado.setOnItemClickListener { parent, view, position, id ->
            buscarDireccionViewModel.parkingCercanos(adapterDirecciones.getItem(position)!!)
            direccionActual = adapterDirecciones.getItem(position)
            bajarTeclado()
        }

        /*
         * Controla el efecto Shimmer dependiendo de si la variable
         * nParking del ViewModel vale 10 o no.
         */
        buscarDireccionViewModel.nParking.observe(requireActivity()) {
            if (it == 10) {
                binding.shimmer.visibility = View.GONE
                binding.shimmer.stopShimmer()
                binding.recyclerViewParking.visibility = View.VISIBLE
                cargarCardsParkings()
            } else {
                binding.shimmer.startShimmer()
                binding.shimmer.visibility = View.VISIBLE
                binding.recyclerViewParking.visibility = View.GONE
            }
        }

        /*
         * Navega a la ventana del mapa
         */
        binding.tvVerMapa.setOnClickListener {
            aplicarAnimaciones()
            findNavController().navigate(
                BuscarDireccionFragmentDirections.actionBuscarDireccionFragmentToMapFragment(
                    Direccion(0, 42.340833, -3.699722, null, null, null),
                    arrayOf()
                )
            )
        }

        /*
         * Se realiza la búsqueda de aparcamientos cercanos según la ubicación del usuario.
         * Se comprueba que se han concedido los permisos y que tiene la ubicación activada.
         */
        binding.tvUbicacionActual.setOnClickListener {
            bajarTeclado()
            buscarDireccionViewModel.nParking.postValue(0)
            verificarPermisos()
            if (permisosConcedidos) {
                if (localizacionActivada()) {
                    localizacionUsuario =
                        LocationServices.getFusedLocationProviderClient(requireActivity())
                    localizacionUsuario.getCurrentLocation(
                        LocationRequest.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    ).addOnSuccessListener {
                        direccionActual = Direccion(
                            0,
                            it.latitude,
                            it.longitude,
                            null,
                            null,
                            null
                        )
                        buscarDireccionViewModel.parkingCercanos(
                            direccionActual!!
                        )
                        buscarDireccionViewModel.getReverseDireccion(
                            Parking(
                                0,
                                it.latitude,
                                it.longitude,
                                0,
                                null,
                                null,
                                null,
                                null
                            )
                        )
                    }
                } else {
                    /*
                     * Muestra un SnackBar si la localización está desactivada
                     */
                    showSnackBar("Localización desactivada")
                }
            } else {
                /*
                 * Muestra un SnackBar si no se han concedido los permisos
                 */
                showSnackBar("Permisos no concedidos")
            }
        }

        /*
         * Modifica el input si el usuario ha pulsado sobre la ubicación actual
         * y la variable ha cambiado
         */
        buscarDireccionViewModel.reverseDireccion.observe(requireActivity()) {
            if (it != null) {
                autoCompletado.setText(it.toString())
            }
        }

        return binding.root
    }

    /*
     * Muestra un SnackBar según el mensaje enviado
     */
    private fun showSnackBar(message: String) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    /*
     * Carga en el recyclerView los aparcamientos más cercanos
     */
    private fun cargarCardsParkings() {
        val recyclerView = binding.recyclerViewParking
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter =
            ParkingAdapter(buscarDireccionViewModel.closestParkings.value!!, direccionActual!!)
    }

    /*
     * Comprueba que se han dado todos los permisos
     * @return Boolean: todos los permisos han sido otorgados
     */
    private fun checkPermisos(permisos: Array<String>): Boolean {
        return permisos.all {
            return ContextCompat.checkSelfPermission(
                requireActivity(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    /*
     * Solicita al usuario los permisos mandados por parámetros
     */
    private fun solicitarPermisos(permisos: Array<String>) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            permisos,
            CODIGO_PERMISOS_UBICACION_SEGUNDO_PLANO
        )
    }

    /*
     * Establece los permisos necesarios según la versión de Android
     * y los checkea, si no están concedidos los solicita.
     */
    private fun verificarPermisos() {
        val permisos =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                arrayListOf(
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION,
                    ACCESS_NETWORK_STATE,
                    READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE,
                    FOREGROUND_SERVICE
                )
            } else {
                arrayListOf(
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION,
                    ACCESS_NETWORK_STATE,
                    READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE
                )
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permisos.add(ACCESS_BACKGROUND_LOCATION)
        }
        val permisosArray = permisos.toTypedArray()

        if (checkPermisos(permisosArray)) {
            permisosConcedidos = true
        } else {
            solicitarPermisos(permisosArray)
        }
    }

    /*
     * Comprueba si la localización está activada
     * @return Boolean: la localización está activada
     */
    private fun localizacionActivada(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /*
     * Baja el teclado del teléfono
     */
    private fun bajarTeclado() {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity?.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


    override fun onPause() {
        super.onPause()
        buscarDireccionViewModel.parkingCargados.value = null
        binding.shimmer.visibility = View.GONE
        binding.autoCompleteDirecciones.setText("")
    }

    /*
     * Define las animaciones que se ejecutan cuando el método es llamado
     */
    private fun aplicarAnimaciones() {
        binding.tvVerMapa.animate()
            .alpha(0f)
            .translationXBy(1200f)
            .duration = 400L
        binding.tvUbicacionActual.animate()
            .alpha(0f)
            .translationXBy(-1200f)
            .duration = 400L
        binding.recyclerViewParking.animate()
            .alpha(0f)
            .duration = 400L
        binding.layoutAutoComplete.animate()
            .alpha(0f)
            .translationY(-1000f)
            .duration = 400L
    }
}