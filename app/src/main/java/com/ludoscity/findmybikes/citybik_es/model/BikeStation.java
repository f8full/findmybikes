package com.ludoscity.findmybikes.citybik_es.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.ludoscity.findmybikes.StationItem;

@SuppressWarnings("unused")
@Entity
public class BikeStation {

    public BikeStation() {}

    @SuppressWarnings("NullableProblems")
    @SerializedName("id")
    @ColumnInfo(name = "location_hash")
    @PrimaryKey
    @NonNull
    private String locationHash; //used as a uid

    @ColumnInfo(name = "empty_slots")
    @SerializedName("empty_slots")
    private Integer emptySlots;
    @Embedded(prefix = "extra_")
    private BikeStationExtra extra;
    @ColumnInfo(name = "free_bikes")
    @SerializedName("free_bikes")
    private Integer freeBikes;
    @ColumnInfo(name = "latitude")
    private Double latitude;
    @ColumnInfo(name = "longitude")
    private Double longitude;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "timestamp")
    private String timestamp;

    public BikeStation(StationItem toSave) {
        this.locationHash = toSave.getId();
        this.emptySlots = toSave.getEmpty_slots();
        this.extra = new BikeStationExtra(toSave.isLocked(), toSave.getName());
        this.freeBikes = toSave.getFree_bikes();
        this.latitude = toSave.getLocation().latitude;
        this.longitude = toSave.getLocation().longitude;
        this.name = toSave.getName();
        this.timestamp = toSave.getTimestamp();
    }

    @NonNull
    public String getLocationHash() {
        return locationHash;
    }

    public void setLocationHash(@NonNull String locationHash) {
        this.locationHash = locationHash;
    }

    public Integer getEmptySlots() {
        return emptySlots;
    }

    public void setEmptySlots(Integer empty_slots) {
        this.emptySlots = empty_slots;
    }

    public BikeStationExtra getExtra() {
        return extra;
    }

    public void setExtra(BikeStationExtra bikeStationExtra) {
        this.extra = bikeStationExtra;
    }

    public Integer getFreeBikes() {
        return freeBikes;
    }

    public void setFreeBikes(Integer freeBikes) {
        this.freeBikes = freeBikes;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}