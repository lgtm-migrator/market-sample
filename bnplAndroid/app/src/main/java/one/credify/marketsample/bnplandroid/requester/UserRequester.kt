package one.credify.marketsample.bnplandroid.requester

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import one.credify.marketsample.bnplandroid.model.Constant
import one.credify.marketsample.bnplandroid.model.User
import one.credify.sdk.core.model.*
import org.json.JSONObject

class UserRequester {
    fun getUserProfile(
        context: Context,
        url: String,
        onRequest: () -> Unit,
        onResult: (isSuccess: Boolean, userProfile: UserProfile?, user: User?) -> Unit
    ) {
        onRequest()

        Log.d("UserRequester", "Url: $url")

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)

        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val gson = Gson()
                val user = gson.fromJson(response, User::class.java)

                val userProfile = UserProfile(
                    id = user.id,
                    name = UserName(
                        firstName = user.firstName,
                        lastName = user.lastName,
                        middleName = null,
                        fullName = ""
                    ),
                    phone = UserPhoneNumber(
                        phoneNumber = user.phoneNumber,
                        countryCode = user.phoneCountryCode
                    ),
                    email = user.email,
                    dob = null,
                    address = null,
                    credifyId = user.credifyId
                )

                onResult(true, userProfile, user)
            },
            {
                onResult(false, null, null)
            }
        )

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun pushClaims(
        context: Context,
        useId: String,
        credifyId: String,
        onResult: (isSuccess: Boolean) -> Unit,
    ) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)

        // Request a string response from the provided URL.
        val stringRequest = object : JsonObjectRequest(
            Method.POST,
            Constant.PUSH_CLAIMS_API_URL,
            JSONObject().apply {
                put("id", useId)
                put("credify_id", credifyId)
            },
            { _ ->
                onResult(true)
            },
            {
                // Error
                onResult(false)
            }
        ) {}

        queue.add(stringRequest)
    }
}