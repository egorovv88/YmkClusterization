package com.example.yandexclussterization;

public class Store {

    public Store(Double lat, Double lng, int param) {
        this.lat = lat;
        this.lng = lng;
        this.param = param;
    }

    private Double lat;
    private Double lng;
    private int param;

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public int getParam() {
        return param;
    }
}
