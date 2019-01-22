package com.zii.base.widget.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import com.zii.base.util.ResourceUtils;
import com.zii.base.util.ViewHelper;

/**
 * PopupItemsDialog
 * 常用的一个个选项popup类型弹框
 * Create by zii at 2018/11/1.
 */
public class PopupItemsDialog extends PopupDialog {

  public PopupItemsDialog(@NonNull Context context, int widthDp, @DrawableRes int bgRes) {
    super(context, widthDp, bgRes);
  }

  @Override
  protected void setContentView() {
    mContentView = new LinearLayout(getContext());
    ((LinearLayout) mContentView).setOrientation(LinearLayout.VERTICAL);
    mContentView.setBackgroundResource(mBgRes);
  }

  public PopupItemsDialog addItem(final String text, final OnClickChild onClickChild) {
    return addItem(new GetTextView() {
      @Override
      public TextView onTextView() {
        return getDefaultTextView(text);
      }
    }, onClickChild);
  }

  public PopupItemsDialog addItem(GetTextView getTextView, final OnClickChild onClickChild) {
    TextView tv = getTextView.onTextView();
    tv.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (onClickChild != null) {
          onClickChild.onClick(PopupItemsDialog.this, v);
        }
      }
    });
    ((LinearLayout) mContentView).addView(tv);
    return this;
  }

  private TextView getDefaultTextView(String text) {
    int paddingTopBottom = ViewHelper.dp2px(12);
    int textSizeDp = 16;
    int textColor = ResourceUtils.getColor("#333333");

    ColorDrawable pressColor = new ColorDrawable(ResourceUtils.getColor("#cacaca"));
    ColorDrawable normalColor = new ColorDrawable(Color.WHITE);
    Drawable background = ViewHelper.generatePressedSelector(pressColor, normalColor);

    TextView tv = new TextView(getContext());
    tv.setText(text);
    ViewGroup.LayoutParams params =
        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    tv.setLayoutParams(
        params);
    tv.setPadding(0, paddingTopBottom, 0, paddingTopBottom);

    tv.setBackground(background);
    tv.setGravity(Gravity.CENTER);
    tv.setTextColor(textColor);
    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSizeDp);
    return tv;
  }

  public interface GetTextView {

    TextView onTextView();
  }
}
