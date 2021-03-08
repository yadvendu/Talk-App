package com.example.talk.signup.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.talk.R
import com.example.talk.commons.util.FirebaseNodes
import com.example.talk.commons.TalkBaseActivity
import com.example.talk.databinding.ActivitySingUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SingUpActivity : TalkBaseActivity<ActivitySingUpBinding>() {

    private lateinit var binding: ActivitySingUpBinding
    private var firebaseUser: FirebaseUser? = null
    private var dataBaseReference: DatabaseReference? = null
    private var localImageUri: Uri? = null
    private lateinit var fileStorage: StorageReference

    companion object {
        private const val PICK_IMAGE = 100
        private const val PICK_IMAGE_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindView(R.layout.activity_sing_up)
        with(binding) {
            lifecycleOwner = this@SingUpActivity
            singUpActivity = this@SingUpActivity
        }

        fileStorage = FirebaseStorage.getInstance().reference
        binding.signUpImageView.setOnClickListener {
            pickProfileImage()
        }
    }

    /**
     * This method is used for sign up validation
     */
    fun doSingUpValidation() {
        with(binding) {
            when {
                emailTextView.text?.trim()?.isEmpty() == true -> {
                    emailTextView.error = getString(R.string.enter_email_hint)
                }
                passwordTextView.text?.trim()?.isEmpty() == true -> {
                    passwordTextView.error = getString(R.string.enter_password_hint)
                }
                confirmPasswordTextView.text?.trim()?.isEmpty() == true -> {
                    confirmPasswordTextView.error = getString(R.string.confirm_password_hint)
                }
                passwordTextView.text?.trim().toString() != confirmPasswordTextView.text?.trim()
                    .toString() -> {
                    confirmPasswordTextView.error = getString(R.string.password_mismatch_error)
                }
                nameTextView.text?.trim()?.isEmpty() == true -> {
                    nameTextView.error = getString(R.string.enter_name_hint)
                }
                else -> {
                    val email = emailTextView.text?.trim().toString()
                    val password = passwordTextView.text?.trim().toString()
                    val firebaseAuth = FirebaseAuth.getInstance()

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailTextView.error = getString(R.string.invalid_email_error)
                    }

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                firebaseUser = firebaseAuth.currentUser
                                localImageUri?.let { imageUri ->
                                    updateNameAndPhoto(imageUri)
                                } ?: updateNameOnly()
                                updateNameOnly()
                            } else {
                                Toast.makeText(
                                    this@SingUpActivity,
                                    getString(R.string.sign_up_failed),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }

    private fun updateNameOnly() {
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(binding.nameTextView.text?.trim().toString())
            .build()

        firebaseUser?.updateProfile(request)?.addOnCompleteListener {
            if (it.isSuccessful) {
                val userId = firebaseUser?.uid
                dataBaseReference =
                    FirebaseDatabase.getInstance().reference.child(FirebaseNodes.users)
                val userData = createUser(null)

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
