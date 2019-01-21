package com.zii.base.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.LayoutRes;
import butterknife.ButterKnife;

/**
 * BaseBusinessActivity
 * Create by zii at 2018/11/11.
 */
public abstract class BaseBusinessActivity extends BaseActivity implements IBaseView {

  protected View mContentView;

  /**
   * 上次点击时间
   */
  private long lastClick = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle bundle = getIntent().getExtras();
    initData(bundle);
    setBaseView(bindLayout());
    initView(savedInstanceState, mContentView);
    doBusiness();
  }

  protected void setBaseView(@LayoutRes int layoutId) {
    if (layoutId == 0) return;
    setContentView(mContentView = LayoutInflater.from(this).inflate(layoutId, null));
    ButterKnife.bind(this);
  }

  /**
   * 判断是否快速点击
   *
   * @return {@code true}: 是<br>{@code false}: 否
   */
  private boolean isFastClick() {
    long now = System.currentTimeMillis();
    if (now - lastClick >= 200) {
      lastClick = now;
      return false;
    }
    return true;
  }

  @Override
  public void onClick(final View view) {
    if (!isFastClick()) onWidgetClick(view);
  }
}
