package com.zii.base.base;

/**
 * BaseLazyFragment
 * Create by zii at 2018/11/11.
 */
public abstract class BaseLazyBusinessFragment extends BaseBusinessFragment {

  private boolean isDataLoaded;

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser && mContentView != null && !isDataLoaded) {
      doLazyBusiness();
      isDataLoaded = true;
    }
  }

  @Override
  public void doBusiness() {
    if (getUserVisibleHint()) {
      doLazyBusiness();
      isDataLoaded = true;
    }
  }

  public abstract void doLazyBusiness();
}
