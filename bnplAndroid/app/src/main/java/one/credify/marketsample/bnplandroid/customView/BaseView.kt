package one.credify.marketsample.bnplandroid.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

abstract class BaseView : ConstraintLayout {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        createView(context, attrs)
    }

    private fun createView(context: Context, attrs: AttributeSet?) {
        val view = LayoutInflater.from(context).inflate(layoutId, this, true)
        onCreate(view, attrs)
    }

    abstract val layoutId: Int

    abstract fun onCreate(view: View, attrs: AttributeSet?)
}