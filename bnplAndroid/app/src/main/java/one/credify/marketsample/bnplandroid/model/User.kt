package one.credify.marketsample.bnplandroid.model

import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("id")
    lateinit var id: String

    @SerializedName("firstName")
    lateinit var firstName: String

    @SerializedName("lastName")
    lateinit var lastName: String

    @SerializedName("email")
    lateinit var email: String

    @SerializedName("phoneNumber")
    lateinit var phoneNumber: String

    @SerializedName("phoneCountryCode")
    lateinit var phoneCountryCode: String

    @SerializedName("address")
    lateinit var address: String

    @SerializedName("credifyId")
    var credifyId: String? = null

    @SerializedName("tier")
    var tier: String? = null

    @SerializedName("loyaltyPoint")
    var totalPoint: String? = null
}