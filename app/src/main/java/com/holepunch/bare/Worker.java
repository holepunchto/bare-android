package com.holepunch.bare;

public class Worker extends Thread {
  static {
    System.loadLibrary("bare_android_shared");
  }

  public native void run();
}

