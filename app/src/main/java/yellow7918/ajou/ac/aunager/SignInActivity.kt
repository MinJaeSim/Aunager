package yellow7918.ajou.ac.aunager

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val auth: FirebaseAuth = FirebaseAuth.getInstance()


        auth.currentUser?.let {
            autoLogin()
        }


        button_sign_up.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
        }

        button_sign_in.setOnClickListener { v ->
            button_sign_in.visibility = View.GONE
            val email = email_edit.text.toString()
            val password = password_edit.text.toString()

            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                finish()
            }.addOnFailureListener {
                Snackbar.make(v, it.localizedMessage, Snackbar.LENGTH_SHORT).show()
                button_sign_in.visibility = View.VISIBLE
            }
        }
    }

    private fun autoLogin(): Unit {
        Toast.makeText(this@SignInActivity, "Hello", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
        finish()
    }
}

/*
private fun FirebaseUser.autoLogin(activity: AppCompatActivity): Unit {
    Toast.makeText(activity, "Hello", Toast.LENGTH_SHORT).show()
    activity.startActivity(Intent(activity, MainActivity::class.java))
    activity.finish()
}
*/