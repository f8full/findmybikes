package com.ludoscity.findmybikes.ui.sheet;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.ludoscity.findmybikes.R;
import com.ludoscity.findmybikes.activities.NearbyActivity;
import com.ludoscity.findmybikes.ui.main.NearbyActivityViewModel;

/**
 * Created by F8Full on 2016-06-03.
 * extends library provided class for sheet to support 'editable' mode for the whole sheet
 */
public class EditableMaterialSheetFab extends MaterialSheetFab
                        implements View.OnClickListener{

    public interface OnFavoriteSheetEventListener {
        void onFavoriteSheetEditDone();
        void onFavoriteSheetEditCancel();
    }

    private OnFavoriteSheetEventListener mListener;

    private FloatingActionButton mEditFAB;
    private FloatingActionButton mEditDoneFAB;

    private NearbyActivityViewModel mNearbyActivityViewModel;
    /**
     * Creates a MaterialSheetFab instance and sets up the necessary click listeners.
     *
     * @param view       The FAB view.
     * @param sheet      The sheet view.
     * @param overlay    The overlay view.
     * @param sheetColor The background color of the material sheet.
     * @param fabColor   The background color of the FAB.
     */
    public EditableMaterialSheetFab(NearbyActivity isFavoriteSheetItemNameEditInProgress, NearbyActivityViewModel nearbyActivityViewModel, View view, View sheet, View overlay, int sheetColor, int fabColor, OnFavoriteSheetEventListener _listener) {
        //noinspection unchecked
        super(view, sheet, overlay, sheetColor, fabColor);
        mEditFAB = sheet.findViewById(R.id.favorite_sheet_edit_fab);
        mEditFAB.setOnClickListener(this);  //TODO: Consider making the fragment the listener ?

        mEditDoneFAB = sheet.findViewById(R.id.favorite_sheet_edit_done_fab);
        mEditDoneFAB.setOnClickListener(this);

        mNearbyActivityViewModel = nearbyActivityViewModel;

        mNearbyActivityViewModel.isFavoriteSheetEditInProgress().observe(isFavoriteSheetItemNameEditInProgress, isSheetEditing -> {

            if (mEditFAB.getVisibility() == View.INVISIBLE && mEditDoneFAB.getVisibility() == View.INVISIBLE)
                return;

            if (isSheetEditing != null && isSheetEditing) {
                mEditFAB.hide();
                mEditDoneFAB.show();
            } else {
                mEditDoneFAB.hide();
                mEditFAB.show();
            }
        });



        mListener = _listener;
    }

    public EditableMaterialSheetFab(NearbyActivityViewModel nearbyActivityViewModel, View view, View sheet, View overlay, int sheetColor, int fabColor, OnFavoriteSheetEventListener _listener) {
        //noinspection unchecked
        super(view, sheet, overlay, sheetColor, fabColor);
        mEditFAB = sheet.findViewById(R.id.favorite_sheet_edit_fab);
        mEditFAB.setOnClickListener(this);  //TODO: Consider making the fragment the listener ?

        mEditDoneFAB = sheet.findViewById(R.id.favorite_sheet_edit_done_fab);
        mEditDoneFAB.setOnClickListener(this);

        mNearbyActivityViewModel = nearbyActivityViewModel;

        mListener = _listener;
    }

    public void hideEditFab(){ mEditFAB.hide(); }
    public void showEditFab(){ mEditFAB.show(); }

    public void showEditDoneFab() {
        mEditDoneFAB.show();
    }

    public void hideEditDoneFab() {
        mEditDoneFAB.hide();
    }
    /*public void scrollToTop(){

        //from http://stackoverflow.com/questions/27757892/recyclerview-no-animation-on-notifyiteminsert
        if (((LinearLayoutManager)mFavRecyclerview.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == 0)
            mFavRecyclerview.getLayoutManager().scrollToPosition(0);
        else if (mFavRecyclerview.getAdapter().getItemCount() > 1)
            mFavRecyclerview.smoothScrollToPosition(0);}*/

    @Override
    public void hideSheet() {

        if (mEditDoneFAB.getVisibility() == View.VISIBLE){

            mListener.onFavoriteSheetEditCancel();

            mNearbyActivityViewModel.favoriteSheetEditDone();

            mEditDoneFAB.hide();
            mEditFAB.show();
        }

        super.hideSheet();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.favorite_sheet_edit_fab:
                mNearbyActivityViewModel.favoriteSheetEditStart();

                break;
            case R.id.favorite_sheet_edit_done_fab:
                mNearbyActivityViewModel.favoriteSheetEditDone();

                //TODO : Do what was done in NearbyActivity .onFavoriteSheetEditDone()
                //dropping all favorites
                //adding them all back from the DBHelper (!)
                //or modelView will allow update live one element by element
                mListener.onFavoriteSheetEditDone();
                break;
        }
    }
}
