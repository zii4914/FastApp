package com.zii.base.util;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import com.zii.base.app.AppConstant;
import com.zii.base.app.AppPath;
import com.zii.base.util.constant.PermissionConstants;
import java.io.File;

/**
 * SysActionUtils
 * Create by zii at 2019/1/22.
 */
public class SysActionUtils {

  public static final String FILE_SUFFIX_TAKE_PICTURE = ".jpg";
  public static final String FILE_PREFIX_TAKE_PICTURE_DCIM = "IMG_";
  public static final String FILE_TAKE_PICTURE_TEMP = "TEMP_PICTURE" + FILE_SUFFIX_TAKE_PICTURE;

  public static void openUrl(String url) {
    if (!RegexUtils.isURL(url)) {
      ToastUtils.showShort("无法打开该链接：" + url);
      return;
    }
    Uri uri = Uri.parse(url);
    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    Utils.getTopActivityOrApp().startActivity(intent);
  }

  public static Object[] takePictureWithoutSave(Activity activity, int requestCode) {
    return takePicture(activity, AppPath.APP_CACHE, requestCode, true);
  }

  public static Object[] takePicture(Activity activity, int requestCode) {
    return takePicture(activity, PathUtils.getExternalPicturesPath(), requestCode);
  }

  public static Object[] takePicture(Activity activity, String saveDir, int requestCode) {
    return takePicture(activity, saveDir, requestCode, false);
  }

  /**
   * @param isTempPicture 是否是临时存储
   * @return 存储图片位置的uri路径
   */
  public static Object[] takePicture(Activity activity, String saveDir, int requestCode, boolean isTempPicture) {
    if (!Utils.getApp().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
      new AlertDialog.Builder(activity)
          .setMessage("您的设备不支持相机，无法打开相机")
          .show();
      return null;
    }

    if (!checkTakePicturePermissions(activity)) {
      return null;
    }

    String fileName;
    if (isTempPicture) {
      fileName = FILE_TAKE_PICTURE_TEMP;
    } else {
      fileName = FILE_PREFIX_TAKE_PICTURE_DCIM
          + TimeUtils.getNowString(TimeUtils.PATTERN_yyyyMMddHHmmss)
          + FILE_SUFFIX_TAKE_PICTURE;
    }
    String fileParentPath = AppPath.APP_STORAGE_PICTURE;

    File file = new File(saveDir, fileName);

    FileUtils.createOrExistsDir(fileParentPath);

    Uri uri;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
      uri = Uri.fromFile(file);
    } else {
      uri = FileProvider.getUriForFile(Utils.getApp(), AppConstant.PROVIDER_AUTHORITY, file);
    }

    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

    activity.startActivityForResult(intent, requestCode);

    return new Object[] { uri, file.getAbsolutePath() };
  }

  /**
   * 把图片添加到画廊
   *
   * @param picPath 图片路径
   */
  public static void galleryAddPicture(String picPath) {
    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    File f = new File(picPath);
    Uri contentUri = Uri.fromFile(f);
    mediaScanIntent.setData(contentUri);
    Utils.getApp().sendBroadcast(mediaScanIntent);
  }

  private static boolean checkTakePicturePermissions(final Activity activity) {
    final String[] permissions = new String[] {
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    };
    if (PermissionUtils.isGranted(permissions)) {
      return true;
    }
    PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.STORAGE)
        .callback(new PermissionUtils.SimpleCallback() {
          @Override
          public void onGranted() {

          }

          @Override
          public void onDenied() {
            new AlertDialog.Builder(ActivityUtils.getTopActivity())
                .setMessage("无法打开相机，缺少相关权限，请授权相机，及存储权限")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    PermissionUtils.launchAppDetailsSettings();
                  }
                })
                .setNegativeButton("取消", null)
                .show();
          }
        }).request();

    return false;
  }
}
