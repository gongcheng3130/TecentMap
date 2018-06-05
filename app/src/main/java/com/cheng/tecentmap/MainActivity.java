package com.cheng.tecentmap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdate;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.TencentMapOptions;
import com.tencent.tencentmap.mapsdk.maps.UiSettings;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.Circle;
import com.tencent.tencentmap.mapsdk.maps.model.CircleOptions;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TencentLocationListener, SensorEventListener {

    String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private TencentSearch tencentSearch;
    private MapView mapView;
    private TencentMap tencentMap;//地图设置
    private UiSettings mapUiSettings;//地图手势设置
    private TencentLocationManager locationManager;
    private TencentLocationRequest tencentLocationRequest;//定位请求
    private Marker marker, markerTarger;//当前确定位置
    private Circle circle;//当前确定位置
    //定义SensorManager传感器管理实例
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题栏
        setContentView(R.layout.activity_main);
        findViewById(R.id.location).setOnClickListener(this);
        initMapView((MapView) findViewById(R.id.mapView));
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//取得SensorManager实例
        initLocation(this);
        startLocation();
    }

    public void initLocation(Context context){
        //创建定位请求管理
        tencentLocationRequest= TencentLocationRequest.create();
        /**
         * 设置定位所需信息级别 setRequestLevel()
         REQUEST_LEVEL_GEO	0	包含经纬度
         REQUEST_LEVEL_NAME	1	包含经纬度, 位置名称, 位置地址
         REQUEST_LEVEL_ADMIN_AREA	3	包含经纬度，位置所处的中国大陆行政区划
         REQUEST_LEVEL_POI	4	包含经纬度，位置所处的中国大陆行政区划及周边POI列表
         REQUEST_LEVEL_FORMATTED_ADDRESS  5  包含经经纬度, 位置描述, 附近的POI
         */
        tencentLocationRequest.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_POI);
        //设置定位周期(位置监听器回调周期), 单位为 ms (毫秒)
        tencentLocationRequest.setInterval(2 * 1000);
        //设置是否允许使用缓存, 连续多次定位时建议允许缓存 使用getLastKnownLocation()获取上一次定位结果
        tencentLocationRequest.setAllowCache(false);
        //创建定位设置管理
        locationManager = TencentLocationManager.getInstance(context);
        //设置定位坐标系
        locationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_GCJ02);
    }

    private void initMapView(MapView mapView){
        this.mapView = mapView;
        tencentMap = mapView.getMap();//腾讯地图设置管理
        tencentMap.setMapType(TencentMap.MAP_TYPE_NORMAL);//设置地图显示模式
//        tencentMap.addCircle(getCircleOptions());//添加圆
//        tencentMap.addMarker(Marker);//添加标注
//        tencentMap.addPolygon(Polygon);//添加多边形
//        tencentMap.addPolyline(Polyline);//添加折线
//        tencentMap.animateCamera(CameraUpdate);//把地图变换到指定的状态,带动画
//        tencentMap.animateCamera(CameraUpdate, duration, TencentMap.CancelableCallback);//把地图变换到指定的状态,带动画,时长,完成回调
//        tencentMap.clear();//清除所有标注
//        tencentMap.getCameraPosition();//return CameraPosition 获取当前地图的状态（包括中心点、比例尺、旋转角、倾斜角）
//        tencentMap.getCityName();//return String 获得当前经纬度所在的城市名称
//        tencentMap.getMyLocation();//return MyLocation 获取我的位置
//        tencentMap.getProjection();//return Projection 获取坐标转换操作对象
//        tencentMap.getZoomToSpanLevel(Polyline);//return float 根据两点计算合适的缩放级别(需要等地图加载完成调用)
//        tencentMap.isMyLocationEnabled();//return boolean 是否显示我的位置
//        tencentMap.moveCamera(CameraUpdate);////把地图变换到指定的状态
//        tencentMap.removeBubble(id);//移除一个气泡
//        tencentMap.setCameraCenterProportion(x, y);//设置地图变换中心点
//        tencentMap.setInfoWindowAdapter(InfoWindowAdapter);//设置一个气泡
//        tencentMap.setLocationSource(LocationSource);//设置我的位置来源
//        tencentMap.setMyLocationEnabled(boolean);//设置是否显示我的位置
//        tencentMap.setOnTapMapViewInfoWindowHidden(boolean);//点击地图其他区域时，infowindow是否需要隐藏
//        tencentMap.setOnTop(boolean);//设置地图是不是在最在最上面
//        tencentMap.setPadding(int, int, int, int);//设置底图相对上、下、左、右的边距
//        tencentMap.setPointToCenter(int, int);//设置屏幕上的某个像素点为中心
//        tencentMap.snapshot(TencentMap.SnapshotReadyCallback);//获取地图当前截图
//        tencentMap.setMyLocationEnabled(TencentMap.SnapshotReadyCallback, Bitmap.Config config);//获取地图当前截图

//      Marker 标注 这个函数不由开发者调用，获取一个标注对象由 TencentMap.addMarker(MarkerOptions) 获取

//      Projection 标转换操作对象，如屏幕上某点转经纬度，经纬度转像素点等 由 TencentMap.getProjection() 获取

//      Polygon 多边形 这个函数不由开发者调用，获取一个多边形对象由 TencentMap.addPolygon(PolygonOptions) 获取

//      PolylineOptions 折线 路线之类的需要用到

//      MyLocationStyle 设置我的定位点样式  不起作用妈卖批
//        MyLocationStyle style = new MyLocationStyle();
//        style.anchor(0.5f, 0.5f);//设置定位图标的锚点。u 水平 y 垂直 建议传入0 到1 之间的数值
//        style.strokeWidth(50);//设置圆形区域（以定位位置为圆心，定位半径的圆形区域）的边框宽度
//        style.fillColor(R.color.text_blue);//设置圆形区域（以定位位置为圆心，定位半径的圆形区域）的填充颜色
//        style.strokeColor(R.color.white);//设置圆形区域（以定位位置为圆心，定位半径的圆形区域）的边框颜色。
//        style.icon(BitmapDescriptorFactory.fromResource(R.mipmap.use_car_icon_loc_tag));
//        style.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
//        tencentMap.setMyLocationStyle(style);
//        tencentMap.setMyLocationEnabled(true);
//        tencentMap.setLocationSource(new LocationSource() {
//            @Override
//            public void activate(OnLocationChangedListener onLocationChangedListener) {
//
//            }
//            @Override
//            public void deactivate() {
//
//            }
//        });
//        Log.i("111", "MyLocationEnabled = " + tencentMap.isMyLocationEnabled());

        //地图监听类
//        TencentMap.AsyncOperateCallback//异步操作回调
//        TencentMap.CancelableCallback//地图变换相关操作的状态回调接口
//        TencentMap.InfoWindowAdapter//自定义Marker气泡样式接口
//        TencentMap.OnCameraChangeListener//当地图发生变化时的调用接口
//        TencentMap.OnCompassClickedListener//地图的罗盘被点击时的回调接口
//        TencentMap.OnDismissCallback//在wear设备上触发退出操作回调
//        TencentMap.OnIndoorStateChangeListener//不明
//        TencentMap.OnInfoWindowClickListener//地图InfoWindow被点击回调
//        TencentMap.OnMapClickListener//地图被点击回调
//        TencentMap.OnMapLoadedCallback//地图加载完成回调
//        TencentMap.OnMapLongClickListener//地图被长按的回调
//        TencentMap.OnMapPoiClickListener//地图上Poi被点击的回调
//        TencentMap.OnMarkerClickListener//地图上Marker被点击的回调
//        TencentMap.OnMarkerDragListener//地图上Marker被拖动的回调
//        TencentMap.OnMyLocationChangeListener//我的位置发生改变回调
//        TencentMap.OnPolylineClickListener//地图上Polyline被点击的回调
//        TencentMap.SnapshotReadyCallback//地图截图操作回调
//        TencentMapAllGestureListener//地图手势监听 带默认实现，可自己按需重载
//        TencentMapGestureListener//地图手势监听（如：单击、双击、长按等）
//        TencentMapGestureListenerList//地图手势监听集合，可添加多个TencentMapGestureListener

        tencentMap.setOnMyLocationChangeListener(new TencentMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                Log.i("111", "onMyLocationChange() --- Latitude = " + location.getLatitude() + " --- Longitude = " + location.getLongitude());
            }
        });
        tencentMap.setOnCameraChangeListener(new TencentMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
//                Log.i("111", "onCameraChange() --- ");
            }
            @Override
            public void onCameraChangeFinished(CameraPosition position) {//这个参数是指地图所显示区域的中心点的位置
                Log.i("111", "onCameraChangeFinished() --- Latitude = " + position.target.latitude + " --- Longitude = " + position.target.longitude);
            }
        });

        //地图UI操作设置
        mapUiSettings = tencentMap.getUiSettings();//腾讯地图图标、缩放与手势管理
        mapUiSettings.setCompassEnabled(true);//指南针图标禁用
        mapUiSettings.setMyLocationButtonEnabled(false);//定位图标禁用
        mapUiSettings.setRotateGesturesEnabled(false);//旋转手势禁用
        mapUiSettings.setScaleViewEnabled(false);//比例尺图标禁用
        mapUiSettings.setFlingGestureEnabled(false);//快速滑动禁用
        mapUiSettings.setGestureScaleByMapCenter(false);//设置是否按地图中心点缩放，缩放手势关联
