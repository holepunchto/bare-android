package to.holepunch.bare.android

import android.app.Activity
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import to.holepunch.bare.kit.Worklet


class MainActivity : Activity() {
  var worklet: Worklet? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
    }

    worklet = Worklet(null)

    try {
      worklet!!.start("/app.bundle", assets.open("app.bundle"), null)
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }

  public override fun onPause() {
    super.onPause()

    worklet!!.suspend()
  }

  public override fun onResume() {
    super.onResume()

    worklet!!.resume()
  }

  public override fun onDestroy() {
    super.onDestroy()

    worklet!!.terminate()
    worklet = null
  }
}
