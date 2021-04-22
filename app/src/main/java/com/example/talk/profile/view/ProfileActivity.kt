package com.example.talk.profile.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.talk.R
import com.example.talk.commons.TalkBaseActivity
import com.example.talk.commons.util.FirebaseNodes
import com.example.talk.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class ProfileActivity : TalkBaseActivity<ActivityProfileBinding>() {

    @Inject
    @JvmField
    var firebaseUser: FirebaseUser? = null
    private var localImageUri: Uri? = null
    private var dataBaseReference: DatabaseReference? = null
    private lateinit var fileStorage: StorageReference
    private lateinit var binding: ActivityProfileBinding

    companion object {
        private const val PICK_IMAGE = 100
        private const val PICK_IMAGE_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindView(R.layout.activity_profile)
        with(binding) {
            lifecycleOwner = this@ProfileActivity
        }
        fileStorage = FirebaseStorage.getInstance().reference

        if (firebaseUser!= null) {
            with(binding) {
                emailTextView.setText(firebaseUser?.email)
                nameTextView.setText(firebaseUser?.displayName)
                firebaseUser?.photoUrl?.let {
                    Glide.with(this@ProfileActivity)
                        .load(it)
                        .placeholder(R.drawable.default_diplay_picture)
                        .error(R.drawable.default_diplay_picture)
                        .into(signUpImageView)
                }
            }
        }

        binding.signUpButton.setOnClickListener {
            if (binding.nameTextView.text?.trim().toString().isEmpty()) {
                binding.nameTextView.error = getString(R.string.enter_name_hint)
            } else {
                if (localImageUri != null) {
                    localImageUri?.let { updateNameAndPhoto(it) }
                } else {
                    updateNameOnly()
                }
            }
        }

        binding.signUpImageView.setOnClickListener {
            changeImage()
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
                                    this,
                                    getString(R.string.sign_up_successfully),
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
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

    private fun changeImage() {
        if (firebaseUser?.photoUrl == null) {
            pickProfileImage()
        } else {
            val popUpMenu = PopupMenu(this, binding.signUpImageView)
            popUpMenu.menuInflater.inflate(R.menu.menu_picture,popUpMenu.menu)
            popUpMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.change_picture_menu -> {
                        pickProfileImage()
                    }
                    R.id.remove_picture_menu -> {
                        removePhoto()
                    }
                }
                true
            }
            popUpMenu.show()
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
                            val userData = createUserWithPhoto(server_image_uri)

                            userId?.let { it1 ->
                                dataBaseReference?.child(it1)?.setValue(userData)
                                    ?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(
                                                this,
                                                getString(R.string.sign_up_successfully),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this,
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

    private fun createUserWithPhoto(imageUri: Uri?): HashMap<String, String> {
        val userHashMap = HashMap<String, String>()
        userHashMap[FirebaseNodes.name] = binding.nameTextView.text?.trim().toString()
        userHashMap[FirebaseNodes.photo] = imageUri?.path ?: ""
        return userHashMap
    }

    private fun createUser(imageUri: Uri?): HashMap<String, String> {
        val userHashMap = HashMap<String, String>()
        userHashMap[FirebaseNodes.name] = binding.nameTextView.text?.trim().toString()
        return userHashMap
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

    private fun pickProfileImage() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                PICK_IMAGE_PERMISSION_CODE
            )
        }
    }

    private fun removePhoto() {
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(binding.nameTextView.text?.trim().toString())
            .setPhotoUri(null)
            .build()

        firebaseUser?.updateProfile(request)?.addOnCompleteListener {
            if (it.isSuccessful) {
                val userId = firebaseUser?.uid
                dataBaseReference =
                    FirebaseDatabase.getInstance().reference.child(FirebaseNodes.users)
                val userData = createUserWithPhoto(null)

                userId?.let { it1 ->
                    dataBaseReference?.child(it1)?.setValue(userData)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    getString(R.string.sign_up_successfully),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
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
