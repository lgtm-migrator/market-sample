package one.credify.marketsample.bnplandroid.customView

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import one.credify.marketsample.bnplandroid.R

class SpreadTextView : BaseView {
    private lateinit var tvTitle: TextView
    private lateinit var tvValue: TextView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override val layoutId: Int
        get() {
            return R.layout.view_spread_text
        }

    override fun onCreate(view: View, attrs: AttributeSet?) {
        tvTitle = view.findViewById(R.id.tvTitle)
        tvValue = view.findViewById(R.id.tvValue)
    }

    fun bind(title: String, value: String) {
        tvTitle.text = title
        tvValue.text = value
    }

    fun setTextSize(textSize: Float) {
        tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
        tvValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
    }

    fun setTitleTextColor(@ColorInt color: Int) {
        setTextColor(tvTitle, color)
    }

    fun setValueTextColor(@ColorInt color: Int) {
        setTextColor(tvValue, color)
    }

    fun setTitleTypeface(typeface: Typeface?) {
        setTypeface(tvTitle, typeface)
    }

    fun setValueTypeface(typeface: Typeface?) {
        setTypeface(tvValue, typeface)
    }

    private fun setTypeface(tv: TextView, typeface: Typeface?) {
        tv.typeface = typeface
    }

    private fun setTextColor(tv: TextView, @ColorInt color: Int) {
        tv.setTextColor(color)
    }
}