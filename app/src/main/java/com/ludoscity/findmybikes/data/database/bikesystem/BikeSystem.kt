package com.ludoscity.findmybikes.data.database.bikesystem

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BikeSystem(
        @PrimaryKey
        val id: String,
        @ColumnInfo(name = "last_status_update")
        val lastStatusUpdateLocalTimestamp: Long,
        @ColumnInfo(name = "citybik_dot_es_url")
        val citybikDOTesUrl: String,
        val name: String,
        val hashtaggableName: String,
        val latitude: Double,
        val longitude: Double,
        val city: String,
        val country: String,
        //TODO: add a companies table and reference foreign key from here
        @ColumnInfo(name = "bbox_north_east_lat")
        val boundingBoxNorthEastLatitude: Double?,
        @ColumnInfo(name = "bbox_north_east_lng")
        val boundingBoxNorthEastLongitude: Double?,
        @ColumnInfo(name = "bbox_south_west_lat")
        val boundingBoxSouthWestLatitude: Double?,
        @ColumnInfo(name = "bbox_south_west_lng")
        val boundingBoxSouthWestLongitude: Double?)