package com.cheng.tecentmap;

import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public class GetCenterPoint {

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

