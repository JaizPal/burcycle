package com.example.burparking.ui.view

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.burparking.R
import com.example.burparking.databinding.FragmentMapBinding
import com.example.burparking.ui.viewModel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.MapBoxTileSource
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@AndroidEntryPoint
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    lateinit var map: MapView

    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapViewModel.onCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        mapViewModel.onCreate()
        configureMap()
        mapViewModel.parkingCargados.observe(requireActivity()) {
            if (it == true) {
                val items = ArrayList<OverlayItem>()

                mapViewModel.parkings.value?.forEach {
                    val overlayItem = OverlayItem(
                        it.calle,
                        it.numero,
                        GeoPoint(it.lat, it.lon)
                    )
                    overlayItem.setMarker(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.ic_location
                        )
                    )
                    items.add(overlayItem)

                }
                var overlay = ItemizedOverlayWithFocus<OverlayItem>(
                    items,
                    object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                        override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                            TODO("Not yet implemented")
                            return true
                        }

                        override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                            TODO("Not yet implemented")
                            return false
                        }
                    },
                    requireContext()
                )
                map.overlays.add(overlay)
            }

        }
        return binding.root
    }

    private fun configureMap() {
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        mapColorConfigure()
        map.isTilesScaledToDpi = true
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        map.setMultiTouchControls(true);
        val mapController = map.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(42.340833, -3.699722)
        mapController.setCenter(startPoint)

        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)


    }

    private fun mapColorConfigure() {
        val inverseMatrix = ColorMatrix(floatArrayOf(-1.0f,0.0f, 0.0f, 0.0f, 255f,
            0.0f, -1.0f, 0.0f, 0.0f, 255f,
            0.0f, 0.0f, -1.0f, 0.0f, 255f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f))
        val destinationColor = Color.parseColor("#FF2A2A2A")
        val lr = (255.0f - Color.red(destinationColor))/255.0f
        val lg = (255.0f - Color.green(destinationColor))/255.0f
        val lb = (255.0f - Color.blue(destinationColor))/255.0f
        val grayscaleMatrix = ColorMatrix(floatArrayOf(lr, lg, lb, 0f, 0f,
            lr, lg, lb, 0f, 0f,
            lr, lg, lb, 0f, 0f,
            0f, 0f, 0f, 0f, 255f, ))
        grayscaleMatrix.preConcat(inverseMatrix)
        val dr = Color.red(destinationColor)
        val dg = Color.green(destinationColor)
        val db = Color.blue(destinationColor)
        val drf = dr / 255f
        val dgf = dg / 255f;
        val dbf = db / 255f
        val tintMatrix = ColorMatrix(floatArrayOf(
            drf, 0f, 0f, 0f, 0f,
            0f, dgf, 0f, 0f, 0f,
            0f, 0f, dbf, 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
        ))
        tintMatrix.preConcat(grayscaleMatrix)
        val lDestianation = drf * lr + dgf * lg + dbf * lb
        val scale = 1f - lDestianation
        val translate = 1 - scale * 0.5f
        val scaleMatrix = ColorMatrix(floatArrayOf(
            scale, 0f, 0f, 0f, dr * translate,
            0f, scale, 0f, 0f, dg * translate,
            0f, 0f, scale, 0f, db * translate,
            0f, 0f, 0f, 1f, 0f
        ))
        scaleMatrix.preConcat(tintMatrix)
        val filter = ColorMatrixColorFilter(scaleMatrix)
        map.overlayManager.tilesOverlay.setColorFilter(filter)
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }


}