//        TencentMapOptions.LOGO_POSITION_BOTTOM_CENTER
//        TencentMapOptions.LOGO_POSITION_BOTTOM_LEFT
//        TencentMapOptions.LOGO_POSITION_BOTTOM_RIGHT
//        TencentMapOptions.LOGO_POSITION_TOP_CENTER
//        TencentMapOptions.LOGO_POSITION_TOP_LEFT
//        TencentMapOptions.LOGO_POSITION_TOP_RIGHT
        mapUiSettings.setLogoPosition(TencentMapOptions.LOGO_POSITION_BOTTOM_RIGHT);//腾讯地图标志位置
        //调用此方法可不需要上面那一句
        mapUiSettings.setLogoPositionWithMargin(TencentMapOptions.LOGO_POSITION_BOTTOM_RIGHT
                , 20, 20, 20, 20);//腾讯地图标志边距
        mapUiSettings.setLogoSize(-1);//腾讯地图标志大小  测试-1是标志最小的样子  无法隐藏
        mapUiSettings.setScrollGesturesEnabled(true);//地图滑动启用
        mapUiSettings.setTiltGesturesEnabled(false);//倾斜手势禁用
        mapUiSettings.setZoomControlsEnabled(false);//缩放图标禁用
        mapUiSettings.setZoomGesturesEnabled(true);//地图缩放启用
