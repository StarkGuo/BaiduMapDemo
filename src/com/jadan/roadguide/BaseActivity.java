package com.jadan.roadguide;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.jadan.roadguide.util.Utils;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public abstract class BaseActivity extends Activity {
	
	private static final String TAG = "BaseActivity";
	
	/** 天安门坐标 */
	protected LatLng tamPos = new LatLng(39.915112,116.403963);

	protected MapView mapView;
	protected BaiduMap baiduMap;

	// 这里加final是为了不让子类覆盖，原因是为了预防这里的一些类还没初始化的时候就被子类调用
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//获取地图控件引用  
        mapView = (MapView) findViewById(R.id.bmapView); 
        
        baiduMap = mapView.getMap();	// 获取地图控制器
        
        // 1.	隐藏缩放按钮、比例尺
        // mapView.showScaleControl(false);	// 隐藏比例按钮，默认是显示的
        // mapView.showZoomControls(false);	// 隐藏缩放按钮，默认是显示的
        
        // 2.	获取获取最小（3）、最大缩放级别（20）
        float maxZoomLevel = baiduMap.getMaxZoomLevel(); // 获取地图最大缩放级别
        float minZoomLevel = baiduMap.getMinZoomLevel(); // 获取地图最小缩放级别
        Log.i(TAG, "minZoomLevel = " + minZoomLevel + ", maxZoomLevel" + maxZoomLevel);
        
		// 3.	没网的情况下，设置地图中心点为天安门
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(tamPos);
        baiduMap.setMapStatus(mapStatusUpdate);
        
        // 4.	设置地图缩放为最大
        mapStatusUpdate = MapStatusUpdateFactory.zoomTo(maxZoomLevel);
        baiduMap.setMapStatus(mapStatusUpdate);
        
        // 6.	获取地图Ui控制器：隐藏指南针
        // UiSettings uiSettings = baiduMap.getUiSettings();
        // uiSettings.setCompassEnabled(false);	//  不显示指南针
        
        init();
	}
	
	/** 这个方法让子类实现 */
	public abstract void init();
	
	/**
	 * 在屏幕中央显示一个Toast
	 * @param text
	 */
	public void showToast(CharSequence text) {
		Utils.showToast(this, text);
	}
	
	@Override  
	protected void onDestroy() {  
		super.onDestroy();  
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
		mapView.onDestroy();  
	} 
	
	@Override  
	protected void onResume() {  
		super.onResume();  
		//在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
		mapView.onResume();  
	}  
	
	@Override  
	protected void onPause() {  
		super.onPause();  
		//在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
		mapView.onPause();  
	}  
}
