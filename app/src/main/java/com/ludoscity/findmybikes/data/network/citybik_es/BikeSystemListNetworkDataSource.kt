package com.ludoscity.findmybikes.data.network.citybik_es

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ludoscity.findmybikes.common.presentation.data.datasource.remote.citybik_es.BikeSystemListAnswerRoot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class BikeSystemListNetworkDataSource private constructor() {

    private val coroutineScopeIO = CoroutineScope(Dispatchers.IO)

    private val downloadedBikeSystemList: MutableLiveData<BikeSystemListAnswerRoot> = MutableLiveData()

    val data: LiveData<BikeSystemListAnswerRoot>
        get() = downloadedBikeSystemList

    fun startFetchingBikeSystemListService(ctx: Context) {
        val intentToFetch = Intent(ctx, FetchCitybikDOTesDataIntentService::class.java)
        intentToFetch.action = FetchCitybikDOTesDataIntentService.ACTION_FETCH_SYSTEM_LIST

        FetchCitybikDOTesDataIntentService.enqueueWork(ctx, intentToFetch)
    }

    fun fetchBikeSystemList(citybik_esAPI: Citybik_esAPI) {
        coroutineScopeIO.launch {
            val call = citybik_esAPI.bikeNetworkList

            val listAnswerRootAnswerRootAnswer: Response<BikeSystemListAnswerRoot>

            try {
                listAnswerRootAnswerRootAnswer = call.execute()
                downloadedBikeSystemList.postValue(listAnswerRootAnswerRootAnswer.body()!!)
            } catch (e: IOException) {
                //server level error, could not retrieve bike system data
                Log.w(TAG, "Exception raised trying to fetch bike system list -- Aborted", e)
            }
        }
    }

    companion object {
        private val TAG = BikeSystemListNetworkDataSource::class.java.simpleName

        // For Singleton instantiation
        private val LOCK = Any()
        private var sInstance: BikeSystemListNetworkDataSource? = null

        /**
         * Get the singleton for this class
         */
        fun getInstance(): BikeSystemListNetworkDataSource {
            //Log.d(TAG, "Getting bike system list network data source")
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance =
                        BikeSystemListNetworkDataSource()
                    Log.d(TAG, "Made new bike system list network data source")
                }
            }
            return sInstance!!
        }
    }
}