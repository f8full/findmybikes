package com.ludoscity.findmybikes.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.ludoscity.findmybikes.R;
import com.ludoscity.findmybikes.StationItem;
import com.ludoscity.findmybikes.StationRecyclerViewAdapter;
import com.ludoscity.findmybikes.utils.DividerItemDecoration;
import com.ludoscity.findmybikes.utils.ScrollingLinearLayoutManager;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.NO_POSITION;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

public class StationListFragment extends Fragment
            implements StationRecyclerViewAdapter.OnStationListItemClickListener{

    public static final String STATION_LIST_ITEM_CLICK_PATH = "station_list_item_click";
    public static final String STATION_LIST_FAVORITE_FAB_CLICK_PATH = "station_list_fav_fab_click";
    public static final String STATION_LIST_DIRECTIONS_FAB_CLICK_PATH = "station_list_dir_fab_click";
    public static final String STATION_LIST_ARG_BACKGROUND_RES_ID = "station_list_arg_background_res_id";

    private RecyclerView mStationRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mRecyclerViewScrollingState = SCROLL_STATE_IDLE;
    private TextView mEmptyListTextView;
    private ImageView mProximityHeaderImageView;
    private TextView mAvailabilityTextView;

    private OnStationListFragmentInteractionListener mListener;

    private StationRecyclerViewAdapter getStationRecyclerViewAdapter(){
        return (StationRecyclerViewAdapter)mStationRecyclerView.getAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView =  inflater.inflate(R.layout.fragment_station_list, container, false);
        mEmptyListTextView = (TextView) inflatedView.findViewById(R.id.empty_list_text);
        mStationRecyclerView = (RecyclerView) inflatedView.findViewById(R.id.station_list_recyclerview);
        mStationRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        //mStationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mStationRecyclerView.setLayoutManager(new ScrollingLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false, 300));
        mStationRecyclerView.setAdapter(new StationRecyclerViewAdapter(this, getContext()));
        mStationRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mRecyclerViewScrollingState = newState;

            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) inflatedView.findViewById(R.id.station_list_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener((SwipeRefreshLayout.OnRefreshListener) getActivity());
        mSwipeRefreshLayout.setColorSchemeResources(R.color.stationlist_refresh_spinner_green, R.color.stationlist_refresh_spinner_yellow, R.color.stationlist_refresh_spinner_red);

        mAvailabilityTextView = (TextView) inflatedView.findViewById(R.id.availability_header);
        mProximityHeaderImageView = (ImageView) inflatedView.findViewById(R.id.proximity_header);

        Bundle args = getArguments();
        if (args != null){

            mStationRecyclerView.setBackground(ContextCompat.getDrawable(this.getContext(), args.getInt(STATION_LIST_ARG_BACKGROUND_RES_ID)));
            mProximityHeaderImageView.setVisibility(View.GONE);
        }

        return inflatedView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStationListFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("selected_pos", getStationRecyclerViewAdapter().getSelectedPos());
        outState.putParcelable("sort_reference_LatLng", getStationRecyclerViewAdapter().getSortReferenceLatLng());
        outState.putParcelable("distance_reference_latlng", getStationRecyclerViewAdapter().getDistanceReferenceLatLng());
        outState.putString("string_if_empty", mEmptyListTextView.getText().toString());
        outState.putBoolean("empty_string_visible", mEmptyListTextView.getVisibility() == View.VISIBLE);

        getStationRecyclerViewAdapter().saveStationList(outState);
        getStationRecyclerViewAdapter().saveLookingForBike(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {

            int selectedPos = savedInstanceState.getInt("selected_pos");

            if (selectedPos != NO_POSITION)
                getStationRecyclerViewAdapter().setSelectedPos(selectedPos, false);

            LatLng sortReferenceLatLng = savedInstanceState.getParcelable("sort_reference_LatLng");
            LatLng distanceReferenceLatLng = savedInstanceState.getParcelable("distance_reference_latlng");
            ArrayList<StationItem> stationList = savedInstanceState.getParcelableArrayList("stationitem_arraylist");

            if (!savedInstanceState.getBoolean("empty_string_visible"))
                mEmptyListTextView.setVisibility(View.GONE);

            setupUI(stationList, savedInstanceState.getBoolean("looking_for_bike"), savedInstanceState.getString("string_if_empty"),
                    sortReferenceLatLng, distanceReferenceLatLng);
        }
    }

    public void setupUI(ArrayList<StationItem> stationsNetwork, boolean lookingForBike, String stringIfEmpty,
                        LatLng _sortReferenceLatLng, LatLng _distanceReferenceLatLng) {

        boolean restoreEmptyStringGone = getStationRecyclerViewAdapter().getItemCount()== 0 &&
                mEmptyListTextView.getVisibility() == View.GONE;

        if (stationsNetwork != null) {

            if (!stationsNetwork.isEmpty()) {
                mStationRecyclerView.setVisibility(View.VISIBLE);
                mEmptyListTextView.setVisibility(View.GONE);
            }
            else{
                mStationRecyclerView.setVisibility(View.GONE);
                mEmptyListTextView.setText(stringIfEmpty);
                if (!restoreEmptyStringGone)
                    mEmptyListTextView.setVisibility(View.VISIBLE);
                else
                    mEmptyListTextView.setVisibility(View.GONE);
            }

            getStationRecyclerViewAdapter().setupStationList(stationsNetwork, _sortReferenceLatLng, _distanceReferenceLatLng);
            lookingForBikes(lookingForBike);
        }
    }

    public void hideEmptyString(){
        mEmptyListTextView.setVisibility(View.GONE);
    }

    public void showEmptyString(){
        mEmptyListTextView.setVisibility(View.VISIBLE);
    }

    public void setDistanceSortReferenceLatLngAndSort(LatLng _toSet) {

        if (mRecyclerViewScrollingState == SCROLL_STATE_IDLE) {

            getStationRecyclerViewAdapter().setDistanceSortReferenceLatLngAndSortIfRequired(_toSet, false);
        }
    }

    public void setDistanceDisplayReferenceLatLng(LatLng _toSet, boolean _notify) {
        getStationRecyclerViewAdapter().setDistanceDisplayReferenceLatLng(_toSet, _notify);
    }

    public String highlightClosestStationWithAvailability(boolean _lookingForBike){

        String closestWithAvailability = getStationRecyclerViewAdapter().getClosestStationWithAvailability(_lookingForBike);

        highlightStation(closestWithAvailability);

        return closestWithAvailability;

    }

    public boolean isRecyclerViewReadyForItemSelection(){
        return mStationRecyclerView != null && getStationRecyclerViewAdapter().getSortReferenceLatLng() != null &&
                ((ScrollingLinearLayoutManager)mStationRecyclerView.getLayoutManager()).findFirstVisibleItemPosition() !=
                        NO_POSITION;
    }

    public boolean highlightStation(String _stationId) {

        int selectedPos = getStationRecyclerViewAdapter().setSelection(_stationId, false);

        ((StationRecyclerViewAdapter)mStationRecyclerView.getAdapter()).requestFabAnimation();

        return selectedPos != NO_POSITION;
    }

    public StationItem getHighlightedStation(){

        return getStationRecyclerViewAdapter().getSelected();
    }

    public void removeStationHighlight(){
        getStationRecyclerViewAdapter().clearSelection();
    }

    public void lookingForBikes(boolean lookingForBike) {

        getStationRecyclerViewAdapter().lookingForBikesNotify(lookingForBike);

        if (lookingForBike) {

            mAvailabilityTextView.setText(getString(R.string.bikes));

            if (getArguments()!= null){
                mProximityHeaderImageView.setVisibility(View.GONE);
            }
            else {
                mProximityHeaderImageView.setVisibility(View.VISIBLE);
                mProximityHeaderImageView.setImageResource(R.drawable.ic_loc_walk);
            }
        }
        else {

            mAvailabilityTextView.setText(getString(R.string.docks));

            if (getArguments()!= null){
                mProximityHeaderImageView.setVisibility(View.GONE);
            }
            else {
                mProximityHeaderImageView.setVisibility(View.VISIBLE);
                mProximityHeaderImageView.setImageResource(R.drawable.ic_a_bike);
            }
        }
    }

    @Override
    public void onStationListItemClick(String _path) {
        Uri.Builder builder = new Uri.Builder();

        builder.appendPath(_path);

        if (mListener != null) {
            mListener.onStationListFragmentInteraction(builder.build());
        }
    }

    public void setRefreshing(boolean toSet) {
        if (toSet != mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(toSet);
    }

    public void setRefreshEnable(boolean toSet) {
        mSwipeRefreshLayout.setEnabled(toSet);
    }

    public void removeStation(StationItem toRemove, String stringIfEmpty) {
        if (getStationRecyclerViewAdapter().removeItem(toRemove)){
            mStationRecyclerView.setVisibility(View.GONE);
            mEmptyListTextView.setText(stringIfEmpty);
            mEmptyListTextView.setVisibility(View.VISIBLE);
        }
    }

    public void addStation(StationItem toAdd) {
        mEmptyListTextView.setVisibility(View.GONE);
        mStationRecyclerView.setVisibility(View.VISIBLE);
        getStationRecyclerViewAdapter().addItem(toAdd);
    }

    public void smoothScrollSelectionInView(boolean _appBarExpanded) {
        if (_appBarExpanded && getStationRecyclerViewAdapter().getSelectedPos() >=
                ((LinearLayoutManager)mStationRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition()){
            mStationRecyclerView.smoothScrollToPosition(getStationRecyclerViewAdapter().getSelectedPos()+1);
        }
        else
            mStationRecyclerView.smoothScrollToPosition(getStationRecyclerViewAdapter().getSelectedPos());
    }

    public interface OnStationListFragmentInteractionListener {

        void onStationListFragmentInteraction(Uri uri);
    }

}