//        TencentMapOptions.ZOOM_POSITION_BOTTOM_LEFT
//        TencentMapOptions.ZOOM_POSITION_BOTTOM_RIGHT
//        TencentMapOptions.ZOOM_POSITION_TOP_LEFT
//        TencentMapOptions.ZOOM_POSITION_TOP_RIGHT
//        mapUiSettings.setZoomPosition(TencentMapOptions.ZOOM_POSITION_BOTTOM_RIGHT);//缩放图标位置

//        MarkerOptions 标注属性设置
//        MarkerOptions markerOptions = getMarkerOptions();

//        Marker 标注的类，由 TencentMap.addMarker(markerOptions) 获取
//        Marker marker = tencentMap.addMarker(getMarkerOptions());

//        LatLng 表示一个经纬度
//        LatLng latLng = getLatLng();

//        CircleOptions 圆的参数类
//        CircleOptions circleOptions = getCircleOptions();

//        Circle 标注的类，由 TencentMap.addMarker(circleOptions) 获取
//        Circle circle = tencentMap.addCircle(getCircleOptions());

//        CameraUpdateFactory 生成地图状态将要发生的变化，主要通过不同的参数生成不同的CameraUpdate来进行。
//        CameraUpdateFactory.newCameraPosition(CameraPosition);//由一个CameraPosition来生成新的状态变化对象
//        CameraUpdateFactory.newLatLng(LatLng);//生成一个把地图移动到指定的经纬度到屏幕中心的状态变化对象
//        CameraUpdateFactory.newLatLngZoom(LatLng, float);//把地图以latlng为中心，以zoom为缩放级别，移到屏幕中心
//        CameraUpdateFactory.zoomTo(float);//把地图缩放到目标级别

