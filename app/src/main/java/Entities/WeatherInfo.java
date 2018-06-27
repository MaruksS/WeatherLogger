package Entities;


public class WeatherInfo {

    public int dataId;
    public String city;
    public String weatherDescription;
    public String dateOfEvent;
    public Double temperature;
    public int humidity;
    public int pressure;


    public WeatherInfo(int dataId, String city, String weatherDescription, String dateOfEvent,
                       Double temperature, int humidity, int pressure){

        this.dataId = dataId;
        this.city = city;
        this.weatherDescription = weatherDescription;
        this.dateOfEvent = dateOfEvent;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
    }

}
