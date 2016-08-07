package com.jodelXposed.models;

public class Location {
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public double getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLat(double lat) {
        this.lat = Float.parseFloat(String.valueOf(lat));
    }

    public double getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public void setLng(double lng) {
        this.lng = Float.parseFloat(String.valueOf(lng));
    }

    boolean active = false;
    public String city = "Heard and McDonald Islands";
    public String country = "Heard and McDonald Islands";
    public String countryCode = "AU";
    public float lat = -53.076499f;
    public float lng = 73.37357f;
}
