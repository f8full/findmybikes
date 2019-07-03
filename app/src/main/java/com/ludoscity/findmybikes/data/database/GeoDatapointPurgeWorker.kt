package com.ludoscity.findmybikes.data.database

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ludoscity.findmybikes.utils.InjectorUtils

/**
 * Created by F8Full on 2019-06-30. This file is part of #findmybikes
 *
 */
class GeoDatapointPurgeWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Do the work here--in this case, purge the data
        val repo = InjectorUtils.provideRepository(applicationContext)

        Log.i(GeoDatapointPurgeWorker::class.java.simpleName, "About to purge geolocation table")
        repo.purgeGeoTable()

        // Indicate whether the task finished successfully with the Result
        return Result.success()
    }
}