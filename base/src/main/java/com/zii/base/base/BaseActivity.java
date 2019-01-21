package com.zii.base.base;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Description:<br>
 *
 * <p>BaseActivity Create by Zii at 2018/6/14.</p>
 */
public abstract class BaseActivity extends AppCompatActivity {

  protected Activity mActivity;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mActivity = this;
  }
}
