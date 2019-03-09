package com.ludoscity.findmybikes.data

import android.arch.lifecycle.LiveData
import android.content.Context
import android.util.Log
import com.ludoscity.findmybikes.citybik_es.model.BikeStation
import com.ludoscity.findmybikes.citybik_es.model.BikeSystemStatus
import com.ludoscity.findmybikes.data.network.BikeSystemNetworkDataSource
import com.ludoscity.findmybikes.helpers.BikeStationDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FindMyBikesRepository private constructor(private val stationDao: BikeStationDao,
                                                private val bikeSystemNetworkDataSource: BikeSystemNetworkDataSource){

    private val coroutineScopeIO = CoroutineScope(Dispatchers.IO)

    private var mInitialized = false
    private val networkBikeSystemStatusData: LiveData<BikeSystemStatus> = bikeSystemNetworkDataSource.bikeSystemStatusData

    init {

        networkBikeSystemStatusData.observeForever { newStatusDataFromNetwork ->
            coroutineScopeIO.launch {

                Log.d(TAG, "New data from network, begin processing of " +
                        "${newStatusDataFromNetwork?.bikeStationList?.size} stations for saving in Room")
                //TODO: Calculate bounds
                /*val boundsBuilder = LatLngBounds.Builder()

                for (station in statusAnswer.body()!!.network.bikeStationList) {
                    boundsBuilder.include(station.location)
                }

                mDownloadedBikeNetworkBounds = boundsBuilder.build()*/
                newStatusDataFromNetwork!!.bikeStationList!!.forEach {

                    it.uid = it.locationHash

                    if (it.extra != null){
                        if (it.extra.uid != null){
                            it.uid = "${newStatusDataFromNetwork.id}:${it.extra.uid}"
                        }

                        if (it.name == null){
                            it.name = it.extra.name
                        }
                    }
                }
                Log.d(TAG, "Processing done")
                //TODO: smarter update algorithm that purges obsolete stations from DB without dropping the whole table
                //deleteOldData
                stationDao.deleteAllBikeStation()
                Log.d(TAG, "Old station data deleted")
                //Insert new data into database
                stationDao.insertBikeStationList(newStatusDataFromNetwork.bikeStationList)
                Log.d(TAG, "New values inserted with replace strategy")
            }
        }
    }

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     */
    @Synchronized
    private fun initializeData(ctx: Context) {

        // Only perform initialization once per app lifetime. If initialization has already been
        // performed, we have nothing to do in this method.
        if (mInitialized) return
        mInitialized = true

        // This method call triggers Findmybikes to create its task to synchronize station data
        // periodically.
        //TODO: non manual refresh
        //bikeSystemNetworkDataSource.scheduleRecurringFetchBikeSystemSync()

        coroutineScopeIO.launch {
            if (isFetchNeeded()){
                startFetchBikeSystemDataService(ctx)
            }
        }
    }

    fun getBikeSystemStationData(ctx: Context): LiveData<List<BikeStation>>{
        initializeData(ctx)
        return stationDao.all
    }



    private fun isFetchNeeded(): Boolean {
        /*val today = SunshineDateUtils.getNormalizedUtcDateForToday()
        val count = mWeatherDao.countAllFutureWeather(today)
        return count < WeatherNetworkDataSource.NUM_DAYS*/
        return true
    }

    private fun startFetchBikeSystemDataService(ctx: Context) {
        bikeSystemNetworkDataSource.startFetchBikeSystemService(ctx)
    }

    companion object {
        private val TAG = FindMyBikesRepository::class.java.simpleName

        // For Singleton instantiation
        private val LOCK = Any()
        private var sInstance: FindMyBikesRepository? = null

        @Synchronized
        fun getInstance(
                bikeStationDao: BikeStationDao,
                bikeSystemNetworkDataSource: BikeSystemNetworkDataSource): FindMyBikesRepository {
            Log.d(TAG, "Getting the repository")
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = FindMyBikesRepository(bikeStationDao, bikeSystemNetworkDataSource)
                    Log.d(TAG, "Made new repository")
                }
            }
            return sInstance!!
        }
    }
}