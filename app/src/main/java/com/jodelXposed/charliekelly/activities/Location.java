package com.jodelXposed.charliekelly.activities;

import com.orm.SugarRecord;

/**
 * Created by Admin on 13.04.2016.
 */
public class Location extends SugarRecord {
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Location(double lat, double lng, String city, String country, String countryCode) {
        this.lat = lat;
        this.lng = lng;
        this.city = city;
        this.country = country;
        this.countryCode = countryCode;
    }

    public Location() {
    }

    private double lat;
    private double lng;
    private String city;
    private String country;
    private String countryCode;
}
