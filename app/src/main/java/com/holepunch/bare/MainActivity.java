package com.holepunch.bare;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
  Worker worker;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    worker = new Worker();
    worker.start();
  }
}
