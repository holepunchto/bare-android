package com.holepunch.bare;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
  static {
    System.loadLibrary("bare_android_shared");
  }

  public native void init();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    init();
  }
}
