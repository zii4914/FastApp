package com.zii.base.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.DrawableRes;

/**
 * BaseViewHelper
 * Create by zii at 2018/10/13.
 */
public class ViewHelper {

  public static Rect emptyRect(Rect rect) {
    rect.setEmpty();
    return rect;
  }

  public static Path resetPath(Path path) {
    path.reset();
    return path;
  }

  public static Paint newPaint() {
    Paint paint = new Paint();
    paint.setAntiAlias(true);
    paint.setDither(true);
    return paint;
  }

  public static Paint resetPaint(Paint paint) {
    paint.reset();
    paint.setDither(true);
    paint.setAntiAlias(true);
    return paint;
  }

  /**
   * 获得字体本身实际的绘制区域宽度，即无左右边距
   */
  public static int getTextBoundsWidth(Paint paint, String text, Rect rect) {
    if (TextUtils.isEmpty(text)) {
      return 0;
    }
    rect.setEmpty();
    paint.getTextBounds(text, 0, text.length(), rect);
    return rect.right - rect.left;
  }

  /**
   * 获得字体本身实际的绘制区域高度，即无上下边距
   */
  public static int getTextBoundsHeight(Paint paint, String text, Rect rect) {
    if (TextUtils.isEmpty(text)) {
      return 0;
    }
    rect.setEmpty();
    paint.getTextBounds(text, 0, text.length(), rect);
    return rect.bottom - rect.top;
  }

  /**
   * 获得字体本身实际的绘制区域，无上下左右边距
   */
  public static Rect getTextBounds(Paint paint, String text, Rect rect) {
    rect.setEmpty();
    if (TextUtils.isEmpty(text)) {
      return rect;
    }
    paint.getTextBounds(text, 0, text.length(), rect);
    return rect;
  }

  /**
   * 获得文本绘制后的宽度，包含左右边距
   */
  public static float getTextWidth(Paint paint, String text) {
    return paint.measureText(text);
  }

  /**
   * 获取文本占位高度，使用bottom - top<br>
   * <p>
   * top:在一个大小确定的字体中，被当做最高字形，基线(base)上方的最大距离。<br>
   * ascent:单行文本中，在基线(base)上方被推荐的距离。<br>
   * descent:单行文本中，在基线(base)下方被推荐的距离。<br>
   * bottom:在一个大小确定的字体中，被当做最低字形,基线(base)下方的最大距离。<br>
   * </p>
   * 关于descent、ascen、bottom、top：https://img-blog.csdn.net/20160704230318475
   */
  public static float getTextHeight(Paint paint, String text) {
    Paint.FontMetrics fm = paint.getFontMetrics();
    float height = fm.bottom - fm.top + fm.leading;
    return height;
  }

  /**
   * 使用app的density，进行转换
   */
  public static int dp2px(float dp) {
    final float scale = Utils.getAppResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
  }

  /**
   * 使用系统的density进行转换
   */
  public static int dp2pxInSys(float dp) {
    final float scale = Utils.getAppResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
  }

  public static int dp8() {
    final float scale = Utils.getAppResources().getDisplayMetrics().density;
    return (int) (8 * scale + 0.5f);
  }

  public static int dp12() {
    final float scale = Utils.getAppResources().getDisplayMetrics().density;
    return (int) (12 * scale + 0.5f);
  }

  public static int dp16() {
    final float scale = Utils.getAppResources().getDisplayMetrics().density;
    return (int) (16 * scale + 0.5f);
  }

  public static int sp2px(float sp) {
    final float fontScale = Utils.getAppResources().getDisplayMetrics().scaledDensity;
    return (int) (sp * fontScale + 0.5f);
  }

  /**
   * 获取屏幕缩放系数，xxxhdpi为3x，1080设计为3x，density为3.0，但是有些系统2.75或其他的
   */
  public static float getAppScreenScale() {
    return Utils.getAppResources().getDisplayMetrics().density;
  }

  private static Context getContext() {
    return Utils.getApp();
  }

  public static int getScreenWidth() {
    return getScreenSize().x;
  }

