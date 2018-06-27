package com.maruks.sergejs.weatherlogger

import Entities.WeatherInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        read()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /*
     * Read Weather information from file
     */
    private fun read(){
        val weatherIntentId:Int = intent.getIntExtra("dataId", 0)
        if(fileList().contains("weather.txt")) {
            try {
                val file = InputStreamReader(openFileInput("weather.txt"))
                val br = BufferedReader(file)
                var line = br.readLine()
                while (line != null) {
                    val strs = line.split(",")
                    val dataId: Int = strs[0].toInt()
                    if  (dataId==weatherIntentId) {
                        val city: String = strs[1]
                        val weatherDescriptor: String = strs[2]
                        val date:String = strs[3]
                        val temp:Double = strs[4].toDouble()
                        val humidity: Int = strs[5].toInt()
                        val pressure: Int = strs[6].toInt()

                        val weather = WeatherInfo(dataId, city, weatherDescriptor,
                                date, temp, humidity, pressure)

                        updateView(weather)
                        break
                    }

                    line = br.readLine()
                }
                br.close()
                file.close()
            }
            catch (e: IOException) {
                println(e.toString())
            }
        }
    }

    private fun updateView(weatherInfo: WeatherInfo){
        val twDate = findViewById<TextView>(R.id.twDate)
        val twCity = findViewById<TextView>(R.id.twCity)
        val twTemp = findViewById<TextView>(R.id.twTemp)
        val twWeather = findViewById<TextView>(R.id.twWeather)
        val twHumidity = findViewById<TextView>(R.id.twHumidity)
        val twPressure = findViewById<TextView>(R.id.twPressure)

        twDate.text = weatherInfo.dateOfEvent
        twCity.text = weatherInfo.city
        twTemp.text = weatherInfo.temperature.toString()
        twWeather.text = weatherInfo.weatherDescription
        twHumidity.text = weatherInfo.humidity.toString()
        twPressure.text = weatherInfo.pressure.toString()
    }

}
