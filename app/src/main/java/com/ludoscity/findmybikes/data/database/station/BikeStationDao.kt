package com.ludoscity.findmybikes.data.database.station

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Created by F8Full on 2017-12-17.
 * This file is part of #findmybikes
 */
@Dao
interface BikeStationDao {

    @get:Query("SELECT * FROM bikestation")
    val all: LiveData<List<BikeStation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBikeStationList(bikeStationList: List<BikeStation>)

    /*@Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertBikeStation(BikeStation toInsert);*/

    @Update
    fun updateBikeStation(toUpdate: BikeStation)

    @Update
    fun updateBikeStationList(vararg bikeStationList: BikeStation)

    @Delete
    fun deleteBikeStationList(vararg bikeStationList: BikeStation)

    @Query("DELETE FROM bikestation")
    fun deleteAllBikeStation()

    //TODO: review station IDs
    @Query("SELECT * FROM bikestation WHERE location_hash = :stationId")
    fun getStation(stationId: String): BikeStation

    //TODO: Add queries for inserting or removing only one BikeStation ? Or use the list one with a list of size 1 ?
    //@Insert(onConflict = REPLACE)
    //void save(BikeStation bikestation);
    //@Query("SELECT * FROM user WHERE location_hash = :locationHash")
    //LiveData<BikeStation> load(String locationHash);

}
