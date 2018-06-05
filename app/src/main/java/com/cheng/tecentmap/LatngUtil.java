package com.cheng.tecentmap;

import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public class LatngUtil {

    /**
     * 计算两点连线的偏转度
     * @param lat1 坐标点1
     * @param lat2 坐标点2
     * @return
     */
    public static double getAngle1(LatLng lat1, LatLng lat2) {
        return getAngle1(lat1.latitude, lat1.longitude, lat2.latitude, lat2.longitude);
    }

    /**
     * 计算两点连线的偏转度
     * @param lat_a 纬度1
     * @param lng_a 经度1
     * @param lat_b 纬度2
     * @param lng_b 经度2
     * @return
     */
    public static double getAngle1(double lat_a, double lng_a, double lat_b, double lng_b) {
        double y = Math.sin(lng_b-lng_a) * Math.cos(lat_b);
        double x = Math.cos(lat_a)*Math.sin(lat_b) - Math.sin(lat_a)*Math.cos(lat_b)*Math.cos(lng_b-lng_a);
        double brng = Math.atan2(y, x);
        brng = Math.toDegrees(brng);
        if(brng < 0) brng = brng +360;
        return brng;
    }

    /**
     * 计算两点之间的距离
     * @param long1 经度1
     * @param lat1 维度1
     * @param long2 经度2
     * @param lat2 纬度2
     * @return
     */
//    public static double getDistance(double long1, double lat1, double long2, double lat2) {
//        double a, b, R;
//        R = 6378137; // 地球半径
//        lat1 = lat1 * Math.PI / 180.0;
//        lat2 = lat2 * Math.PI / 180.0;
//        a = lat1 - lat2;
//        b = (long1 - long2) * Math.PI / 180.0;
//        double d;
//        double sa2, sb2;
//        sa2 = Math.sin(a / 2.0);
//        sb2 = Math.sin(b / 2.0);
//        d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
//        return d;
//    }

    private static double EARTH_RADIUS = 6378.137;

    /**
     * 通过经纬度获取距离(单位：米)
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static int getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        s = s*1000;
        return (int)s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     *  根据输两点坐标计算中心点
     * @param lat1
     * @param lat2
     * @return
     */
    public static LatLng getCenterPoint(LatLng lat1, LatLng lat2) {
        List<LatLng> lists = new ArrayList<>();
        lists.add(lat1);
        lists.add(lat2);
        return getCenterPoint(lists);
    }

    /**
     *  根据输入的地点坐标计算中心点
     * @param lists
     * @return
     */
    public static LatLng getCenterPoint(List<LatLng> lists) {
        int total = lists.size();
        double X = 0, Y = 0, Z = 0;
        for (LatLng g : lists) {
            double lat, lon, x, y, z;
            lat = g.latitude * Math.PI / 180;
            lon = g.longitude * Math.PI / 180;
            x = Math.cos(lat) * Math.cos(lon);
            y = Math.cos(lat) * Math.sin(lon);
            z = Math.sin(lat);
            X += x;
            Y += y;
            Z += z;
        }
        X = X / total;
        Y = Y / total;
        Z = Z / total;
        double Lon = Math.atan2(Y, X);
        double Hyp = Math.sqrt(X * X + Y * Y);
        double Lat = Math.atan2(Z, Hyp);
        return new LatLng(Lat * 180 / Math.PI, Lon * 180 / Math.PI);
    }

    // 以下为简化方法（400km以内）
    /**
     * 根据输入的地点坐标计算中心点（适用于400km以下的场合）
     * @param lists
     * @return
     */
    public static LatLng getCenterPoint400(List<LatLng> lists) {
        int total = lists.size();
        double lat = 0, lon = 0;
        for (LatLng g : lists) {
            lat += g.latitude * Math.PI / 180;
            lon += g.longitude * Math.PI / 180;
        }
        lat /= total;
        lon /= total;
        return new LatLng(lat * 180 / Math.PI, lon * 180 / Math.PI);
    }

}

