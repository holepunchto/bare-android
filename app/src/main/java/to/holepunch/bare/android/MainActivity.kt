package to.holepunch.bare.android

import android.app.Activity
import android.os.Bundle
import to.holepunch.bare.kit.Worklet

class MainActivity : Activity() {
  var worklet: Worklet? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

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
