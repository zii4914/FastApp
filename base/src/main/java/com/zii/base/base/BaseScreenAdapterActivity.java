package com.zii.base.base;

import com.zii.base.util.ScreenUtils;

/**
 * MARK mark-zii : 2018/11/11  目前在Dialog，及Toast尚存在一些问题，需要先cancelAdapter，show后恢复，作者说过几天出方案
 *
 * BaseScreenAdapterActivity
 * Create by zii at 2018/11/11.
 */
public abstract class BaseScreenAdapterActivity extends BaseBusinessActivity {

  private static final int designInPx = 720;
  // MARK mark-zii : 2018/11/11  需要修改为设计的尺寸

  @Override
  protected void setBaseView(int layoutId) {
    if (ScreenUtils.isPortrait()) {
      ScreenUtils.adaptScreen4VerticalSlide(this, designInPx);
    } else {
      ScreenUtils.setFullScreen(this);
      ScreenUtils.adaptScreen4HorizontalSlide(this, designInPx);
    }
    super.setBaseView(layoutId);
  }
}
