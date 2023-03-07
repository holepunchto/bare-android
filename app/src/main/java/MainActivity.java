package com.holepunch.pear;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
  static {
    System.loadLibrary("pear_android_shared");
  }

  public native void init();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    init();
  }
}
