package com.example.dating.utils

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.example.dating.R
import com.example.dating.activities.RegisterActivity
import com.example.dating.models.BaseModel
import com.example.dating.models.UserModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

fun hideKeyboard(view: View?) {
    view?.let { v ->
        v.clearFocus()
        val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                as? InputMethodManager
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}

@Suppress("DEPRECATION")
fun isInternetAvailable(context: Context): Boolean {
    var result = false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }
    return result
}

fun showAlertDialog(
    context: Context,
    title: String?,
    message: String?,
    positiveButtonText: String?,
    positiveClick: DialogInterface.OnClickListener?,
    negativeButtonText: String?,
    negativeClick: DialogInterface.OnClickListener?
) {
    val alertBuilder = AlertDialog.Builder(context)
    alertBuilder.setTitle(title)
    alertBuilder.setMessage(message)

    var positiveText: String = context.getString(R.string.ok)
    positiveButtonText?.let {
        positiveText = it
    }

    var positiveListener: DialogInterface.OnClickListener =
        DialogInterface.OnClickListener { dialogInterface, _ ->
            dialogInterface.cancel()
        }
    positiveClick?.let {
        positiveListener = it
    }

    alertBuilder.setPositiveButton(positiveText, positiveListener)

    var negativeListener: DialogInterface.OnClickListener =
        DialogInterface.OnClickListener { dialogInterface, _ ->
            dialogInterface.cancel()
        }
    negativeClick?.let {
        negativeListener = it
    }
    alertBuilder.setNegativeButton(negativeButtonText, negativeListener)

    val alertDialog = alertBuilder.create()
    alertDialog.show()
}

fun showInfoAlertDialog(context: Context, message: String) {
    showAlertDialog(context, null, message, null, null, null, null)
}

fun validateInternet(context: Context): Boolean {
    return if (isInternetAvailable(context)) {
        true
    } else {
        showInfoAlertDialog(context, context.getString(R.string.no_internet))
        false
    }
}

fun validateResponse(context: Context, baseResponse: BaseModel): Boolean {
    return if (baseResponse.status == null) {
        true
    } else {
        val message = if(baseResponse.message == null) Constants.SOMETHING_WENT_WRONG else baseResponse.message!!
        showInfoAlertDialog(context, message)
        false
    }
}

fun printLog(string: String) {
    Log.d(Constants.LOG_TAG, string)
}

fun dpToPx(context: Context, valueInDp: Float): Float {
    val displayMetrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, displayMetrics)
}

fun saveLoggedInUserToShared(context: Context, userModel: UserModel) {
    val gson = Gson()
    val strJson = gson.toJson(userModel)
    SharedPreferenceHelper.saveStringToShared(context, Constants.LOGGED_IN_USER, strJson)
}

fun getLoggedInUserFromShared(context: Context): UserModel {
    val gson = Gson()
    val strJson = SharedPreferenceHelper.getStringFromShared(context, Constants.LOGGED_IN_USER)
    return gson.fromJson(strJson, UserModel::class.java)
}

fun formatDobForAPI(calendar: Calendar): String {
    val format = SimpleDateFormat(Constants.DOB_DATE_FORMAT, Locale.US)
    return format.format(calendar.time)
}

fun getRussianLocale(): Locale {
    return Locale("ru", "RU")
}

fun getDisplayableDate(calendar: Calendar): String {
    val format = SimpleDateFormat(Constants.DOB_ONLY_DATE_FORMAT, getRussianLocale())
    return format.format(calendar.time)
}

fun getDisplayableMonth(calendar: Calendar): String {
    val format = SimpleDateFormat(Constants.DOB_ONLY_MONTH_FORMAT, getRussianLocale())
    return format.format(calendar.time)
}

fun getDisplayableYear(calendar: Calendar): String {
    val format = SimpleDateFormat(Constants.DOB_ONLY_YEAR_FORMAT, getRussianLocale())
    return format.format(calendar.time)
}

fun logoutAndMoveToHome(context: Context) {
    SharedPreferenceHelper.clearShared(context)
    val loginScreen = Intent(context, RegisterActivity::class.java)
    loginScreen.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(loginScreen)
}