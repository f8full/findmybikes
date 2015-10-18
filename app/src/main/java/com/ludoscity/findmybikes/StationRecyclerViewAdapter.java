package com.ludoscity.findmybikes;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.support.v7.widget.RecyclerView.NO_POSITION;

/**
 * Created by Gevrai on 2015-04-03.
 *
 * Adapter used to show the datas of every stationItem
 */
public class StationRecyclerViewAdapter extends RecyclerView.Adapter<StationRecyclerViewAdapter.StationListItemViewHolder> {

    private ArrayList<StationItem> mStationList = new ArrayList<>();
    private LatLng mCurrentUserLatLng;

    private boolean mIsLookingForBikes;

    private int mSelectedPos = NO_POSITION;

    private OnStationListItemClickListener mListener;

    public void saveStationList(Bundle outState) {
        outState.putParcelableArrayList("stationitem_arraylist", mStationList);
    }

    public interface OnStationListItemClickListener {
        void onStationListItemClick();
    }

    public StationRecyclerViewAdapter(OnStationListItemClickListener listener){
        super();
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(StationListItemViewHolder holder, int position) {

        holder.bindStation(mStationList.get(position), position == mSelectedPos);
    }

    @Override
    public StationListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stationlist_item, parent, false);
        return new StationListItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mStationList.size();
    }

    public class StationListItemViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener{

        TextView mDistance;
        TextView mName;
        TextView mAvailability;

        public StationListItemViewHolder(View itemView) {
            super(itemView);

            mDistance = (TextView) itemView.findViewById(R.id.station_distance);
            mName = (TextView) itemView.findViewById(R.id.station_name);
            mAvailability = (TextView) itemView.findViewById(R.id.station_availability);
            itemView.setOnClickListener(this);
        }

        public void bindStation(StationItem station, boolean selected){

            mName.setText(station.getName());

            if (mCurrentUserLatLng != null) {
                mDistance.setVisibility(View.VISIBLE);
                mDistance.setText(station.getDistanceStringFromLatLng(mCurrentUserLatLng));
            } else {
                mDistance.setVisibility(View.GONE);
            }

            if (mIsLookingForBikes) {
                mAvailability.setText(String.valueOf(station.getFree_bikes()));
                setBackgroundColor(selected, station.getFree_bikes());

            }
            else {
                mAvailability.setText(String.valueOf(station.getEmpty_slots()));
                setBackgroundColor(selected, station.getEmpty_slots());
            }
        }

        private void setBackgroundColor(boolean selected, int availabilityValue){
            if (!selected)
                itemView.setBackgroundResource(android.R.color.transparent);
            else{
                if (availabilityValue == 0)
                    itemView.setBackgroundResource(R.color.stationlist_item_background_red);
                else if (availabilityValue < 3)
                    itemView.setBackgroundResource(R.color.stationlist_item_background_yellow);
                else
                    itemView.setBackgroundResource(R.color.stationlist_item_background_green);
            }
        }

        @Override
        public void onClick(View view) {
            StationRecyclerViewAdapter.this.setSelectionFromName(mName.getText().toString(), true);
            mListener.onStationListItemClick();
        }
    }

    public void setStationList(ArrayList<StationItem> toSet){
        String selectedNameBefore = null;

        if (null != getSelected())
            selectedNameBefore = getSelected().getName();

        //Making a copy as sorting shouldn't interfere with the rest of the code
        mStationList.clear();
        mStationList.addAll(toSet);

        if (selectedNameBefore != null)
            setSelectionFromName(selectedNameBefore, false);
    }

    public void setCurrentUserLatLng(LatLng currentUserLatLng, boolean notify) {
        if (mCurrentUserLatLng != currentUserLatLng) {
            this.mCurrentUserLatLng = currentUserLatLng;
            sortStationListByClosest();
            if (notify)
                notifyDataSetChanged();
        }
    }

    public LatLng getCurrentUserLatLng(){
        return mCurrentUserLatLng;
    }

    public int setSelectionFromName(String stationName, boolean unselectOnTwice){

        return setSelectedPos(getPositionInList(stationName), unselectOnTwice);
    }

    public StationItem getSelected(){
        StationItem toReturn = null;

        if (mSelectedPos != NO_POSITION)
            toReturn = mStationList.get(mSelectedPos);

        return toReturn;
    }

    public int getSelectedPos(){
        return mSelectedPos;
    }

    public void clearSelection(){
        int selectedBefore = mSelectedPos;
        mSelectedPos = NO_POSITION;

        if (selectedBefore != NO_POSITION)
            notifyItemChanged(selectedBefore);
    }



    private int getPositionInList(String stationName){

        int toReturn = NO_POSITION;

        int i=0;
        for (StationItem stationItem: mStationList){
            if (stationItem.getName().equals(stationName)) {
                toReturn = i;
                break;
            }
            ++i;
        }
        return toReturn;
    }

    public int setSelectedPos(int pos, boolean unselectedOnTwice){

        int toReturn = NO_POSITION;

        if (mSelectedPos == pos)
            if (unselectedOnTwice)
                clearSelection();
            else
                toReturn = mSelectedPos;
        else {
            notifyItemChanged(mSelectedPos);
            mSelectedPos = pos;
            notifyItemChanged(pos);
            toReturn = mSelectedPos;
        }

        return toReturn;
    }

    public void lookingForBikesNotify(boolean isLookingForBikes) {
        if (mIsLookingForBikes != isLookingForBikes){
            mIsLookingForBikes = isLookingForBikes;
            notifyDataSetChanged();
        }
    }

    public void sortStationListByClosest(){

        String selectedNameBefore = null;

        if (null != getSelected())
            selectedNameBefore = getSelected().getName();

        if (mCurrentUserLatLng != null) {
            Collections.sort(mStationList, new Comparator<StationItem>() {
                @Override
                public int compare(StationItem lhs, StationItem rhs) {
                    return (int) (lhs.getMeterFromLatLng(mCurrentUserLatLng) - rhs.getMeterFromLatLng(mCurrentUserLatLng));
                }
            });
        }

        if (selectedNameBefore != null)
            setSelectionFromName(selectedNameBefore, false);
    }
}