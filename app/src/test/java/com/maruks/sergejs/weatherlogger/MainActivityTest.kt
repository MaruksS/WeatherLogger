package com.maruks.sergejs.weatherlogger


import org.json.JSONObject
import org.junit.Test


class MainActivityTest {


    val jsonObject = JSONObject("""{"cod":401, "message": "Invalid API key.
        |Please see http://openweathermap.org/faq#error401 for more info."}""".trimMargin())

    val apiLink = "http://api.openweathermap.org/data/2.5/weather?" +
            "id=456172&APPID=75ac5ba974a5570488359849c2380b76"
    @Test
    fun run() {
        val mainActivity = MainActivity()
        mainActivity.run(apiLink)
    }


    @Test
    fun addToList() {
        val mainActivity = MainActivity()
        mainActivity.addToList(jsonObject)
    }

    @Test
    fun delete() {
        val mainActivity = MainActivity()
        mainActivity.delete()
    }

    @Test
    fun write() {
        val mainActivity = MainActivity()
        mainActivity.write()
    }

    @Test
    fun openDetailsActivity() {
        val mainActivity = MainActivity()
        mainActivity.openDetailsActivity(1)
    }



}