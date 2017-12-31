package com.ludoscity.findmybikes.datamodel;

import android.arch.persistence.room.Entity;

import com.google.android.gms.maps.model.LatLng;
import com.ludoscity.findmybikes.helpers.DBHelper;

/**
 * Created by F8Full on 2017-12-23. This file is part of #findmybikes
 * A data model class to handle the concept of a favorite station and save it using Room
 */
@Entity
public class FavoriteEntityStation extends FavoriteEntityBase {

    public FavoriteEntityStation(String id, String defaultName){
        super(id,defaultName);
    }

    @Override
    public String getAttributions() {
        return null;
    }

    @Override
    public LatLng getLocation() {
        return DBHelper.getInstance().getStation(getId()).getLocation();
    }
}
