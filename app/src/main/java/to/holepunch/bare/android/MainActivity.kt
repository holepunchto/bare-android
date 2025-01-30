package to.holepunch.bare.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import to.holepunch.bare.kit.Worklet

class MainActivity : ComponentActivity() {
  var worklet: Worklet? = null

  public override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    worklet = Worklet(null)

    try {
      worklet!!.start("/app.bundle", assets.open("app.bundle"), null)
    } catch (e: Exception) {
      throw RuntimeException(e)
    }

    setContent { 
      Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
      ) {
            Text(text = "Hello World")
        }
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
