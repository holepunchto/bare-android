package to.holepunch.bare.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import to.holepunch.bare.kit.IPC;
import to.holepunch.bare.kit.Worklet;

public class MainActivity extends Activity {
  static {
    System.loadLibrary("bare_android");
  }

  Worklet worklet;
  IPC ipc;

  @Override
  public void
  onCreate (Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    worklet = new Worklet();

    try {
      worklet.start("/app.bundle", getAssets().open("app.bundle"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    ipc = new IPC(worklet);

    ipc.read("UTF-8", (data, exception) -> Log.d("bare", data));
    ipc.write("Hello from Android", "UTF-8");
  }

  @Override
  public void
  onPause () {
    super.onPause();

    worklet.suspend();
  }

  @Override
  public void
  onResume () {
    super.onResume();

    worklet.resume();
  }

  @Override
  public void
  onDestroy () {
    super.onDestroy();

    try {
      ipc.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    worklet.terminate();
    worklet = null;
  }
}
