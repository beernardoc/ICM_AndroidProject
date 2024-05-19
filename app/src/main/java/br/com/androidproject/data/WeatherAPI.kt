import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val sys: Sys,
    val name: String
)

data class Weather(
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double,
    val deg: Int
)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class Main(
    val temp: Double,
    val pressure: Int,
    val humidity: Int
)

object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    val instance: WeatherApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(WeatherApi::class.java)
    }
}

suspend fun fetchWeather(lat: Double, lon: Double): String? {
    val apiKey = "60a8e144d61c18ae08e66dae887be8df"
    return try {
        val response = RetrofitClient.instance.getWeather(lat, lon, apiKey)
        val weatherDescription = response.weather.firstOrNull()?.description ?: "No description"
        val temperature = response.main.temp
        val pressure = response.main.pressure
        val humidity = response.main.humidity
        val windSpeed = response.wind.speed
        val country = response.sys.country
        val cityName = response.name

        """
        Weather in $cityName, $country:
        Temperature: $temperature°C
        Description: $weatherDescription
        Pressure: $pressure hPa
        Humidity: $humidity%
        Wind Speed: $windSpeed m/s
        """.trimIndent()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