//        CameraUpdate 描述地图状态将要发生的变化,由 CameraUpdateFactory来生成

//        CameraPosition CameraPosition 的Builder类
//        bearing(float);//旋转角度
//        target(LatLng);//地图目标经纬度
//        tilt(float);//倾斜角度
//        zoom(float);//地图的目标缩放级别

//        BubbleGroup 对多气泡组的封装，用于多气泡的管理

//        BitmapDescriptor Bitmap的描述信息

//        BitmapDescriptorFactory
//        static BitmapDescriptor	defaultMarker()
//        获取默认样式的Marker的 BitmapDescriptor
//        static BitmapDescriptor	defaultMarker(float color)
//        创建指定颜色的Marker的BitmapDescriptor
//        static BitmapDescriptor	fromAsset(java.lang.String assetName)
//        根据资源asset文件创建一个 BitmapDescriptor
//        static BitmapDescriptor	fromBitmap(android.graphics.Bitmap bitmap)
//        根据Bitmap对象创建一个 BitmapDescriptor
//        static BitmapDescriptor	fromFile(java.lang.String filename)
//        根据应用程序私有文件夹里包含文件的文件名创建一个 BitmapDescriptor
//        static BitmapDescriptor	fromPath(java.lang.String filename)
//        根据文件绝对路径创建一个 BitmapDescriptor
//        static BitmapDescriptor	fromResource(int resId)
//        根据资源id创建一个 BitmapDescriptor
//        static BitmapDescriptor	fromView(android.view.View view)
//        根据传入的View，创建BitmapDescriptor 对象。

//        AnimationSet 一组动画

//        AnimationListener 动画结束回调

//        Animation 动画基类
//        下属各种动画 AlphaAnimation 渐变  TranslateAnimation 平移  ScaleAnimation 缩放 RotateAnimation 旋转

        tencentMap.setOnMapLoadedCallback(new TencentMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
//                "flat":31.297792,
//                        "flng":121.458387,

//                食其家
//                        "tlat":31.17266,
//                        "tlng":121.40654,

//                上海火车站
//                        "tlat":31.249584,
//                        "tlng":121.458234,

//                马戏城
//                        "tlat":31.27956,
//                        "tlng":121.452043,
                double startlat = 31.297792;
                double startlng = 121.458387;
                double endlat = 31.17266;
                double endlng = 121.40654;
                setMarker(startlat, startlng, R.mipmap.use_car_icon_loc_tag);
                setMarkerTarget(endlat, endlng, R.mipmap.use_car_icon_re_loc);
                LatLng start = new LatLng(startlat, startlng);
                LatLng end = new LatLng(endlat, endlng);
                LatLng centerPoint = GetCenterPoint.getCenterPoint(start, end);
                float piex = tencentMap.getZoomToSpanLevel(start, end)-2;
                double piex_y = 0.0007;
                for (int i = 0; i < 18 - piex; i++) {
                    piex_y = piex_y * 1.90;
                }
                startAnimotion(centerPoint.latitude - piex_y, centerPoint.longitude, tencentMap.getZoomToSpanLevel(start, end)-2);
            }
        });

    }

