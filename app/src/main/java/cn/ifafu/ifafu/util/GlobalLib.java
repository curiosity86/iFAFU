package cn.ifafu.ifafu.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.inputmethod.InputMethodManager;

public class GlobalLib {

    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

//    public static String getCityName(Context context) {
//        LocationManager locationManager;
//        String contextString = Context.LOCATION_SERVICE;
//        locationManager = (LocationManager) context.getSystemService(contextString);
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(false);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//        String cityName = null;
//        // 取得效果最好的criteria
//        String provider = locationManager.getBestProvider(criteria, true);
//        if (provider == null) {
//            return null;
//        }
//        // 得到坐标相关的信息
//        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(provider);
//        if (location == null) {
//            return null;
//        }
//
//        double latitude = location.getLatitude();
//        double longitude = location.getLongitude();
//        // 更具地理环境来确定编码
//        Geocoder gc = new Geocoder(context, Locale.getDefault());
//        try {
//            // 取得地址相关的一些信息\经度、纬度
//            List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
//            StringBuilder sb = new StringBuilder();
//            if (addresses.size() > 0) {
//                Address address = addresses.get(0);
//                sb.append(address.getLocality()).append("\n");
//                cityName = sb.toString();
//            }
//        } catch (IOException e) {
//        }
//        return cityName;
//    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}
