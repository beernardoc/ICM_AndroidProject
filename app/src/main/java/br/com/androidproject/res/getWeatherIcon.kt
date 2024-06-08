package br.com.androidproject.res

import androidx.annotation.DrawableRes
import br.com.androidproject.R


@DrawableRes
fun getWeatherIcon(description: String): Int {
    return when {
        description.contains("clear", ignoreCase = true) -> R.drawable.ic_sunny
        description.contains("clouds", ignoreCase = true) -> R.drawable.ic_cloudy
        description.contains("rain", ignoreCase = true) -> R.drawable.ic_rainy
        description.contains("thunder", ignoreCase = true) -> R.drawable.ic_thunder
        description.contains("snow", ignoreCase = true) -> R.drawable.ic_snowy
        description.contains("mist", ignoreCase = true) -> R.drawable.ic_mist
        else -> R.drawable.ic_sunny
    }
}
