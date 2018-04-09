package com.hack.kind.lvivbikemap

import android.app.AlertDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import org.osmdroid.tileprovider.cachemanager.CacheManager
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.MapView

/**
 * Sample for using the cache manager to download tiles on screen
 * Created by alex on 2/21/16.
 */
class SampleCacheDownloader : Fragment(), View.OnClickListener, OnSeekBarChangeListener, TextWatcher {

    protected lateinit var mMapView: MapView

    internal lateinit var btnCache: Button
    internal var executeJob: Button? = null
    internal var zoom_min: SeekBar? = null
    internal var zoom_max: SeekBar? = null
    internal var cache_north: EditText? = null
    internal var cache_south: EditText? = null
    internal var cache_east: EditText? = null
    internal var cache_west: EditText? = null
    internal var cache_estimate: TextView? = null
    internal lateinit var mgr: CacheManager
    internal var downloadPrompt: AlertDialog? = null
    internal var alertDialog: AlertDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.sample_cachemgr, container, false)

        //prevent the action bar/toolbar menu in order to prevent tile source changes.
        //if this is enabled, playstore users could actually download large volumes of tiles
        //from tile sources that do not allow it., causing our app to get banned, which would be
        //bad
        setHasOptionsMenu(false)


        mMapView = root.findViewById(R.id.mapview)
        btnCache = root.findViewById(R.id.btnCache)
        btnCache.setOnClickListener(this)
        mgr = CacheManager(mMapView)
        return root
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
        if (alertDialog != null && alertDialog!!.isShowing) {
            alertDialog!!.dismiss()
        }
        if (downloadPrompt != null && downloadPrompt!!.isShowing) {
            downloadPrompt!!.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.executeJob -> updateEstimate(true)

            R.id.btnCache -> showCacheManagerDialog()
        }
    }


    private fun showCacheManagerDialog() {

        val alertDialogBuilder = AlertDialog.Builder(
                activity)


        // set title
        alertDialogBuilder.setTitle("R.string.cache_manager")
        //.setMessage(R.string.cache_manager_description);

        // set dialog message
        alertDialogBuilder.setItems(arrayOf<CharSequence>("R.string.cache_current_size", "R.string.cache_download", "R.string.cancel")
        ) { dialog, which ->
            when (which) {
                0 -> showCurrentCacheInfo()
                1 -> {
                    downloadJobAlert()
                    dialog.dismiss()
                }
                else -> dialog.dismiss()
            }
        }


        // create alert dialog
        alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog!!.show()


        //mgr.possibleTilesInArea(mMapView.getBoundingBox(), 0, 18);
        // mgr.
    }

    private fun downloadJobAlert() {
        //prompt for input params
        val builder = AlertDialog.Builder(activity)

        val view = View.inflate(activity, R.layout.sample_cachemgr_input, null)

        val boundingBox = mMapView.boundingBox
        zoom_max = view.findViewById(R.id.slider_zoom_max)
        zoom_max!!.max = mMapView.maxZoomLevel.toInt()
        zoom_max!!.setOnSeekBarChangeListener(this@SampleCacheDownloader)
        //
        //
        zoom_min = view.findViewById(R.id.slider_zoom_min)
        zoom_min!!.max = mMapView.maxZoomLevel.toInt()
        zoom_min!!.progress = mMapView.minZoomLevel.toInt()
        zoom_min!!.setOnSeekBarChangeListener(this@SampleCacheDownloader)
        cache_east = view.findViewById(R.id.cache_east)
        cache_east!!.setText(boundingBox.lonEast.toString() + "")
        cache_north = view.findViewById(R.id.cache_north)
        cache_north!!.setText(boundingBox.latNorth.toString() + "")
        cache_south = view.findViewById(R.id.cache_south)
        cache_south!!.setText(boundingBox.latSouth.toString() + "")
        cache_west = view.findViewById(R.id.cache_west)
        cache_west!!.setText(boundingBox.lonWest.toString() + "")
        cache_estimate = view.findViewById(R.id.cache_estimate)

        //        change listeners for both validation and to trigger the download estimation
        cache_east!!.addTextChangedListener(this)
        cache_north!!.addTextChangedListener(this)
        cache_south!!.addTextChangedListener(this)
        cache_west!!.addTextChangedListener(this)
        executeJob = view.findViewById(R.id.executeJob)
        executeJob!!.setOnClickListener(this)
        builder.setView(view)
        builder.setCancelable(true)
        builder.setOnCancelListener {
            cache_east = null
            cache_south = null
            cache_estimate = null
            cache_north = null
            cache_west = null
            executeJob = null
            zoom_min = null
            zoom_max = null
        }
        downloadPrompt = builder.create()
        downloadPrompt!!.show()


    }

    /**
     * if true, start the job
     * if false, just update the dialog box
     */
    private fun updateEstimate(startJob: Boolean) {
        try {
            if (cache_east != null &&
                    cache_west != null &&
                    cache_north != null &&
                    cache_south != null &&
                    zoom_max != null &&
                    zoom_min != null) {
                val n = java.lang.Double.parseDouble(cache_north!!.text.toString())
                val s = java.lang.Double.parseDouble(cache_south!!.text.toString())
                val e = java.lang.Double.parseDouble(cache_east!!.text.toString())
                val w = java.lang.Double.parseDouble(cache_west!!.text.toString())

                val zoommin = zoom_min!!.progress
                val zoommax = zoom_max!!.progress
                //nesw
                val bb = BoundingBox(n, e, s, w)
                val tilecount = mgr.possibleTilesInArea(bb, zoommin, zoommax)
                cache_estimate!!.text = tilecount.toString() + " tiles"
                if (startJob) {
                    if (downloadPrompt != null) {
                        downloadPrompt!!.dismiss()
                        downloadPrompt = null
                    }
                    val callback: CacheManager.CacheManagerCallback = object : CacheManager.CacheManagerCallback {
                        override fun onTaskComplete() {
                            Toast.makeText(activity, "Download complete!", Toast.LENGTH_LONG).show()
                        }

                        override fun onTaskFailed(errors: Int) {
                            Toast.makeText(activity, "Download complete with $errors errors", Toast.LENGTH_LONG).show()
                        }

                        override fun updateProgress(progress: Int, currentZoomLevel: Int, zoomMin: Int, zoomMax: Int) {
                            //NOOP since we are using the build in UI
                        }

                        override fun downloadStarted() {
                            //NOOP since we are using the build in UI
                        }

                        override fun setPossibleTilesInArea(total: Int) {
                            //NOOP since we are using the build in UI
                        }
                    }
                    mgr.downloadAreaAsync(activity, bb, zoommin, zoommax, callback)

                }

            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    private fun showCurrentCacheInfo() {
        Toast.makeText(activity, "Calculating...", Toast.LENGTH_SHORT).show()
        Thread(Runnable {
            val alertDialogBuilder = AlertDialog.Builder(
                    activity)


            // set title
            alertDialogBuilder.setTitle("R.string.cache_manager")
                    .setMessage("Cache Capacity (bytes): " + mgr.cacheCapacity() + "\n" +
                            "Cache Usage (bytes): " + mgr.currentCacheUsage())

            // set dialog message
            alertDialogBuilder.setItems(arrayOf<CharSequence>(

                    " getResources().getString(R.string.cancel)")
            ) { dialog, which -> dialog.dismiss() }




            activity!!.runOnUiThread {
                // show it
                // create alert dialog
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }).start()


    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        updateEstimate(false)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {

    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        updateEstimate(false)
    }

    override fun afterTextChanged(s: Editable) {

    }

}
