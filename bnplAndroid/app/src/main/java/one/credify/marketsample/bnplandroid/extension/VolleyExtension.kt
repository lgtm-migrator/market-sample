package one.credify.marketsample.bnplandroid.extension

import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest

fun StringRequest.updateTimeout(timeoutMs: Int = 30000) {
    retryPolicy = DefaultRetryPolicy(
        timeoutMs,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    )
}

fun JsonObjectRequest.updateTimeout(timeoutMs: Int = 30000) {
    retryPolicy = DefaultRetryPolicy(
        timeoutMs,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
    )
}