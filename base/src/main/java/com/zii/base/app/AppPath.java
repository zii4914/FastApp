package com.zii.base.app;

import com.zii.base.util.FileUtils;
import com.zii.base.util.PathUtils;
import java.io.File;

/**
 * AppPath
 * Create by zii at 2019/1/23.
 */
public class AppPath {

  public static final String APP_STORAGE_NAME = "FastApp";

  public static final String APP_STORAGE = PathUtils.getExternalStoragePath() + File.separator + APP_STORAGE_NAME;

  public static final String APP_STORAGE_PICTURE =
      PathUtils.getExternalStoragePath() + File.separator + APP_STORAGE_NAME + File.separator + "Picture";

  public static final String APP_STORAGE_VIDEO =
      PathUtils.getExternalStoragePath() + File.separator + APP_STORAGE_NAME + File.separator + "Video";

  public static final String APP_STORAGE_FILE =
      PathUtils.getExternalStoragePath() + File.separator + APP_STORAGE_NAME + File.separator + "File";

  public static final String APP_FILE_DCIM = PathUtils.getExternalAppDcimPath();

  public static final String APP_FILE_PICTURE = PathUtils.getExternalAppDcimPath();

  public static final String APP_CACHE = PathUtils.getExternalAppCachePath();

  public static final String APP_FILE_VIDEO = PathUtils.getExternalAppMoviesPath();

  public static final String APP_FILE_DOWNLOAD = PathUtils.getExternalAppDownloadPath();

  public static final String APP_FILE_DOCUMENT = PathUtils.getExternalAppDocumentsPath();

  public static void initDir() {
    FileUtils.createOrExistsDir(APP_STORAGE);
    FileUtils.createOrExistsDir(APP_STORAGE_PICTURE);
    FileUtils.createOrExistsDir(APP_STORAGE_VIDEO);
    FileUtils.createOrExistsDir(APP_STORAGE_FILE);
  }
}
