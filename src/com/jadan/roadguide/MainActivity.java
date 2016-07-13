package com.jadan.roadguide;
import java.util.ArrayList;
import java.util.List;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.jadan.roadguide.util.Utils;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import overlayutil.DrivingRouteOverlay;
import overlayutil.PoiOverlay;
import overlayutil.WalkingRouteOverlay;


public class MainActivity extends BaseActivity  {
	
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private double latitude;    //实时的纬度
	private double longitude;	//实时的精度
	private LatLng resultPosition;
	private Button bt_right;
	private EditText et_search;
	private String searchContent;
	protected RoutePlanSearch routePlanSearch;
	private WalkingRouteOverlay overlay;
	private Button bt_center;
	private Button bt_left;
	private MapStatusUpdate mapStatusUpdate;
	@Override
	public void init() {
		bt_right = (Button) findViewById(R.id.bt_right);
		et_search = (EditText) findViewById(R.id.et_search);
	
		bt_center = (Button) findViewById(R.id.bt_center);
		bt_left = (Button) findViewById(R.id.bt_left);
		//初始化按键点击事件
		initButton();	
		//覆盖物对象
		overlay = new WalkingRouteOverlay(baiduMap);
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
	    mLocationClient.registerLocationListener( myListener );    //注册监听函数
	    initLocation();				//设置定位参数
	    baiduMap.setMyLocationEnabled(true);
	    setLocationCongif();
	    mLocationClient.start();
	
	}
	
	/**
	 * 初始化按键事件
	 */
	private void initButton() {
		mapStatusUpdate = null;
		bt_center.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mapStatusUpdate = MapStatusUpdateFactory.zoomOut();
				baiduMap.setMapStatus(mapStatusUpdate);
			}
		});
		bt_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mapStatusUpdate = MapStatusUpdateFactory.zoomIn();
				baiduMap.setMapStatus(mapStatusUpdate);
			}
		});
		bt_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchContent = et_search.getText().toString();
						    
			    //注册搜索附近对象
			    PoiSearch poiSearch = PoiSearch.newInstance();
			    poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
					
					@Override
					public void onGetPoiResult(PoiResult result) {
						// TODO Auto-generated method stub
						// TODO Auto-generated method stub
						if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
							Utils.showToast(getApplicationContext(), "没搜索到结果哦");
						}
						PoiOverlay poiOverlay = new PoiOverlay(baiduMap){
						
							@Override
							public boolean onPoiClick(int i) {
								// TODO Auto-generated method stub
								PoiInfo poiInfo = getPoiResult().getAllPoi().get(i);
								resultPosition = poiInfo.location;
								Utils.showToast(getApplicationContext(), poiInfo.name+","+poiInfo.address);
								showTheWay();
								return true;
							}

							private void showTheWay() {
								// TODO Auto-generated method stub
								
								routePlanSearch = RoutePlanSearch.newInstance();
								routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener(){
									@Override
									public void onGetWalkingRouteResult(WalkingRouteResult result) {
										// TODO Auto-generated method stub
										baiduMap.setOnMarkerClickListener(overlay);
										overlay.removeFromMap();
										List<WalkingRouteLine> routeLines = result.getRouteLines();	// »ñÈ¡µ½ËùÓÐµÄËÑË÷Â·Ïß£¬×îÓÅ»¯µÄÂ·Ïß»áÔÚ¼¯ºÏµÄÇ°Ãæ
										overlay.setData(routeLines.get(0));	// °ÑËÑË÷½á¹ûÉèÖÃµ½¸²¸ÇÎï
										overlay.addToMap();					// °ÑËÑË÷½á¹ûÌí¼Óµ½µØÍ¼
										overlay.zoomToSpan();				// °ÑËÑË÷½á¹ûÔÚÒ»¸öÆÁÄ»ÄÚÏÔÊ¾Íê
									}
									
									@Override
									public void onGetTransitRouteResult(TransitRouteResult arg0) {
										
									}
									
									@Override
									public void onGetDrivingRouteResult(DrivingRouteResult result) {
									}
									
									@Override
									public void onGetBikingRouteResult(BikingRouteResult arg0) {
										// TODO Auto-generated method stub
										
									}
								} );
									routePlanSearch.walkingSearch(getSearchWayParams());
									
							}
						};
						baiduMap.setOnMarkerClickListener(poiOverlay);
						poiOverlay.setData(result);
						poiOverlay.addToMap();
						poiOverlay.zoomToSpan();
					}
					
					@Override
					public void onGetPoiIndoorResult(PoiIndoorResult arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onGetPoiDetailResult(PoiDetailResult arg0) {
						// TODO Auto-generated method stub
						
					}
				});
			    poiSearch.searchInBound(getSearchParams());
			}
		});
	}

	/**
	 * @return
	 */
	private WalkingRoutePlanOption getSearchWayParams() {
		WalkingRoutePlanOption params = new WalkingRoutePlanOption();
		LatLng hmPos = new LatLng(latitude, longitude);
		params.from(PlanNode.withLocation(hmPos));	// 设置起点
		params.to(PlanNode.withLocation(resultPosition));	// 设置终点
		return params;
	}
	
	/**
	 * @return
	 */
	private PoiBoundSearchOption getSearchParams() {
		// TODO Auto-generated method stub
		PoiBoundSearchOption params =  new PoiBoundSearchOption();
		LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(latitude+0.0001, longitude+0.0001)).include(new LatLng(latitude-0.0001, longitude-0.0001)).build();
		params.bound(bounds);
		params.keyword(searchContent+"");
		return params;
	}

	private void setLocationCongif(){
		MyLocationConfiguration.LocationMode mode = MyLocationConfiguration.LocationMode.FOLLOWING;
	    boolean enableDirection = true;
	   // BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.jiantou1_pressed);
	    MyLocationConfiguration config = new MyLocationConfiguration(mode, enableDirection, null);
		baiduMap.setMyLocationConfigeration(config);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLocationClient.stop();
	}
	
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location!=null) {
				MyLocationData.Builder  builder =new MyLocationData.Builder();
				builder.accuracy(location.getRadius());
				builder.direction(location.getDirection());
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				builder.latitude(latitude);
				builder.longitude(longitude);
				MyLocationData locationData = builder.build();
				baiduMap.setMyLocationData(locationData);
			}
			//Receive Location
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());// 单位：公里每小时
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\nheight : ");
				sb.append(location.getAltitude());// 单位：米
				sb.append("\ndirection : ");
				sb.append(location.getDirection());// 单位度
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append("\ndescribe : ");
				sb.append("gps定位成功");

			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
				sb.append("\ndescribe : ");
				sb.append("网络定位成功");
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				sb.append("\ndescribe : ");
				sb.append("离线定位成功，离线定位结果也是有效的");
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				sb.append("\ndescribe : ");
				sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				sb.append("\ndescribe : ");
				sb.append("网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				sb.append("\ndescribe : ");
				sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}
			sb.append("\nlocationdescribe : ");
			sb.append(location.getLocationDescribe());// 位置语义化信息
			List<Poi> list = location.getPoiList();// POI数据
			if (list != null) {
				sb.append("\npoilist size = : ");
				sb.append(list.size());
				for (Poi p : list) {
					sb.append("\npoi= : ");
					sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
				}
			}
			Log.i("BaiduLocationApiDem", sb.toString());
		}
	}
	/**
	 * 
	 */
	private void initLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy
				);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
		int span=1000;
		option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);//可选，默认false,设置是否使用gps
		option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死  
		option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
	}

}
