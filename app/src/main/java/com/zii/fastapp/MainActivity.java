package com.zii.fastapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  TextView mTv;
  private TestCollection mTestCollection;
  private ImageView mIv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mTv = findViewById(R.id.tv);
    mIv = findViewById(R.id.iv);

    mTestCollection = new TestCollection(this);
  }

  public void onTestSome(View view) {
    //mTestCollection.testPopupDialog(view);
    //mTestCollection.testTakePicture(view);
    mTestCollection.testQuick(view);
    mTv.setText(getDisplayText());
  }

  private String getDisplayText() {
    String text = "";

    text = mTestCollection.getVersions();

    return text;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Object o = null;
    switch (requestCode) {
      case 111:
        o = mTestCollection.onReceiverResult(requestCode, resultCode, data);
        mIv.setImageURI((Uri) o);
        break;
    }
  }
}
