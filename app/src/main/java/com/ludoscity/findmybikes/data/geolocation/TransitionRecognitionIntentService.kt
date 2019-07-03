package com.ludoscity.findmybikes.data.geolocation

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import com.ludoscity.findmybikes.data.FindMyBikesRepository
import com.ludoscity.findmybikes.data.database.tracking.AnalTrackingDatapoint
import com.ludoscity.findmybikes.utils.InjectorUtils

/**
 * Created by F8Full on 2019-07-02. This file is part of #findmybikes
 * An intent service class to monitor user activities transitions via activity transition recognition API
 * https://developer.android.com/guide/topics/location/transitions
 * The API is passed a pending intent pointing to this service. When an activity transition occurs
 * onHandleIntent is called, from which transitions can be extracted.
 */
class TransitionRecognitionIntentService : IntentService("TransitionRecognitionIntentService") {

    companion object {
        private val TAG = TransitionRecognitionIntentService::class.java.simpleName
    }

    private lateinit var repo: FindMyBikesRepository

    override fun onCreate() {
        repo = InjectorUtils.provideRepository(application)
        super.onCreate()
    }

    override fun onHandleIntent(p0: Intent?) {
        p0?.let { intent ->
            //TODO: check intent.getAction
            Log.d("TransitionsIntentServic", "onHandleIntent")

            if (ActivityTransitionResult.hasResult(intent)) {
                val result = ActivityTransitionResult.extractResult(intent)
                result?.transitionEvents?.forEach {
                    when (it.activityType) {
                        DetectedActivity.STILL -> {
                            Log.d("TransitionsIntentServic", "STILL")
                            if (it.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                                Log.d("TransitionsIntentServic", "ACTIVITY_TRANSITION_ENTER")
                                repo.insertInDatabase(AnalTrackingDatapoint(
                                        description = "TransitionRecognitionIntentService--STILL-ACTIVITY_TRANSITION_ENTER"
                                ))
                            } else {
                                Log.d("TransitionsIntentServic", "ACTIVITY_TRANSITION_EXIT")
                                repo.insertInDatabase(AnalTrackingDatapoint(
                                        description = "TransitionRecognitionIntentService--STILL-ACTIVITY_TRANSITION_EXIT"
                                ))
                            }

                        }
                        DetectedActivity.WALKING -> {
                            Log.d("TransitionsIntentServic", "WALKING")
                            if (it.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                                Log.d("TransitionsIntentServic", "ACTIVITY_TRANSITION_ENTER")
                                repo.insertInDatabase(AnalTrackingDatapoint(
                                        description = "TransitionRecognitionIntentService--WALKING-ACTIVITY_TRANSITION_ENTER"
                                ))
                            } else {
                                Log.d("TransitionsIntentServic", "ACTIVITY_TRANSITION_EXIT")
                                repo.insertInDatabase(AnalTrackingDatapoint(
                                        description = "TransitionRecognitionIntentService--WALKING-ACTIVITY_TRANSITION_EXIT"
                                ))
                            }

                        }
                        DetectedActivity.RUNNING -> {
                            Log.d("TransitionsIntentServic", "RUNNING")
                            if (it.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                                Log.d("TransitionsIntentServic", "ACTIVITY_TRANSITION_ENTER")
                                repo.insertInDatabase(AnalTrackingDatapoint(
                                        description = "TransitionRecognitionIntentService--RUNNING-ACTIVITY_TRANSITION_ENTER"
                                ))
                            } else {
                                Log.d("TransitionsIntentServic", "ACTIVITY_TRANSITION_EXIT")
                                repo.insertInDatabase(AnalTrackingDatapoint(
                                        description = "TransitionRecognitionIntentService--WALKING-ACTIVITY_TRANSITION_EXIT"
                                ))
                            }

                        }
                        DetectedActivity.ON_BICYCLE -> {
                            Log.d("TransitionsIntentServic", "ON_BICYCLE")
                            if (it.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                                Log.d("TransitionsIntentServic", "ACTIVITY_TRANSITION_ENTER")
                                repo.startTrackingGeolocation()
                                repo.insertInDatabase(AnalTrackingDatapoint(
                                        description = "$TAG::onHandleIntent--ON_BICYCLE-ACTIVITY_TRANSITION_ENTER-startTrackingGeolocation"
                                ))
                            } else {
                                Log.d("TransitionsIntentServic", "ACTIVITY_TRANSITION_EXIT")
                                repo.stopTrackingGeolocaiton()
                                repo.insertInDatabase(AnalTrackingDatapoint(
                                        description = "$TAG::onHandleIntent--ON_BICYCLE-ACTIVITY_TRANSITION_EXIT-stopTrackingGeolocation"
                                ))
                            }

                        }
                    }
                }
            }
        }
    }
}