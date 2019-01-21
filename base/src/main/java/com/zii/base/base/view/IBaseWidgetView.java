package com.zii.base.base.view;

import android.content.res.TypedArray;
import androidx.annotation.StyleableRes;

/**
 * IBaseWidgetView
 * Create by zii at 2018/11/11.
 */
public interface IBaseWidgetView {

  /**
   * 填充布局
   */
  void inflaterLayout();

  /**
   * 初始化参数
   */
  void initAttrs();

  /**
   * 设置自定义样式
   *
   * @return 样式
   */
  @StyleableRes
  int[] bindStyleable();

  /**
   * 设置样式具体属性参数
   *
   * @param typedArray 样式类型数组
   * @param index 样式id
   */
  void setStyleAttr(TypedArray typedArray, int index);

  /**
   * 初始化画笔
   */
  void initPaints();

  /**
   * 初始化view
   */
  void initViews();
}
