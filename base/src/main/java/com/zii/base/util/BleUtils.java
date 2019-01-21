package com.zii.base.util;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

/**
 * BleUtils
 * Create by zii at 2019/1/16.
 */
public class BleUtils {

  public static final int REQ_CODE_ENABLE_BLE = 0x01998;
  private static final String[] PERMISSION_LOCATION = new String[] {
      Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION
  };

  @RequiresPermission(Manifest.permission.BLUETOOTH)
  public static boolean isEnable() {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
  }

  @RequiresPermission(allOf = { Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN })
  public static void enableBle() {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (bluetoothAdapter != null) {
      bluetoothAdapter.enable();
    }
  }

  public static boolean isGrantedLocationPermission() {
    return PermissionUtils.isGranted(PERMISSION_LOCATION);
  }

  public static void requestLocationPermission(PermissionUtils.SimpleCallback callback) {
    PermissionUtils.permission(PERMISSION_LOCATION)
        .callback(callback)
        .request();
  }

  public static boolean isSupport() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
        && Utils.getApp()
        .getApplicationContext()
        .getPackageManager()
        .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
  }

  public static void requestEnableBle(Object target) {
    if (target == null || (!(target instanceof Fragment) && !(target instanceof Activity))) {
      return;
    }
    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    if (target instanceof Fragment) {
      ((Fragment) target).startActivityForResult(intent, REQ_CODE_ENABLE_BLE);
    } else {
      ((Activity) target).startActivityForResult(intent, REQ_CODE_ENABLE_BLE);
    }
  }

  @RequiresPermission(allOf = { Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN })
  public static boolean checkPermissionAndTips() {
    if (!isSupport()) {
      ToastUtils.showShort("您的设备不支持蓝牙Ble");
      return false;
    } else if (!isGrantedLocationPermission()) {
      ToastUtils.showShort("搜索连接蓝牙设备，需要授权位置权限");
      requestLocationPermission(new PermissionUtils.SimpleCallback() {
        @Override
        public void onGranted() {

        }

        @Override
        public void onDenied() {
          new AlertDialog.Builder(Utils.getTopActivityOrApp())
              .setMessage("搜索连接蓝牙设备，需要授权位置权限")
              .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  PermissionUtils.launchAppDetailsSettings();
                }
              })
              .show();
        }
      });
      return false;
    } else if (!isEnable()) {
      ToastUtils.showShort("未开启蓝牙，请先开启蓝牙");
      enableBle();
      return false;
    }
    return true;
  }
}
