package com.example.talk.signup.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.talk.R
import com.example.talk.commons.Event
import com.example.talk.commons.util.FirebaseNodes
import com.example.talk.commons.TalkBaseActivity
import com.example.talk.commons.di.ViewModelProviderFactory
import com.example.talk.databinding.ActivitySingUpBinding
import com.example.talk.signup.viewmodel.SignUpViewEvents
import com.example.talk.signup.viewmodel.SignUpViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class SingUpActivity : TalkBaseActivity<ActivitySingUpBinding>() {

    private lateinit var binding: ActivitySingUpBinding
    private var firebaseUser: FirebaseUser? = null
    private var dataBaseReference: DatabaseReference? = null
    private var localImageUri: Uri? = null
    private lateinit var fileStorage: StorageReference

    @Inject
    lateinit var viewModelFactory: ViewModelProviderFactory
    private lateinit var signUpViewModel: SignUpViewModel

    companion object {
        private const val PICK_IMAGE = 100
        private const val PICK_IMAGE_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindView(R.layout.activity_sing_up)
        signUpViewModel = ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel::class.java)

        with(binding) {
            lifecycleOwner = this@SingUpActivity
            singUpActivity = this@SingUpActivity
            viewModel = signUpViewModel
        }

        fileStorage = FirebaseStorage.getInstance().reference
        binding.signUpImageView.setOnClickListener {
            pickProfileImage()
        }

        observeViewEvents()
        observeViewState()
    }

    fun performUserSignUp() {
        with(binding) {
            signUpViewModel.performUserSignUp(
                emailTextView.text?.trim().toString(),
                passwordTextView.text?.trim().toString(),
                confirmPasswordTextView.text?.trim().toString(),
                nameTextView.text?.trim().toString()
            )
        }
    }

    private fun observeViewEvents() {
        signUpViewModel.viewEvents.observe(this, Observer {
            handViewEvents(it)
        })
    }

    private fun observeViewState() {
        signUpViewModel.viewState.observe(this, Observer {
            // Do something on observing view state
        })
    }

    private fun handViewEvents(events: Event<SignUpViewEvents>) {
        events.getContentIfNotHandled()?.let {
            when (it) {
                is SignUpViewEvents.SignUpValidation -> {
                    with(binding) {
                        when {
                            it.isEmailEmpty -> {
                                emailTextView.error = getString(R.string.enter_email_hint)
                            }
                            it.isConfirmPasswordEmpty -> {
                                confirmPasswordTextView.error =
                                    getString(R.string.confirm_password_hint)
                            }
                            it.isEmailNotValid -> {
                                emailTextView.error = getString(R.string.invalid_email_error)
                            }
                            it.isNameEmpty -> {
                                nameTextView.error = getString(R.string.enter_name_hint)
                            }
                            it.isPasswordEmpty -> {
                                passwordTextView.error = getString(R.string.enter_password_hint)
                            }
                            it.isPasswordMismatch -> {
                                confirmPasswordTextView.error =
                                    getString(R.string.password_mismatch_error)
                            }
                        }
                    }
                }
                is SignUpViewEvents.SignUpStatus -> {
                    if (it.isSignUpSuccessFull) {
                        //finish()
                        Toast.makeText(this,"SignUpSuccessful", Toast.LENGTH_LONG).show()
                    }else {
                        Toast.makeText(this,"SignUpFailed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

//    private fun updateNameOnly() {
//        val request = UserProfileChangeRequest.Builder()
//            .setDisplayName(binding.nameTextView.text?.trim().toString())
//            .build()
//
//        firebaseUser?.updateProfile(request)?.addOnCompleteListener {
//            if (it.isSuccessful) {
//                val userId = firebaseUser?.uid
//                dataBaseReference =
//                    FirebaseDatabase.getInstance().reference.child(FirebaseNodes.users)
//                val userData = createUser(null)
//
//                userId?.let { it1 ->
//                    dataBaseReference?.child(it1)?.setValue(userData)
//                        ?.addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                Toast.makeText(
//                                    this@SingUpActivity,
//                                    getString(R.string.sign_up_successfully),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                finish()
//                            } else {
//                                Toast.makeText(
//                                    this@SingUpActivity,
//                                    getString(R.string.sign_up_failed),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                }
//            } else {
//                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    private fun createUser(imageUri: Uri?): HashMap<String, String> {
        val userHashMap = HashMap<String, String>()
        userHashMap[FirebaseNodes.name] = binding.nameTextView.text?.trim().toString()
        userHashMap[FirebaseNodes.email] = binding.emailTextView.text?.trim().toString()
        userHashMap[FirebaseNodes.photo] = imageUri?.path ?: ""
        userHashMap[FirebaseNodes.online] = "true"
        return userHashMap
    }

    private fun pickProfileImage() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PICK_IMAGE_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PICK_IMAGE_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        } else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            binding.signUpImageView.setImageURI(imageUri)
            localImageUri = imageUri
        }
    }

    private fun updateNameAndPhoto(imageUri: Uri) {
        val fileName = "${firebaseUser?.uid}.jpg"
        val fileReference = fileStorage.child("images/${fileName}")
        fileReference.putFile(imageUri).addOnCompleteListener {
            if (it.isSuccessful) {
                fileReference.downloadUrl.addOnSuccessListener { server_image_uri ->
                    val request = UserProfileChangeRequest.Builder()
                        .setDisplayName(binding.nameTextView.text?.trim().toString())
                        .setPhotoUri(server_image_uri)
                        .build()

                    firebaseUser?.updateProfile(request)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val userId = firebaseUser?.uid
                            dataBaseReference =
                                FirebaseDatabase.getInstance().reference.child(FirebaseNodes.users)
                            val userData = createUser(server_image_uri)

                            userId?.let { it1 ->
                                dataBaseReference?.child(it1)?.setValue(userData)
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this@SingUpActivity,
                                                getString(R.string.sign_up_successfully),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@SingUpActivity,
                                                getString(R.string.sign_up_failed),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
