package com.zii.base.base.mvp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import com.zii.base.base.BaseActivity;

/**
 * Description:<br>
 *
 * <p> BaseMvpActivity Create by Zii at 2018/6/14. </p>
 */
public abstract class BaseMvpActivity<T extends IBaseMvpPresenter> extends BaseActivity
    implements IBaseMvpView {

  protected View mContentView;

  protected T mPresenter;
  /**
   * 上次点击时间
   */
  private long lastClick = 0;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initPresenter();
    Bundle bundle = getIntent().getExtras();
    initData(bundle);
    setBaseView(bindLayout());
    initView(savedInstanceState, mContentView);
  }

  protected void setBaseView(@LayoutRes int layoutId) {
    if (layoutId == 0) return;
    setContentView(mContentView = LayoutInflater.from(this).inflate(layoutId, null));
    ButterKnife.bind(this);
  }

  private void initPresenter() {
    mPresenter = createPresenter();
    mPresenter.attachView(this);
    getLifecycle().addObserver(mPresenter);
  }

  protected abstract T createPresenter();

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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mPresenter.detachView();
  }
}
