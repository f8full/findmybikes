package com.ludoscity.findmybikes.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import com.ludoscity.findmybikes.R
import com.ludoscity.findmybikes.ui.table.TableFragmentViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

/**
 * Created by F8Full on 2015-04-30.
 *
 * Class with static utilities
 */
object Utils {

    fun getBikeSpeedPaddedBounds(ctx: Context, boundsToPad: LatLngBounds): LatLngBounds {
        return padLatLngBounds(boundsToPad, getAverageBikingSpeedKmh(ctx).toDouble())
    }

    private fun padLatLngBounds(boundsIn: LatLngBounds, paddingKm: Double): LatLngBounds {

        //http://gis.stackexchange.com/questions/2951/algorithm-for-offsetting-a-latitude-longitude-by-some-amount-of-meters
        //http://stackoverflow.com/questions/29478463/offset-latlng-by-some-amount-of-meters-in-android
        //Latitude : easy, 1 degree = 111111m (historically because of the French :D)
        //Longitude : 1 degree = 111111 * cos (latitude)m
        val southwestPadded = LatLng(boundsIn.southwest.latitude - (paddingKm * 1000.0) / 111111.0,
                boundsIn.southwest.longitude - (paddingKm * 1000.0) / 111111.0 * Math.cos(boundsIn.southwest.latitude))
        val northeastPadded = LatLng(boundsIn.northeast.latitude + (paddingKm * 1000.0) / 111111.0,
                boundsIn.northeast.longitude + (paddingKm * 1000.0) / 111111.0 * Math.cos(boundsIn.northeast.latitude))

        return LatLngBounds(southwestPadded, northeastPadded)
    }

    fun getExponentialDelayMillis(nbAttempt: Int): Long {
        return Math.round(Math.pow(2.0, nbAttempt.toDouble())) * 1000
    }

    fun extractNearestAvailableStationIdFromDataString(_processedString: String): String {

        //int debug0 = _processedString.indexOf(StationTableRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX);
        //int debug1 = StationTableRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX.length();
        //int debug2 = _processedString.length();


        //Either a station id followed by _AVAILABILITY_AOK
        //or
        //a station id followed by _AVAILABILITY_BAD
        //or
        //a station id followed by _AVAILABILITY_LCK

        //extract only first id
        val firstId = extractOrderedStationIdsFromProcessedString(_processedString)[0]

        return if (firstId.length >= 32) firstId.substring(0, 32) else ""


        //everything went AOK
        /*if (_processedString.indexOf(StationTableRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX) != -1 &&
                _processedString.indexOf(StationTableRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX) + StationTableRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX.length() ==
                        _processedString.length()){

            return _processedString.substring(0, _processedString.length() - StationTableRecyclerViewAdapter.AOK_AVAILABILITY_POSTFIX.length() );

        }
        else {
            int debug3 = _processedString.lastIndexOf(StationTableRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE);

            //some availability troubles, let's just trim the end
            return _processedString.substring(0, _processedString.lastIndexOf(StationTableRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE));
        }*/
    }

    fun cleanStringForHashtagUse(toClean: String): String {
        var hashtagableBikeSystemName = toClean
        hashtagableBikeSystemName = hashtagableBikeSystemName.replace("\\s".toRegex(), "")
        hashtagableBikeSystemName = hashtagableBikeSystemName.replace("[^A-Za-z0-9 ]".toRegex(), "")
        hashtagableBikeSystemName = hashtagableBikeSystemName.toLowerCase()

        return hashtagableBikeSystemName
    }

    //citybik.es Ids, ordered by distance
    //get(0) is the id of the selected station with BAD or AOK availability
    fun extractOrderedStationIdsFromProcessedString(_processedString: String): ArrayList<String> {

        if (_processedString.isEmpty()) {
            val toReturn = ArrayList<String>()
            toReturn.add(_processedString)

            return toReturn
        }

        //int startSequenceIdx = _processedString.lastIndexOf(StationTableRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE);

        /*int subStringStarIdxDebug = _processedString.lastIndexOf(StationTableRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE)
                + StationTableRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE.length();*/

        /*String subStringDebug = _processedString.substring(_processedString.lastIndexOf(StationTableRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE)
                + StationTableRecyclerViewAdapter.AVAILABILITY_POSTFIX_START_SEQUENCE.length());*/


        //TODO: something is fishy here, couldn't figure out how to get the same result without intermediary debug labelled variable
        val debugSplit = _processedString.substring(_processedString.indexOf(TableFragmentViewModel.AVAILABILITY_POSTFIX_START_SEQUENCE) + TableFragmentViewModel.AOK_AVAILABILITY_POSTFIX.length)

        //String[] debugSplitResult = debugSplit.split(String.format("(?<=\\G.{%d})", StationTableRecyclerViewAdapter.CRITICAL_AVAILABILITY_POSTFIX.length() + 32));

        val toReturn = ArrayList<String>()
        toReturn.add(_processedString.substring(0, 32 + TableFragmentViewModel.AOK_AVAILABILITY_POSTFIX.length))

        toReturn.addAll(splitEqually(debugSplit, TableFragmentViewModel.CRITICAL_AVAILABILITY_POSTFIX.length + 32))

        return toReturn
    }

    private fun splitEqually(text: String, size: Int): List<String> {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        val ret = ArrayList<String>((text.length + size - 1) / size)

        var start = 0
        while (start < text.length) {
            ret.add(text.substring(start, Math.min(text.length, start + size)))
            start += size
        }
        return ret
    }

