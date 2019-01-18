package com.cosmo.library.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jin on 2017/12/22.
 * Description 地图选择位置 在高德demo上修改
 */

public class SelectLocationActivity extends AppCompatActivity
        implements  LocationSource, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener, PoiSearch.OnPoiSearchListener {

    ImageView mClearBtn;
    EditText mSearchText;
    MapView mMapView;
    ListView mListView;
    RecyclerView mRecyclerView;
    TextView mCityTextView;
    ImageView mCityArrowImageView;
    Context context;

    private void findview(){
        mCityArrowImageView = (ImageView) findViewById(R.id.city_arrow_image_view);
        mClearBtn = (ImageView) findViewById(R.id.clear);
        mSearchText = (EditText) findViewById(R.id.keyWord);
        mMapView = (MapView) findViewById(R.id.map);
        mListView = (ListView) findViewById(R.id.listview);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_search);
        mCityTextView = (TextView) findViewById(R.id.city_view);
    }

    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private String poiCity = "杭州";
    private LatLng location;

    private Marker locationMarker;

    private ProgressDialog progDialog = null;
    private GeocodeSearch geocoderSearch;

    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private List<PoiItem> poiItems;// poi数据

    private String searchKey = "";
    String areaId = "";
    private LatLonPoint searchLatlonPoint;

    private List<PoiItem> resultData;

    private SearchResultAdapter searchResultAdapter;

    private boolean isItemClickAction;

    private List<PoiItem> autoTips;

    final boolean cityArrowUp = true;
    final boolean cityArrowDown = false;
    boolean cityArrowStatus = cityArrowDown;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lcoation);
        context = this;
        findview();
        mMapView.onCreate(savedInstanceState);
        init();
        initView();
        initSearch();

        resultData = new ArrayList<>();
    }





    // 城市选择按钮
    public void chooseCity(View view) {
        if (cityArrowStatus) {
            hideCitySearch();
        } else {
            showCitySearch();
        }

        Bundle bundle = new Bundle();
        bundle.putString("areaId", areaId);
       // ArouterUtils.startActivity(ActivityPath.MainConstant.MyAddressSelectWindow, bundle);
    }

  /*  @Subscribe(threadMode = ThreadMode.MAIN)
    public void setLocationArea(SelectLocationEvent event) {
        poiCity = event.getLocation();
        mCityTextView.setText(poiCity);
        areaId = event.getAreaId();
        hideCitySearch();
    }*/

    /**
     * 显示城市选择
     */
    private void showCitySearch() {
        cityArrowStatus = cityArrowUp;
        mCityArrowImageView.setBackground(ContextCompat.getDrawable(this, R.mipmap.door_arrow_up));
    }

    /**
     * 隐藏城市选择
     */
    private void hideCitySearch() {
        cityArrowStatus = cityArrowDown;
        mCityArrowImageView.setBackground(ContextCompat.getDrawable(this, R.mipmap.door_arrow_down));
    }

    private void initView() {

        searchResultAdapter = new SearchResultAdapter(this);
        mListView.setAdapter(searchResultAdapter);

        mListView.setOnItemClickListener(onItemClickListener);

        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString().trim();
                if (newText.length() > 0) {


                    PoiSearch.Query query2 = new PoiSearch.Query(newText, "", poiCity);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
                    query2.setCityLimit(true);
                    query2.setPageSize(20);
                    query2.setPageNum(1);

//					if (searchLatlonPoint != null) {
                    PoiSearch poiSearch2 = new PoiSearch(SelectLocationActivity.this, query2);
                    poiSearch2.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                        @Override
                        public void onPoiSearched(PoiResult poiResult, int resultCode) {

                            if (resultCode == AMapException.CODE_AMAP_SUCCESS && poiResult != null && poiResult.getQuery() != null) {// 正确返回
                                List<PoiItem> temp = new ArrayList<>();
                                List<PoiItem> list = poiResult.getPois();
                                for (PoiItem tip : list) {
                                    // 筛选掉那些没有经纬度的值
                                    if (tip.getLatLonPoint() != null) {
                                        temp.add(tip);
                                    }
                                }
                                autoTips = temp;
                                mAdapter.setNewData(autoTips);
                            } else {
                                Toast.makeText(context, "erroCode " + resultCode , Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPoiItemSearched(PoiItem poiItem, int i) {

                        }
                    });
                    poiSearch2.searchPOIAsyn();
//					}
                } else {
                    clearEvent();
                }
            }

            /**
             * 监听输入框变化，然后改变清除按钮的显示与隐藏
             */
            @Override
            public void afterTextChanged(Editable s) {
                mClearBtn.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
        });

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);

        hideSoftKey(mSearchText);
    }

