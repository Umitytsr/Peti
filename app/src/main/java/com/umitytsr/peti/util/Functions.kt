package com.umitytsr.peti.util

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.Timestamp
import com.umitytsr.peti.R
import com.umitytsr.peti.data.model.Message
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun applyTheme(isDarkModeEnabled: Boolean) {
    val mode = if (isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
    AppCompatDelegate.setDefaultNightMode(mode)
}

fun setAppLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val resources = context.resources
    val configuration = resources.configuration
    configuration.setLocale(locale)
    configuration.setLayoutDirection(locale)

    @Suppress("DEPRECATION")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        configuration.setLocale(locale)
    } else {
        configuration.locale = locale
    }

    @Suppress("DEPRECATION")
    resources.updateConfiguration(configuration, resources.displayMetrics)
}

fun formatTimestampToDayMonthYear(context: Context, timestamp: Timestamp): String {
    val calendarNow = Calendar.getInstance()
    val calendarTimestamp = Calendar.getInstance().apply {
        timeInMillis = timestamp.seconds * 1000
    }

    // Tarih bugünse "Today", dünkü tarihse "Yesterday" döndür, değilse formatla
    return when {
        isSameDay(calendarNow, calendarTimestamp) -> context.getString(R.string.today)
        isYesterday(calendarTimestamp) -> context.getString(R.string.yesterday)
        else -> {
            val date = timestamp.toDate() // Timestamp'ı Date'e çevir
            val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) // Formatlayıcı
            formatter.format(date)
        }
    }
}

private fun isSameDay(calendar1: Calendar, calendar2: Calendar): Boolean {
    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
            calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
}

private fun isYesterday(calendarTimestamp: Calendar): Boolean {
    val yesterday = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
    }
    return calendarTimestamp.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) &&
            calendarTimestamp.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)
}

fun getDefaultLanguage(): String {
    val localeLanguage = Locale.getDefault().language
    return when(localeLanguage){
        "en", "fr", "es", "tr" -> localeLanguage
        else -> "tr"
    }
}

fun formatTimestampToTime(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("HH.mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}

fun isDateChanged(message1: Message, message2: Message): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = message1.date!!.seconds * 1000 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = message2.date!!.seconds * 1000 }

    return cal1.get(Calendar.YEAR) != cal2.get(Calendar.YEAR) ||
            cal1.get(Calendar.DAY_OF_YEAR) != cal2.get(Calendar.DAY_OF_YEAR)
}