    //workaround from https://code.google.com/p/gmaps-api-issues/issues/detail?id=9011
    fun getBitmapDescriptor(ctx: Context, id: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(ctx.resources, id, null)
        val bm = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth,
                vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bm)
    }

    fun map(x: Float, in_min: Float, in_max: Float, out_min: Float, out_max: Float): Float {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min
    }

    fun dpToPx(toConvert: Float, ctx: Context): Int {
        /// Converts 66 dip into its equivalent px
        val r = ctx.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, toConvert, r.displayMetrics).toInt()
    }

    //http://stackoverflow.com/questions/3282390/add-floating-point-value-to-android-resources-values
    fun getAverageWalkingSpeedKmh(ctx: Context): Float {
        val outValue = TypedValue()

        ctx.resources.getValue(R.dimen.average_walking_speed_kmh, outValue, true)

        return outValue.float
    }

    fun getAverageBikingSpeedKmh(ctx: Context): Float {
        val outValue = TypedValue()

        ctx.resources.getValue(R.dimen.average_biking_speed_kmh, outValue, true)

        return outValue.float
    }

    fun getWalkingProximityString(from: LatLng?, to: LatLng?, _2digitsFormat: Boolean, nf: NumberFormat, ctx: Context): String? {
        return getProximityString(from, to, getAverageWalkingSpeedKmh(ctx), _2digitsFormat, nf, ctx)
    }

    fun getBikingProximityString(_from: LatLng, _to: LatLng, _2digitsFormat: Boolean, _nf: NumberFormat, _ctx: Context): String? {
        return getProximityString(_from, _to, getAverageBikingSpeedKmh(_ctx), _2digitsFormat, _nf, _ctx)
    }

    fun getWalkingDurationBetweenInMinutes(from: LatLng?, to: LatLng?, ctx: Context): Int? {

        from?.let {
            to?.let {
                return computeTimeBetweenInMinutes(from, to, getAverageWalkingSpeedKmh(ctx))
            }
        }
        return null
    }

    fun getBikingDurationBetweenInMinutes(from: LatLng?, to: LatLng?, ctx: Context): Int? {
        from?.let {
            to?.let {
                return computeTimeBetweenInMinutes(from, to, getAverageBikingSpeedKmh(ctx))
            }
        }
        return null
    }

    private fun getProximityString(_from: LatLng?, _to: LatLng?, _speedKmh: Float, _2digitsFormat: Boolean, _nf: NumberFormat, _ctx: Context): String? {

        return durationToProximityString(computeTimeBetweenInMinutes(_from, _to, _speedKmh), _2digitsFormat, _nf, _ctx)

    }

    fun durationToProximityString(durationMinute: Int?, twoDigitsFormat: Boolean, nf: NumberFormat, ctx: Context): String? {

        var toReturn: String? = null

        //TODO: shouldn't the person doing the calling configure the NumberFormat ?
        if (twoDigitsFormat)
            nf.minimumIntegerDigits = 2
        else
            nf.minimumIntegerDigits = 1

        durationMinute?.let {
            toReturn = when {
                durationMinute < 1 -> "<" + nf.format(1) + ctx.getString(R.string.min_abbreviated)
                durationMinute < 60 -> "~" + nf.format(durationMinute.toLong()) + ctx.getString(R.string.min_abbreviated)
                else -> "> ${nf.format(1)}${ctx.getString(R.string.hour_abbreviated)}"
            }
        }

        return toReturn
    }

    fun computeTimeBetweenInMinutes(from: LatLng?, to: LatLng?, speedKmH: Float): Int {

        var toReturn = 0

        from?.let {
            to?.let {
                val distance = SphericalUtil.computeDistanceBetween(from, to).toInt()

                val speedMetersPerH = speedKmH * 1000f
                val speedMetersPerS = speedMetersPerH / 3600f

                val timeInS = distance / speedMetersPerS

                val timeInMs = (timeInS * 1000).toLong()

                toReturn = (timeInMs / 1000 / 60).toInt()
            }
        }

        return toReturn
    }

    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    private fun round(d: Float, decimalPlace: Int): Float {
        var bd = BigDecimal(java.lang.Float.toString(d))
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
        return bd.toFloat()
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
    fun getPercentResource(_ctx: Context, _resId: Int, _rounded: Boolean): Float {
        val valueContainer = TypedValue()
        _ctx.resources.getValue(_resId, valueContainer, true)
        var toReturn = valueContainer.getFraction(1f, 1f)//http://stackoverflow.com/questions/11734470/how-does-one-use-resources-getfraction

        if (_rounded)
            toReturn = round(toReturn, 2)

        return toReturn
    }

    fun fromHtml(html: String): Spanned {
        val result: Spanned
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            result = Html.fromHtml(html)
        }
        return result
    }

    //Snackbar related utils
    object Snackbar {


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
        fun makeStyled(_view: View, @StringRes _textStringResId: Int, _duration: Int,
                       @ColorInt _backgroundColor: Int/*, @ColorInt int _textColor, @ColorInt int _actionTextColor*/): com.google.android.material.snackbar.Snackbar {

            val toReturn = com.google.android.material.snackbar.Snackbar.make(_view, _textStringResId, _duration)

            val snackbarView = toReturn.view

            //didn't use to work but maybe newer SnackBar versions will support it ?
            /*//change snackbar action text color
            toReturn.setActionTextColor(_actionTextColor);

            // change snackbar text color
            int snackbarTextId = android.support.design.R.id.snackbar_text;
            TextView textView = (TextView)snackbarView.findViewById(snackbarTextId);
            textView.setTextColor(_textColor);*/

            // change snackbar background
            snackbarView.setBackgroundColor(_backgroundColor)

            return toReturn
        }

    }
}