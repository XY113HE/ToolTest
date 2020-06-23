package com.freeme.tooltest;

import com.yiguo.adressselectorlib.CityInterface;

public class CitysBean implements CityInterface {
    private String cityName;

    public CitysBean(String cityName) {
        this.cityName = cityName;
    }

    @Override
    public String getCityName() {
        return cityName;
    }
}
