package com.example.moodymons.account

import android.app.Activity.RESULT_OK
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.moodymons.R
import com.example.moodymons.databinding.FragmentSignInBinding
import com.example.moodymons.page.daily.SERViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@RequiresApi(Build.VERSION_CODES.O)
class SignInFragment: Fragment() {
    private var _binding : FragmentSignInBinding?=null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val viewModel: SERViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        auth = Firebase.auth
//        Log.i("SignInFragment", "on create view")

        val signInButton: SignInButton = binding.signInButton
        signInButton.setSize(SignInButton.SIZE_WIDE)

        signInButton.setOnClickListener {
//            Log.i("SignInFragment", "button click")
            createSignInIntent()
        }
        reDesignGoogleButton(binding.signInButton)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
//        Log.i("SignInFragment", "on start sign in")
        if (currentUser != null) {
            updateUI(currentUser)
        }
    }

    //Easy
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun createSignInIntent() {
//        Log.i("SignInFragment", "create intent")
        // Choose authentication providers
        val providers = arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
//        Log.i("SignInFragment", "on sign result")
        //val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                updateUI(user)
            }
        } else {
            Toast.makeText(requireActivity(), "登入失敗，再試一次", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateUI(user: FirebaseUser) {
        val name: String? = user.displayName
        Toast.makeText(requireActivity(), "$name 歡迎來到 moodyMons", Toast.LENGTH_SHORT).show()

        viewModel.setUser(user)
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(R.id.action_signInFragment_to_viewPagerFragment)
    }

    fun reDesignGoogleButton(signInButton: SignInButton) {
        for (i in 0 until signInButton.childCount) {
            val v = signInButton.getChildAt(i)
            if (v is TextView) {
                //v.text = buttonText //setup your text here
                v.setBackgroundResource(R.drawable.ic_sign_in_button) //setting transparent
                // color
                // that
                // will hide
                // google image and white background
                v.setTextColor(resources.getColor(android.R.color.transparent)) // text color here
                v.typeface = Typeface.DEFAULT_BOLD // even typeface
                return
            }
        }
    }
}
