package yellow7918.ajou.ac.aunager

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TimeUnit.SECONDS.sleep(10)

        Snackbar.make(constraint_layout, "SIGN OUT", Snackbar.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()

        startActivity(Intent(this@MainActivity, SignInActivity::class.java))
    }
}