package one.credify.marketsample.bnplandroid.base

import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable

open class BaseFragment : Fragment() {
    protected val mCompositeDisposable = CompositeDisposable()

    override fun onDestroyView() {
        super.onDestroyView()

        mCompositeDisposable.dispose()
    }

    protected fun showLoading() {
        val act = activity
        if (act is BaseActivity && !act.isFinishing) {
            act.showLoading()
        }
    }

    protected fun hideLoading() {
        val act = activity
        if (act is BaseActivity && !act.isFinishing) {
            act.hideLoading()
        }
    }
}