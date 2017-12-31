package com.ludoscity.findmybikes.citybik_es.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.SphericalUtil;

@SuppressWarnings("unused")
@Entity
public class BikeStation {//implements Parcelable {

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
        return extra.getName();
    }

    public void setName(String name) {
        extra.setName(name);
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getMeterFromLatLng(LatLng userLocation) {
        return SphericalUtil.computeDistanceBetween(userLocation, getLocation());}

    public LatLng getLocation() {return new LatLng(latitude,longitude);}

    public boolean isLocked() {
        return extra.getLocked();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //test data
    //Laurier / Brebeuf
        /*if (_bikeStation.id.equalsIgnoreCase("f132843c3c740cce6760167985bc4d17")){
            this.empty_slots = 35;
            this.free_bikes = 0;

            //Lanaudiere / Laurier
        }else if (_bikeStation.id.equalsIgnoreCase("92d97d6adec177649b366c36f3e8e2ff")){
            this.empty_slots = 17;
            this.free_bikes = 2;

        }else if (_bikeStation.id.equalsIgnoreCase("d20fea946f06e7e64e6da7d95b3c3a89")){
            this.empty_slots = 1;
            this.free_bikes = 19;
        }else if (_bikeStation.id.equalsIgnoreCase("3500704c9971a0c13924e696f5804bbd")){
            this.empty_slots = 0;
            this.free_bikes = 31;
        } else {*/
}