//    @OnClick(R2.id.clear)
//    public void clear() {
//        mSearchText.setText("");
//    }


    public void clearEvent() {
        mSearchText.requestFocus();
        autoTips.clear();
        mAdapter.setNewData(autoTips);
        // 弹出键盘
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im != null)
            im.showSoftInput(mSearchText, 0);
    }

    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
        }

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                if (!isItemClickAction && !isInputKeySearch) {
                    geoAddress();
                    startJumpAnimation();
                }
                searchLatlonPoint = new LatLonPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);
                isInputKeySearch = false;
                isItemClickAction = false;
            }
        });

        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                addMarkerInScreenCenter(null);
            }
        });
    }

    /**
     * 设置一些AMap的属性
     */
    private void setUpMap() {
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //设置定位蓝点的icon图标方法，需要用到BitmapDescriptor类对象作为参数。
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon_cricle));
        //连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);

        //设置定位蓝点精度圆圈的边框颜色的方法。
//		myLocationStyle.strokeColor(ContextCompat.getColor(this, R.color.location_color));
        //设置定位蓝点精度圆圈的填充颜色的方法。
        myLocationStyle.radiusFillColor(Color.parseColor("#00000000"));
        //设置定位蓝点精度圈的边框宽度的方法。
        myLocationStyle.strokeWidth(0f);

        aMap.setMyLocationStyle(myLocationStyle);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置默认定位按钮是否显示
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if(null != mLocationClient){
            mLocationClient.onDestroy();
        }

    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);
                location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                searchLatlonPoint = new LatLonPoint(location.latitude, location.longitude);
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f));
                isInputKeySearch = false;
                poiCity = amapLocation.getCity();
                mCityTextView.setText(poiCity);
            } else {
                poiCity = "杭州";
                mCityTextView.setText(poiCity);
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setOnceLocation(true);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }


    /**
     * 响应逆地理编码
     */
    public void geoAddress() {
        showDialog();
        if (searchLatlonPoint != null){
            RegeocodeQuery query = new RegeocodeQuery(searchLatlonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            geocoderSearch.getFromLocationAsyn(query);
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        currentPage = 0;
        query = new PoiSearch.Query(searchKey, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setCityLimit(true);
        query.setPageSize(20);
        query.setPageNum(currentPage);

        if (searchLatlonPoint != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(searchLatlonPoint, 1000, true));//
            poiSearch.searchPOIAsyn();
        }
    }

    private String mainLocation = "";

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                mainLocation = result.getRegeocodeAddress().getProvince() + result.getRegeocodeAddress().getCity() + result.getRegeocodeAddress().getDistrict() + result.getRegeocodeAddress().getTownship();
                PoiItem firstItem = new PoiItem("regeo", searchLatlonPoint, mainLocation, mainLocation);
                doSearchQuery();
            }
        } else {
            Toast.makeText(SelectLocationActivity.this, "error code is " + rCode, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    /**
     * POI搜索结果回调
     * @param poiResult 搜索结果
     * @param resultCode 错误码
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int resultCode) {
        if (resultCode == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getQuery() != null) {
                if (poiResult.getQuery().equals(query)) {
                    poiItems = poiResult.getPois();
                    if (poiItems != null && poiItems.size() > 0) {
//						if (isClickTip) {
//							isClickTip = false;
//							PoiItem poiItem = poiItems.get(0);
//
//							// 过来获取一个省市区
////							confirmResult(tipName, poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getAdName(), poiItem.getAdCode(), tipLongitude, tipLatitude);
//						} else {
                        updateListView(poiItems);
//						}
                    } else {
                        Toast.makeText(SelectLocationActivity.this, "无搜索结果", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(SelectLocationActivity.this, "无搜索结果", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 更新列表中的item
     * @param poiItems poiItems
     */
    private void updateListView(List<PoiItem> poiItems) {
        resultData.clear();
        resultData.addAll(poiItems);

        mListView.smoothScrollToPosition(0);
        searchResultAdapter.setData(resultData);
        searchResultAdapter.notifyDataSetChanged();
    }


    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    /**
     * 搜索结果点击事件
     */
    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            PoiItem poiItem = (PoiItem) searchResultAdapter.getItem(position);
            String result = poiItem.getTitle();
//			String result = poiItem.getCityName() + poiItem.getAdName() + poiItem.getSnippet() + poiItem.getTitle();
            confirmResult(result, poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getAdName(),
                    poiItem.getAdCode(), poiItem.getLatLonPoint().getLongitude(), poiItem.getLatLonPoint().getLatitude());

        }
    };

    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在加载...");
        progDialog.show();
    }

    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    private void addMarkerInScreenCenter(LatLng locationLatLng) {
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        locationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f,1f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon_orange)));
        //设置Marker在屏幕上,不跟随地图移动
        locationMarker.setPositionByPixels(screenPosition.x,screenPosition.y);
        locationMarker.setZIndex(1);

    }

    /**
     * 屏幕中心marker 跳动
     */
    public void startJumpAnimation() {

        if (locationMarker != null ) {
            //根据屏幕距离计算需要移动的目标点
            final LatLng latLng = locationMarker.getPosition();
            Point point =  aMap.getProjection().toScreenLocation(latLng);
            point.y -= dip2px(this,125);
            LatLng target = aMap.getProjection()
                    .fromScreenLocation(point);
            //使用TranslateAnimation,填写一个需要移动的目标点
            Animation animation = new TranslateAnimation(target);
            animation.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    // 模拟重加速度的interpolator
                    if(input <= 0.5) {
                        return (float) (0.5f - 2 * (0.5 - input) * (0.5 - input));
                    } else {
                        return (float) (0.5f - Math.sqrt((input - 0.5f)*(1.5f - input)));
                    }
                }
            });
            //整个移动所需要的时间
            animation.setDuration(600);
            //设置动画
            locationMarker.setAnimation(animation);
            //开始动画
            locationMarker.startAnimation();

        } else {
            Log.e("ama","screenMarker is null");
        }
    }

    //dip和px转换
    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 转换成千米
     */
    String changeToKilometer(int distance) {

        double temp = distance * 0.001;
        DecimalFormat df = new DecimalFormat("0.0");//格式化
        String distanceStr = df.format(temp);

        return distanceStr + "km";
    }



    boolean isClickTip = false;
    String tipName = "";
    double tipLongitude = 0;
    double tipLatitude = 0;

    private RecyclerListAdapter<PoiItem> mAdapter;
    private void initSearch() {
        autoTips = new ArrayList<>();
        mAdapter = new RecyclerListAdapter<PoiItem>(this, autoTips, R.layout.item_search_route) {
            @Override
            public void convert(RecycleCommonViewHolder helper, PoiItem poiItem, final int position) {

                helper.setText(R.id.title, poiItem.getTitle())
                        .setText(R.id.content, poiItem.getCityName() + poiItem.getAdName() + poiItem.getSnippet());

                if (location != null) {
                    if (poiItem.getLatLonPoint() != null) {
                        int distance = (int) AMapUtils.calculateLineDistance(AMapUtil.convertToLatLng(poiItem.getLatLonPoint()) , location);
                        String distanceStr = distance > 1000 ? changeToKilometer(distance) : "< " + distance + "m";
                        helper.setText(R.id.miles, distanceStr);
                    } else {
                        helper.setText(R.id.miles, "");
                    }
                } else {
                    helper.setText(R.id.miles, "");
                }

                helper.setOnItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void itemClick() {
                        if (autoTips != null && autoTips.size() > position) {
                            PoiItem poiItem = autoTips.get(position);
                            hideSoftKey(mSearchText);

                            tipName = poiItem.getTitle();
                            tipLongitude = poiItem.getLatLonPoint().getLongitude();
                            tipLatitude = poiItem.getLatLonPoint().getLatitude();

                            isClickTip = true;
//							searchPoi(tip);

                            String result = poiItem.getTitle();
                            confirmResult(result, poiItem.getProvinceName() + poiItem.getCityName() + poiItem.getAdName(),
                                    poiItem.getAdCode(), poiItem.getLatLonPoint().getLongitude(), poiItem.getLatLonPoint().getLatitude());
                        }
                    }
                });
            }
        };

        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (imm != null) {
                    imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
                }

                isClickTip = false;

                if (autoTips.size() == 0) {
                    mRecyclerView.setVisibility(View.GONE);
                    mSearchText.clearFocus();
                }
                return false;
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);

        mSearchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mRecyclerView.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            }
        });
    }



    private boolean isInputKeySearch;

    private void hideSoftKey(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     *
     * @param result poi
     * @param longitude 经度
     * @param latitude 纬度
     */

    public void confirmResult(String result, String AreaAddr, String areaCode, double longitude, double latitude) {
        Intent intent = new Intent();
        intent.putExtra("resultStr", result);
        intent.putExtra("AreaAddr", AreaAddr);
        intent.putExtra("areaCode", areaCode);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        setResult(RESULT_OK, intent);
        finish();
    }
}
