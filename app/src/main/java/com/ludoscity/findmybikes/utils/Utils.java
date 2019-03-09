package com.ludoscity.findmybikes.utils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.ludoscity.findmybikes.R;
import com.ludoscity.findmybikes.activities.WebViewActivity;
import com.ludoscity.findmybikes.ui.page.StationPageRecyclerViewAdapter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by F8Full on 2015-04-30.
 *
 * Class with static utilities
 */
public class Utils {

    public static String extractClosestAvailableStationIdFromProcessedString(String _processedString){

        //int debug0 = _processedString.indexOf(StationPageRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX);
        //int debug1 = StationPageRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX.length();
        //int debug2 = _processedString.length();


        //Either a station id followed by _AVAILABILITY_AOK
        //or
        //a station id followed by _AVAILABILITY_BAD
        //or
        //a station id followed by _AVAILABILITY_LCK

        //extract only first id
        String firstId = extractOrderedStationIdsFromProcessedString(_processedString).get(0);

        return firstId.length()>=32 ? firstId.substring(0, 32) : "";






        //everything went AOK
        /*if (_processedString.indexOf(StationPageRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX) != -1 &&
                _processedString.indexOf(StationPageRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX) + StationPageRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX.length() ==
                        _processedString.length()){

            return _processedString.substring(0, _processedString.length() - StationPageRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX.length() );

        }
        else {
            int debug3 = _processedString.lastIndexOf(StationPageRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE);

            //some availability troubles, let's just trim the end
            return _processedString.substring(0, _processedString.lastIndexOf(StationPageRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE));
        }*/
    }

    //citybik.es Ids, ordered by distance
    //get(0) is the id of the selected station with BAD or AOK availability
    public static List<String> extractOrderedStationIdsFromProcessedString(String _processedString){

        if (_processedString.isEmpty()){
            List<String> toReturn = new ArrayList<>();
            toReturn.add(_processedString);

            return toReturn;
        }

        //int startSequenceIdx = _processedString.lastIndexOf(StationPageRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE);

        /*int subStringStarIdxDebug = _processedString.lastIndexOf(StationPageRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE)
                + StationPageRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE.length();*/

        /*String subStringDebug = _processedString.substring(_processedString.lastIndexOf(StationPageRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE)
                + StationPageRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE.length());*/


        //TODO: something is fishy here, couldn't figure out how to get the same result without intermediary debug labelled variable
        String debugSplit = _processedString.substring(_processedString.indexOf(StationPageRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE)
                + StationPageRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX.length());

        //String[] debugSplitResult = debugSplit.split(String.format("(?<=\\G.{%d})", StationPageRecyclerViewAdapter.CRITICAL_AVAILABILITY_POSTFIX.length() + 32));

        List<String> toReturn = new ArrayList<>();
        toReturn.add(_processedString.substring(0, 32 + StationPageRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX.length()));

        toReturn.addAll(splitEqually(debugSplit, StationPageRecyclerViewAdapter.CRITICAL_AVAILABILITY_POSTFIX.length() + 32));

        return toReturn;
    }

    private static List<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        List<String> ret = new ArrayList<>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    //workaround from https://code.google.com/p/gmaps-api-issues/issues/detail?id=9011
    public static BitmapDescriptor getBitmapDescriptor(Context ctx, int id) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(ctx.getResources(), id, null);
        Bitmap bm = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    public static float map(float x, float in_min, float in_max, float out_min, float out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static Intent getWebIntent(Context _ctx, String _url, boolean _javascriptEnabled, String _webViewTitle){
        Intent toReturn;
        //android system webview
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            toReturn = new Intent(_ctx, WebViewActivity.class);
            toReturn.putExtra(WebViewActivity.EXTRA_URL, _url);
            toReturn.putExtra(WebViewActivity.EXTRA_ACTIONBAR_SUBTITLE, _webViewTitle);
            toReturn.putExtra(WebViewActivity.EXTRA_JAVASCRIPT_ENABLED, _javascriptEnabled);
        }
        //browser
        else{
            toReturn = new Intent(Intent.ACTION_VIEW, Uri.parse(_url));
        }

        return toReturn;
    }

