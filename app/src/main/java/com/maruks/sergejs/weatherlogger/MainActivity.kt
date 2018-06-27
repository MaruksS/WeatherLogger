package com.maruks.sergejs.weatherlogger



import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.aednlaxer.lib.kelvin
import okhttp3.*
import org.json.JSONObject

import Adapters.MainAdapter
import Entities.WeatherInfo
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import java.io.*

import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    // Api request client
    private val client = OkHttpClient()

    // Initialize empty weatherInfo list
    private var weatherList: MutableList<WeatherInfo> = mutableListOf<WeatherInfo>()

    // Date format for current Date
    private val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")

    // RecyclerView properties
    private lateinit var mainAdapter: MainAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mLinearLayoutManager: LinearLayoutManager

    // Handler for updating Recycler View
    private lateinit var mHandler: Handler

    // Updating RecyclerView Runnable
    private val addNew = Runnable {
        mainAdapter.notifyItemInserted(0)
        mLinearLayoutManager.scrollToPositionWithOffset(0,0)
    }

    // Updating RecyclerView Runnable
    private val update = Runnable {
        mainAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // RecyclerView properties init
        mRecyclerView = findViewById(R.id.rw_Main)
        mLinearLayoutManager = LinearLayoutManager(this)
        mainAdapter = MainAdapter(this,weatherList)

        // Handler init
        mHandler = Handler(Looper.getMainLooper())

        // Check for existing data
        read("weather.txt")

        mainAdapter.setOnRecyclerViewItemClickListener {
            id -> openDetailsActivity(Integer.parseInt(id as String?)) }
        // Apply LayoutManager and Adapter to RecyclerView
        mRecyclerView.layoutManager = mLinearLayoutManager
        mRecyclerView.adapter = mainAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.getItemId()
        if (id==R.id.read){
            run("http://api.openweathermap.org/data/2.5/weather?id=456172&APPID=75ac5ba974a5570488359849c2380b76")
        }
        else if (id==R.id.delete){
            showConfirmDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    /*
     * Make API request to get weather information
     */
    fun run(url: String) {
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.toString())
            }
            override fun onResponse(call: Call, response: Response) {
                val content = response.body()?.string()

                val weatherFromAPI = JSONObject(content)
                if(weatherFromAPI.get("cod")!=200){
                    println(weatherFromAPI.get("message"))
                }else{
                    addToList(weatherFromAPI)
                }
            }

        })
    }


    /*
     * Add received data to weather list
     */
    fun addToList(jsonObject: JSONObject){
        lateinit var weatherDescription:String

        val mainObj = jsonObject.getJSONObject("main")
        val weatherArray = jsonObject.getJSONArray("weather")
        if (mainObj!=null){
            for (i in 0 until weatherArray.length()) {
                val weatherObj = weatherArray.getJSONObject(i)
                weatherDescription = weatherObj.get("description") as String
            }
            val currentDate = sdf.format(Date())
            val city:String = jsonObject.get("name") as String
            val temp = mainObj.get("temp") as Double
            val tempCelsius = temp.kelvin().toCelsius()
            val humidity = mainObj.get("humidity") as Int
            val pressure = mainObj.get("pressure") as Int
            val dataCount = weatherList.count()

            var weather = WeatherInfo(dataCount, city, weatherDescription,
                    currentDate, tempCelsius, humidity, pressure)
            weatherList.add(0,weather)
            write()
            mHandler.post(addNew)
        }
    }
    /*
     * Read Weather information from file
     */
    fun read(fileName: String){
        if(fileList().contains(fileName)) {
            try {
                val file = InputStreamReader(openFileInput("weather.txt"))
                val br = BufferedReader(file)
                var line = br.readLine()
                while (line != null) {
                    val strs = line.split(",")
                    val dataId: Int = strs[0].toInt()
                    val city: String = strs[1]
                    val weatherDescriptor: String = strs[2]
                    val date:String = strs[3]
                    val temp:Double = strs[4].toDouble()
                    val humidity: Int = strs[5].toInt()
                    val pressure: Int = strs[6].toInt()


                    val weather = WeatherInfo(dataId, city, weatherDescriptor,
                            date, temp, humidity, pressure)
                    weatherList.add(weather)

                    line = br.readLine()
                }
                br.close()
                file.close()
            }
            catch (e:IOException) {
                println(e.toString())
            }
        }
    }

    /*
     * Delete All Weather information
     */
    fun delete(){
        if (fileList()!=null){
            if(fileList().contains("weather.txt")) {
                try {
                    deleteFile("weather.txt")
                    weatherList.clear()
                    mHandler.post(update)
                }
                catch (e:IOException) {
                    println(e.toString())
                }
            }
        }
    }

    /*
     * Data delete popup
     */
    fun showConfirmDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.delete_popup, null)
        dialogBuilder.setView(dialogView)

        dialogBuilder.setTitle(resources.getString(R.string.menu_delete_all))
        dialogBuilder.setMessage(resources.getString(R.string.dialog_confirmation_text))
        dialogBuilder.setPositiveButton(resources.getString(R.string.dialog_confirm_button)) {
            dialog, whichButton -> delete()
        }
        dialogBuilder.setNegativeButton(resources.getString(R.string.dialog_cancel_button)) {
            dialog, whichButton ->
        }
        val confirmDialog = dialogBuilder.create()
        confirmDialog.show()
    }

    /*
     * Write Weather information to file
     */
    fun write(){
        try {
            if (applicationContext!=null){
                File(applicationContext.filesDir, "weather.txt").printWriter().use { out ->
                    weatherList.forEach{
                        out.println("${it.dataId},${it.city},${it.weatherDescription}," +
                                "${it.dateOfEvent},${it.temperature},${it.humidity},${it.pressure},")
                    }

                }
            }
        }
        catch (e : IOException) {
            println(e.toString())
        }
    }

    /*
     * Open Detailed Weather Info
     */
    fun openDetailsActivity(id: Int){
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("dataID", id)
        startActivity(intent)
    }

}
