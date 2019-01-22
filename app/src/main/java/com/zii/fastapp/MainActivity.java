package com.zii.fastapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.zii.base.util.ToastUtils;
import com.zii.base.widget.dialog.PopupDialog;
import com.zii.base.widget.dialog.PopupItemsDialog;
import com.zii.base.widget.dialog.TranslucenceDialog;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void onTestSome(View view) {
    PopupItemsDialog dialog = new PopupItemsDialog(this, 180, 0)
        .addItem("哈哈", new TranslucenceDialog.OnClickChild() {
          @Override
          public void onClick(TranslucenceDialog dialog, View view) {
            dialog.dismiss();
            ToastUtils.showShort(((TextView) view).getText());
          }
        })
        .addItem("Gou", new TranslucenceDialog.OnClickChild() {
          @Override
          public void onClick(TranslucenceDialog dialog, View view) {
            dialog.dismiss();
            ToastUtils.showShort(((TextView) view).getText());
          }
        });
    dialog.setLocation(view, "bottom", PopupDialog.ALIGN_LEFT, 0, 0);
    dialog.show();
  }
}