    public static int dpToPx(float toConvert, Context ctx){
        /// Converts 66 dip into its equivalent px
        Resources r = ctx.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, toConvert, r.getDisplayMetrics());
    }

    //http://stackoverflow.com/questions/3282390/add-floating-point-value-to-android-resources-values
    public static float getAverageWalkingSpeedKmh(Context ctx){
        TypedValue outValue = new TypedValue();

        ctx.getResources().getValue(R.dimen.average_walking_speed_kmh, outValue, true);

        return outValue.getFloat();
    }

    public static float getAverageBikingSpeedKmh(Context ctx){
        TypedValue outValue = new TypedValue();

        ctx.getResources().getValue(R.dimen.average_biking_speed_kmh, outValue, true);

        return outValue.getFloat();
    }

    public static String getWalkingProximityString(LatLng _from, LatLng _to, boolean _2digitsFormat, NumberFormat _nf, Context _ctx ){
        return getProximityString(_from, _to, getAverageWalkingSpeedKmh(_ctx), _2digitsFormat, _nf, _ctx);
    }

    public static String getBikingProximityString(LatLng _from, LatLng _to, boolean _2digitsFormat, NumberFormat _nf, Context _ctx ){
        return getProximityString(_from, _to, getAverageBikingSpeedKmh(_ctx), _2digitsFormat, _nf, _ctx);
    }

    private static String getProximityString(LatLng _from, LatLng _to, float _speedKmh, boolean _2digitsFormat, NumberFormat _nf, Context _ctx){

        if (_from == null || _to == null)
            return "";

        return durationToProximityString(computeTimeBetweenInMinutes(_from, _to, _speedKmh), _2digitsFormat, _nf, _ctx);
    }

    public static String durationToProximityString(int _durationMinute, boolean _2digitsFormat, NumberFormat _nf, Context _ctx){

        String toReturn;

        NumberFormat nf = _nf;

        if (nf == null)
            nf = NumberFormat.getInstance();

        if (_2digitsFormat)
            nf.setMinimumIntegerDigits(2);
        else
            nf.setMinimumIntegerDigits(1);

        if (_durationMinute < 1)
            toReturn = "<" + nf.format(1) + _ctx.getString(R.string.min_abbreviated);
        else if (_durationMinute < 60 )
            toReturn = "~" + nf.format(_durationMinute) + _ctx.getString(R.string.min_abbreviated);
        else
            toReturn = ">"+ nf.format(1) + _ctx.getString(R.string.hour_abbreviated);

        return toReturn;
    }

    public static int computeTimeBetweenInMinutes(LatLng _from, LatLng _to, float _speedKmH){

        int distance = (int) SphericalUtil.computeDistanceBetween(_from, _to);

        float speedMetersPerH = _speedKmH * 1000f;
        float speedMetersPerS = speedMetersPerH / 3600f;

        float timeInS = distance / speedMetersPerS;

        long timeInMs = (long) (timeInS * 1000);

        return (int)(timeInMs / 1000 / 60);
    }

    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /**
     * Returns a percentage value as a float from an XML resource file. The value can be optionally
     * rounded.
     *
     * @param _ctx
     * @param _resId
     * @param _rounded
     * @return float
     */
    public static float getPercentResource(Context _ctx, int _resId, boolean _rounded){
        TypedValue valueContainer = new TypedValue();
        _ctx.getResources().getValue(_resId, valueContainer, true);
        float toReturn = valueContainer.getFraction(1, 1);//http://stackoverflow.com/questions/11734470/how-does-one-use-resources-getfraction

        if (_rounded)
            toReturn = Utils.round(toReturn,2);

        return toReturn;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    //Snackbar related utils
    public static class Snackbar{


        //A modified version of make that allows background color manipulation
        //as it is not currently supported through styles/theming
        //Should be done something like this
        /*<style name="MyCustomSnackbar" parent="Theme.AppCompat.Light">
        <item name="colorAccent">@color/theme_accent</item>
        <item name="android:textColor">@color/theme_textcolor_primary</item>
        <item name="android:background">@color/theme_primary_dark</item>
        </style>*/
        //Right now, textColor and action color are controlled through theming,
        //but not background color.
        public static android.support.design.widget.Snackbar makeStyled(@NonNull View _view, @StringRes int _textStringResId, @android.support.design.widget.Snackbar.Duration int _duration,
                                                                                  @ColorInt int _backgroundColor/*, @ColorInt int _textColor, @ColorInt int _actionTextColor*/ ){

            android.support.design.widget.Snackbar toReturn = android.support.design.widget.Snackbar.make(_view, _textStringResId, _duration );

            View snackbarView = toReturn.getView();

            //didn't use to work but maybe newer SnackBar versions will support it ?
            /*//change snackbar action text color
            toReturn.setActionTextColor(_actionTextColor);

            // change snackbar text color
            int snackbarTextId = android.support.design.R.id.snackbar_text;
            TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
            textView.setTextColor(_textColor);*/

            // change snackbar background
            snackbarView.setBackgroundColor(_backgroundColor);

            return toReturn;
        }

    }



    /**
     * Created by F8Full on 2015-03-15.
     * Used to manipulate request result metadata and avoid repetitive code
     */
    public static class Connectivity extends BroadcastReceiver{

        private static boolean mConnected = false;

        public static boolean isConnected(Context context){

            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            mConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            if (!mConnected){
                //start listening to connectivity change
                ComponentName receiver = new ComponentName(context, Connectivity.class);

                PackageManager pm = context.getPackageManager();

                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            }

            return mConnected;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();


            mConnected = extras.getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY);

            if (mConnected){
                //stop listening to connectivity change
                ComponentName receiver = new ComponentName(context, Connectivity.class);

                PackageManager pm = context.getPackageManager();

                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }
        }
    }
}