package com.example.talk.commons

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.talk.R
import dagger.android.support.DaggerAppCompatActivity

abstract class TalkBaseActivity<B: ViewDataBinding>: DaggerAppCompatActivity() {
    companion object {
        private const val TAG = "BaseActivity"
    }

    lateinit var progressBar: ProgressBar
    lateinit var container: FrameLayout
    lateinit var toolbar: Toolbar
    lateinit var screenTitle: TextView
    lateinit var backButton: ImageButton
    lateinit var dataBinding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val baseBinding: B = DataBindingUtil.setContentView(
            this,
            R.layout.activity_base_talk
        )

        val coordinatorLayout = baseBinding.root
        progressBar = coordinatorLayout.findViewById(R.id.progressBar) as ProgressBar
        container = coordinatorLayout.findViewById(R.id.layout_container) as FrameLayout
        toolbar = coordinatorLayout.findViewById(R.id.toolbar) as Toolbar
        screenTitle = toolbar.findViewById(R.id.text_screen_title) as TextView
        backButton = toolbar.findViewById(R.id.image_back_button) as ImageButton

        onScreenBackButtonClick()
    }

    /**
     * This method finishes the current active
     */
    private fun onScreenBackButtonClick() {
        backButton.setOnClickListener {
            finish()
        }
    }

    /**
     * This methods is used for data binding
     */
    protected fun bindView(layoutId: Int): B {
        dataBinding = DataBindingUtil.inflate(layoutInflater,layoutId,container,true)
        return dataBinding
    }

    /**
     * This method hides back button
     */
    protected fun hideBackButton() {
        backButton.visibility = View.GONE
    }

    /**
     * This method shows backButton
     */
    protected fun showBackButton() {
        backButton.visibility = View.VISIBLE
    }

    /**
     * This method shows screen title
     * @param resId : resource id
     */
    protected fun setScreenTitle(resId: Int) {
        screenTitle.setText(resId)
    }

    /**
     * This method show progress dialog
     */
    protected fun showProgressDialog() {
        progressBar.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    /**
     * This method dismiss progress dialog
     */
    protected fun dismissProgressDialog() {
        progressBar.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    /**
     * This method hides action bar
     */
    protected fun hideActionBar() {
        toolbar.visibility = View.GONE
    }

    /**
     * This method shows action bar
     */
    protected fun showActionBar() {
        toolbar.visibility = View.VISIBLE
    }

    /**
     * This method is used to make full screen
     */
    fun makeFullScreen() {
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack();
        } else {
            if (isTaskRoot) {
                showExitConfirmationScreen()
            } else {
                super.onBackPressed()
            }

        }
    }

    /**
     * This method shows log out dialog
     */
    private fun showExitConfirmationScreen() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.app_logout_dialog_message))
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.app_logout_dialog_positive_button)
            ) { _, _ ->
                finish()
            }
            .setNegativeButton(
                getString(R.string.app_logout_dialog_negative_button)
            ) { dialog, _ ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }
}