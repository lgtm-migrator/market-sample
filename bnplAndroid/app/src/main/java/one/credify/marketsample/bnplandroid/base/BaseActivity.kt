package one.credify.marketsample.bnplandroid.base

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import one.credify.marketsample.bnplandroid.R

abstract class BaseActivity : AppCompatActivity() {
    protected val mCompositeDisposable = CompositeDisposable()

    private var mDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createDialog()
    }

    override fun onDestroy() {
        super.onDestroy()

        mCompositeDisposable.dispose()
    }

    private fun createDialog() {
        mDialog = Dialog(this).apply {
            setContentView(R.layout.dialog_progressing)
            setCancelable(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    fun showLoading() {
        mDialog?.show()
    }

    fun hideLoading() {
        if (mDialog?.isShowing == true) {
            mDialog?.dismiss()
        }
    }
}