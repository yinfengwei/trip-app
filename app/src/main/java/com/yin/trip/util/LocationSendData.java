package com.yin.trip.util;

import java.io.Serializable;

/**
 * Created by yinfeng on 2017/3/14 0014.
 * 发送至服务端的位置信息
 */
public class LocationSendData implements Serializable{
    private double longtitude;      //经度
    private double latitude;        //纬度
    private String addr;            //位置信息


    public LocationSendData(double longtitude, double latitude){
        this.setLatitude(latitude);
        this.setLongtitude(longtitude);
    }
    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
