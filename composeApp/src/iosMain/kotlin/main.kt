import androidx.compose.ui.window.ComposeUIViewController
import com.igris.draw.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
