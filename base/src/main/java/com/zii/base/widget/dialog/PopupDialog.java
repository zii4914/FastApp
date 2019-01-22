package com.zii.base.widget.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import com.zii.base.util.BarUtils;
import com.zii.base.util.ScreenUtils;
import com.zii.base.util.ViewHelper;

/**
 * PopupDialog
 * 以Dialog为本体，实现半透明Popup效果
 * Create by zii at 2018/11/1.
 */
public abstract class PopupDialog extends TranslucenceDialog {

  public static final int ALIGN_LEFT = 1;
  public static final int ALIGN_RIGHT = 2;
  public static final int ALIGN_TOP = 3;
  public static final int ALIGN_BOTTOM = 4;
  public static final int ALIGN_CENTER = 5;

  @DrawableRes
  protected int mBgRes;

  public PopupDialog(@NonNull Context context, int widthDp, @DrawableRes int bgRes) {
    super(context, widthDp, ViewGroup.LayoutParams.WRAP_CONTENT);

    mBgRes = bgRes;
    defaultStyle();
    setGravity(Gravity.TOP | Gravity.START);
    setContentView();
  }

  protected abstract void setContentView();

  /**
   * @param anchorView 依赖的view
   * @param anchorPlace 相对于anchor所处的位置"top","bottom","left","right"
   * @param align 与某一边对齐
   * @param offX x偏移
   * @param offY y偏移
   */
  public PopupDialog setLocation(View anchorView, String anchorPlace, @AnchorAlign int align, int offX, int offY) {
    int[] location = new int[2];
    anchorView.getLocationOnScreen(location);

    float anchorLocX = location[0];
    float anchorLocY = location[1];
    float anchorHeight = anchorView.getMeasuredHeight();
    float anchorWidth = anchorView.getMeasuredWidth();

    float contentWith = ViewHelper.dp2px(mWidthDp);
    float contentHeight = ViewHelper.getMeasuredHeight(mContentView);

    int screenWidth = ScreenUtils.getScreenWidth();
    int screenHeight = ScreenUtils.getScreenHeight();

    float marginTop = 0;
    float marginLeft = 0;

    switch (anchorPlace) {
      case "top":
        marginTop = anchorLocY - contentHeight - BarUtils.getStatusBarHeight() + offY;
        if (align == ALIGN_LEFT) {
          marginLeft = anchorLocX + offX;
        } else if (align == ALIGN_RIGHT) {
          marginLeft = anchorLocX + anchorWidth - contentWith + offX;
        } else {
          marginLeft = anchorLocX + (anchorWidth / 2) - (contentWith / 2) + offX;
        }
        break;
      case "bottom":
        marginTop = anchorLocY + anchorHeight - BarUtils.getStatusBarHeight() + offY;
        if (align == ALIGN_LEFT) {
          marginLeft = anchorLocX + offX;
        } else if (align == ALIGN_RIGHT) {
          marginLeft = anchorLocX + anchorWidth - contentWith + offX;
        } else {
          marginLeft = anchorLocX + (anchorWidth / 2) - (contentWith / 2) + offX;
        }
        break;
      case "left":
        marginLeft = anchorLocX - contentWith + offX;
        if (align == ALIGN_TOP) {
          marginTop = anchorLocY - BarUtils.getStatusBarHeight() + offY;
        } else if (align == ALIGN_BOTTOM) {
          marginTop = anchorLocY + anchorHeight - contentHeight - BarUtils.getStatusBarHeight() + offY;
        } else {
          marginTop = anchorLocY + (anchorHeight / 2) - (contentHeight / 2) - BarUtils.getStatusBarHeight() + offY;
        }
        break;
      case "right":
        marginLeft = anchorLocX + anchorWidth + offX;
        if (align == ALIGN_TOP) {
          marginTop = anchorLocY - BarUtils.getStatusBarHeight() + offY;
        } else if (align == ALIGN_BOTTOM) {
          marginTop = anchorLocY + anchorHeight - contentHeight - BarUtils.getStatusBarHeight() + offY;
        } else {
          marginTop = anchorLocY + (anchorHeight / 2) - (contentHeight / 2) - BarUtils.getStatusBarHeight() + offY;
        }
        break;
      default:
        break;
    }

    mLeftMargin = (int) marginLeft;
    mTopMargin = (int) marginTop;
    return this;
  }

  @IntDef({ ALIGN_LEFT, ALIGN_TOP, ALIGN_RIGHT, ALIGN_BOTTOM, ALIGN_CENTER })
  public @interface AnchorAlign {

  }
}
