// ...existing code...
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
// ...existing code...
import com.buildmasterapp.user.presentation.AuthNavHost
// ...existing code...
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthNavHost()
        }
    }
}
// ...existing code...

