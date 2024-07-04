package to.holepunch.bare.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import to.holepunch.bare.kit.IPC;
import to.holepunch.bare.kit.RPC;
import to.holepunch.bare.kit.Worklet;

public class MainActivity extends Activity {
  static {
    System.loadLibrary("bare_android");
  }

  Worklet worklet;
  IPC ipc;
  RPC rpc;

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

    rpc = new RPC(ipc, (req, error) -> {
      if (req.command.equals("ping")) {
        Log.i("bare", req.data("UTF-8"));

        req.reply("Pong from Android", "UTF-8");
      }
    });

    RPC.OutgoingRequest req = rpc.request("ping");

    req.send("Ping from Android", "UTF-8");

    req.reply("UTF-8", (data, error) -> {
      Log.i("bare", data);
    });
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
      ipc = null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    worklet.terminate();
    worklet = null;
  }
}