//    private Circle getCircle(){
//        Circle circle = new Circle(getCircleOptions(), null, "");
//        circle.contains(LatLng)//判断点是否在圆内
//        circle.getCenter()//获取当前圆心的经纬度
//        circle.getFillColor()//获取圆的填充颜色
//        circle.getId()//获取这个圆的id编号
//        circle.getRadius()//获取圆的半径，单位为米
//        circle.getStrokeColor()//获得圆描边的颜色
//        circle.getStrokeWidth()//获取圆描边的宽度
//        circle.getZIndex()//获得该圆的层级
//        circle.isClickable()//是否支持点击
//        circle.isVisible()//获取是否可见
//        circle.remove()//将这个圆从地图上移除
//        circle.setCenter(LatLng)//重新设置圆心
//        circle.setClickable(boolean)//设置是否可以点击
//        circle.setFillColor(int)//设置圆的填充颜色
//        circle.setOptions(CircleOptions)//判断点是否在圆内
//        circle.setRadius(double)//设置圆的半径，单位为米
//        circle.setStrokeColor(int)//设置圆描边的颜色（ARGB）
//        circle.setStrokeWidth(float)//设置圆描边的宽度
//        circle.setVisible(boolean)//设置是否可见
//        circle.setZIndex(boolean)//设置该圆的层级
//        return circle;
//    }

    private CircleOptions getCircleOptions(double lat, double lon){
        CircleOptions circleOptions  = new CircleOptions();
        circleOptions.center(new LatLng(lat, lon));//设置圆心坐标
        circleOptions.clickable(false);//圆形是否支持点击
        circleOptions.fillColor(Color.parseColor("#90C8DEEE"));//设置圆的填充颜色
//        circleOptions.getCenter();//获取圆心经纬度
//        circleOptions.getFillColor();//获取圆的填充颜色
//        circleOptions.getRadius();//获取圆的半径
//        circleOptions.getStrokeColor();//获取圆描边的颜色
//        circleOptions.getStrokeWidth();//获取圆的描边宽度
//        circleOptions.getZIndex();//获取圆的层级关系
//        circleOptions.isClickable();//是否支持点击
//        circleOptions.isVisible();//获取圆的可见性
        circleOptions.radius(30);//设置圆的半径，单位为米
        circleOptions.strokeColor(getResources().getColor(R.color.white));//设置圆的描边颜色
        circleOptions.strokeWidth(2);//设置圆的描边宽度
        circleOptions.visible(true);//设置圆的可见性
//        circleOptions.zIndex(float);//设置圆的层级关系
        return circleOptions;
    }

    private LatLng getLatLng(double lat, double lon){
        LatLng latLng = new LatLng(lat, lon);
        return latLng;
    }

    private MarkerOptions getMarkerOptions(double lat, double lon, int resouce){
        MarkerOptions markerOptions  = new MarkerOptions(new LatLng(lat, lon));
//        markerOptions.alpha(float);//设置标注的透明度
//                .anchor(anchorU, anchorV)//设置标注的锚点 anchorU - 取值为[0.0 ~ 1.0] 表示锚点从最左边到最右边的百分比 anchorV - 取值为[0.0 ~ 1.0] 表示锚点从最上边到最下边的百分比
                markerOptions.clockwise(false);//旋转角度是否沿顺时针方向
                markerOptions.draggable(false);//设置标注是否可以被拖动
                markerOptions.flat(false);//设置是不是3D标注，3D标注会随着地图倾斜面倾斜
//                .getAlpha()//获取标注的透明度
//                .getAnchorU()//获取标注的上下的锚点
//                .getAnchorV()//获取标注的左右的锚点
//                .getIcon()//获取标注的样式
//                .getPosition()//获取标注的位置
//                .getRotation()//获取标注的旋转角度
//                .getSnippet()//获取标注的InfoWindow(气泡)的内容
//                .getTag() //获得标注的InfoWindow(气泡)的标题
//                .getTitle()//设置标注的锚点
//                .getZIndex()//获取标注的层级关系
        markerOptions.icon(BitmapDescriptorFactory.fromResource(resouce));
//                .icon(BitmapDescriptor)//设置标注的样式
        markerOptions.infoWindowEnable(false);//设置标注是否可以弹出InfoWindow(气泡)
//                .isAvoidAnnocation()//获取是否避让底图文字
//                .isClockwise()//获取旋转角度是否沿顺时针方向
//                .isDraggable()//获取标注是否可以被拖动
//                .isFlat()//获取标注是否是3D
//                .isInfoWindowEnable()//获取标注的InfoWindow是否可以弹出气泡
//                .isVisible()//获得标注是否可见
//                .position(LatLng)//设置标注的位置
//                .rotation(float)//设置标注的旋转角度
//                .snippet(String)//设置标注的InfoWindow(气泡)的内容，如果设置了 TencentMap.setInfoWindowAdapter(com.tencent.tencentmap.mapsdk.maps.TencentMap.InfoWindowAdapter) 则失效
        markerOptions.title("定位点");//设置标注的InfoWindow(气泡)的标题，如果设置了 TencentMap.setInfoWindowAdapter(com.tencent.tencentmap.mapsdk.maps.TencentMap.InfoWindowAdapter) 则失效
        markerOptions.visible(true);//设置标注是否可见
//                .zIndex(float)//设置标注的层级关系
        return markerOptions;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.location:
                startLocation();
                break;
        }
    }

    public int startLocation(){
        int error;
        if(checkPermission(this, permissions[0])){
            error = locationManager.requestLocationUpdates(tencentLocationRequest, this);
        }else{
            error = -1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 100);
            }else{
                error = locationManager.requestLocationUpdates(tencentLocationRequest, this);
            }
        }
        return error;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100 && resultCode==RESULT_OK){
            startLocation();
        }
    }

    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        Log.i("111", "error = " + error + " --- reason = " + reason);
        locationManager.removeUpdates(this);
        if(error== TencentLocation.ERROR_OK){//定位成功
            Log.i("111", "location.getCity() = " + location.getCity());
            Log.i("111", "location.getCityCode() = " + location.getCityCode());
            Log.i("111", "location.getLatitude() = " + location.getLatitude());
            Log.i("111", "location.getLongitude() = " + location.getLongitude());
            Log.i("111", "location.getAccuracy() = " + location.getAccuracy());
            Log.i("111", "location.getBearing() = " + location.getBearing());
            Log.i("111", "location.getDirection() = " + location.getDirection());
            startAnimotion(location.getLatitude() +  + 0.0005, location.getLongitude());
            setMarker(location.getLatitude() +  + 0.0005, location.getLongitude(), R.mipmap.use_car_icon_loc_tag);
            setCircleBg(location.getLatitude() +  + 0.0005, location.getLongitude());
        }else{
            Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusUpdate(String name, int status, String desc) {
        Log.i("111", "name = " + name + " --- status = " + status + " --- desc = " + desc);
    }

    long SensorChangedTimeCount = System.currentTimeMillis();
    double rorate = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 接受方向感应器的类型 即使频率最慢也是在200ms
        //所以需要控制调用时间，否则刷新太频繁
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            // 这里我们可以得到数据，然后根据需要来处理
            if(System.currentTimeMillis() - SensorChangedTimeCount>2000){
                SensorChangedTimeCount = System.currentTimeMillis();
                rorate = event.values[0];
                Log.i("111", "rorate = " + rorate);
                marker.setRotation((float) rorate);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    protected void setMarker(double lat, double lon, int resouce){
        if(marker!=null){
            marker.remove();
        }
        marker = tencentMap.addMarker(getMarkerOptions(lat, lon, resouce));
    }

    protected void setMarkerTarget(double lat, double lon, int resouce){
        if(markerTarger!=null){
            markerTarger.remove();
        }
        markerTarger = tencentMap.addMarker(getMarkerOptions(lat, lon, resouce));
    }

    protected void setCircleBg(double lat, double lon){
        if(circle!=null){
            circle.remove();
        }
        circle = tencentMap.addCircle(getCircleOptions(lat, lon));
    }

    //切换到当前坐标
    protected void startAnimotion(double lat, double lon){
        startAnimotion(lat, lon, 17);
    }

    //切换到当前坐标
    protected void startAnimotion(double lat, double lon, float zoom){
        //移动目标点设置
        CameraUpdate cameraSigma = CameraUpdateFactory.newCameraPosition(new CameraPosition(
                new LatLng(lat, lon), //新的中心点坐标
                zoom,  //新的缩放级别
                10f, //俯仰角 0~45° (垂直地图时为0)
                0)); //偏航角 0~360° (正北方为0)
        tencentMap.animateCamera(cameraSigma);//移动地图
    }

    /**
     * 权限检查
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (geTargetSdkVersion(context) >= Build.VERSION_CODES.M) {
                result = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
        }
        Log.i("111", "checkPermission = " + result);
        return result;
    }

    /**
     *  获取版本号
     *  @param context
     *  @return 当前应用的版本号
     */
    public static int geTargetSdkVersion(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        //接受SensorManager的一个列表(Listener)
        //这里我们指定类型为TYPE_ORIENTATION(方向感应器)
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (sensors.size() > 0) {
            Sensor sensor = sensors.get(0);
            //注册SensorManager
            //this->接收sensor的实例
            //接收传感器类型的列表
            //接受的频率
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        //注销所有传感器的监听
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mapView.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