  public static int getScreenHeight() {
    return getScreenSize().y;
  }

  public static Point getScreenSize() {
    WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    Point point = new Point();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      //noinspection ConstantConditions
      wm.getDefaultDisplay().getRealSize(point);
    } else {
      //noinspection ConstantConditions
      wm.getDefaultDisplay().getSize(point);
    }
    return point;
  }

  /**
   * Return the width of view.
   *
   * @param view The view.
   * @return the width of view
   */
  public static int getMeasuredWidth(final View view) {
    return measureView(view)[0];
  }

  /**
   * Return the height of view.
   *
   * @param view The view.
   * @return the height of view
   */
  public static int getMeasuredHeight(final View view) {
    return measureView(view)[1];
  }

  /**
   * Measure the view.
   *
   * @param view The view.
   * @return arr[0]: view's width, arr[1]: view's height
   */
  public static int[] measureView(final View view) {
    ViewGroup.LayoutParams lp = view.getLayoutParams();
    if (lp == null) {
      lp = new ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
      );
    }
    int widthSpec = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
    int lpHeight = lp.height;
    int heightSpec;
    if (lpHeight > 0) {
      heightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY);
    } else {
      heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    }
    view.measure(widthSpec, heightSpec);
    return new int[] { view.getMeasuredWidth(), view.getMeasuredHeight() };
  }

  public static Drawable generateEnableSelector(Drawable enable, Drawable disable) {
    return generateSelector(android.R.attr.state_enabled, enable, disable);
  }

  public static Drawable generateEnableSelector(@DrawableRes int enable, @DrawableRes int disable) {
    return generateSelector(android.R.attr.state_enabled, enable, disable);
  }

  public static Drawable generatePressedSelector(Drawable pressed, Drawable normal) {
    return generateSelector(android.R.attr.state_pressed, pressed, normal);
  }

  public static Drawable generatePressedSelector(@DrawableRes int pressed,
      @DrawableRes int normal) {
    return generateSelector(android.R.attr.state_pressed, pressed, normal);
  }

  /**
   * 生成定制属性及属性对应状态Drawable的选择器
   *
   * @param stateSet 属性集合，可以添加多个属性，如{{android.R.attr.state_pressed,-android.R.attr
   * .state_enable},{}}同时应用时
   * @param drawableIds 属性对应
   * @return 选择器
   */
  public static Drawable generateSelector(int[][] stateSet, @DrawableRes int[] drawableIds) {
    int length = stateSet.length;

    Drawable[] drawables = new Drawable[length];
    for (int i = 0; i < length; i++) {
      drawables[i] = ResourceUtils.getDrawable(drawableIds[i]);
    }
    return generateSelector(stateSet, drawables);
  }

  public static Drawable generateSelector(int[][] stateSet, Drawable[] drawables) {
    StateListDrawable stateListDrawable = new StateListDrawable();
    for (int i = 0; i < stateSet.length; i++) {
      stateListDrawable.addState(stateSet[i], drawables[i]);
    }
    return stateListDrawable;
  }

  /**
   * 生成单一属性，两种对立情况的选择器
   *
   * @param stateAttr 状态，如：android.R.attrs.state_enable
   * @param resIdTrue true时的资源id ，可以为drawable，或者color
   * @param resIdFalse false时的资源id，可以为drawable，或者color
   * @return 选择器
   */
  public static Drawable generateSelector(int stateAttr, @DrawableRes int resIdTrue,
      @DrawableRes int resIdFalse) {
    return generateSelector(stateAttr, ResourceUtils.getDrawable(resIdTrue),
        ResourceUtils.getDrawable(resIdFalse));
  }

  public static Drawable generateSelector(int stateAttr, Drawable drawableTrue,
      Drawable drawableFalse) {
    StateListDrawable stateListDrawable = new StateListDrawable();
    stateListDrawable.addState(new int[] { stateAttr }, drawableTrue);
    stateListDrawable.addState(new int[] { -stateAttr }, drawableFalse);
    return stateListDrawable;
  }
}
