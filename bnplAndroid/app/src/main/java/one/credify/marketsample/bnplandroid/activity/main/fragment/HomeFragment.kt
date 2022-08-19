package one.credify.marketsample.bnplandroid.activity.main.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import one.credify.marketsample.bnplandroid.base.BaseFragment
import one.credify.marketsample.bnplandroid.databinding.FragmentHomeBinding
import one.credify.marketsample.bnplandroid.model.*
import one.credify.marketsample.bnplandroid.requester.UserRequester
import one.credify.sdk.CredifySDK
import one.credify.sdk.core.model.*

class HomeFragment : BaseFragment() {
    private val mTag = "HomeFragment"

    private var mBinding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        UserRequester().getUserProfile(
            context = view.context,
            Constant.DEMO_USER_API_URL,
            onRequest = {
                showLoading()
            },
            onResult = onResult@{ isSuccess, userProfile, user ->
                if (isSuccess && userProfile != null) {
                    AppState.instance.userProfile = userProfile
                    AppState.instance.user = user

                    getBNPLAvailability(user = userProfile)
                    return@onResult
                }

                hideLoading()
            }
        )
    }

    private fun initView() {
        mBinding?.run {
            btnCheckout.setOnClickListener {
                val user = AppState.instance.userProfile
                if (user == null) {
                    Toast.makeText(context, "User profile is not loaded yet", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                createOrder { orderInfo ->
                    Log.i(mTag, "Order id: ${orderInfo?.orderId}")

                    if (orderInfo != null) {
                        startBNPL(
                            context = it.context,
                            user = user,
                            orderInfo = orderInfo,
                        )
                    }
                }
            }
        }
    }

    private fun createOrderObject(): Order {
        val paymentRecipient = PaymentRecipient(
            name = "bank_name",
            number = "454545454545",
            branch = "bank_branch",
            bank = "bank_bank"
        )

        val orderLines = listOf(
            OrderLine(
                name = "Diane 35 - Bayer (H/21v)",
                referenceId = "reference_id_1",
                imageUrl = "https://apharma.vn/wp-content/uploads/Diane-35.png",
                productUrl = "https://apharma.vn/wp-content/uploads/Diane-35.png",
                quantity = 40,
                unitPrice = TotalAmount(value = "115000", currency = CurrencyType.VND),
                subtotal = TotalAmount(value = "${115000 * 40}", currency = CurrencyType.VND),
                measurementUnit = "EA"
            ),
            OrderLine(
                name = "Marvelon Bayer (H/21v)",
                referenceId = "reference_id_2",
                imageUrl = "https://images.fpt.shop/unsafe/fit-in/600x600/filters:quality(90):fill(white)/nhathuoclongchau.com/images/product/2021/05/00004687-marvelon-h3-vi-7225-60ad_large.jpg",
                productUrl = "https://images.fpt.shop/unsafe/fit-in/600x600/filters:quality(90):fill(white)/nhathuoclongchau.com/images/product/2021/05/00004687-marvelon-h3-vi-7225-60ad_large.jpg",
                quantity = 60,
                unitPrice = TotalAmount(value = "63100", currency = CurrencyType.VND),
                subtotal = TotalAmount(value = "${63100 * 60}", currency = CurrencyType.VND),
                measurementUnit = "EA"
            ),
        )

        val totalAmount = TotalAmount(
            value = orderLines.sumBy { it.subtotal.value.toInt() }.toString(),
            currency = CurrencyType.VND
        )

        return Order(
            referenceId = "reference_id_${System.currentTimeMillis()}",
            totalAmount = totalAmount,
            orderLines = orderLines,
            paymentRecipient = paymentRecipient,
            userId = AppState.instance.userProfile?.id ?: "",
        )
    }

    private fun createOrder(onResult: (orderInfo: OrderInfo?) -> Unit) {
        showLoading()

        val order = createOrderObject()
        val queue = Volley.newRequestQueue(context)

        val stringRequest = object : StringRequest(
            Method.POST,
            Constant.CREATE_ORDER_URL,
            { str ->
                val jsonObject = Gson().fromJson(str, JsonObject::class.java)
                onResult(
                    OrderInfo(
                        orderId = jsonObject.get("id").asString,
                        orderAmount = FiatCurrency(
                            value = order.totalAmount.value,
                            currency = order.totalAmount.currency
                        )
                    )
                )
                hideLoading()
            },
            { error ->
                error.printStackTrace()

                // error
                onResult(null)
                hideLoading()
            }
        ) {
            override fun getBody(): ByteArray {
                val body = Gson().toJson(order)
                return body.toByteArray(Charsets.UTF_8)
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    private fun startBNPL(context: Context, user: UserProfile, orderInfo: OrderInfo) {
        CredifySDK.instance.bnplApi.showBNPL(
            context = context,
            userProfile = user,
            orderInfo = orderInfo,
            pushClaimCallback = object : CredifySDK.PushClaimCallback {
                override fun onPushClaim(
                    credifyId: String,
                    resultCallback: CredifySDK.PushClaimResultCallback
                ) {
                    UserRequester().pushClaims(
                        context = context,
                        useId = user.id,
                        credifyId = credifyId
                    ) { isSuccess ->
                        resultCallback.onPushClaimResult(isSuccess)
                    }
                }
            },
            bnplPageCallback = object : CredifySDK.BNPLPageCallback {
                override fun onClose(
                    status: RedemptionResult,
                    orderId: String,
                    isPaymentCompleted: Boolean
                ) {
                    Log.i(
                        mTag,
                        "Status: $status, order id: $orderId, payment completed: $isPaymentCompleted"
                    )
                }
            }
        )
    }

    private fun getBNPLAvailability(user: UserProfile) {
        showLoading()

        val disposable = CredifySDK.instance.bnplApi.getBNPLAvailability(userProfile = user)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { bnplInfo ->
                    hideLoading()

                    val isAvailable = bnplInfo.isAvailable
                    if (isAvailable) {
                        mBinding?.btnCheckout?.isEnabled = isAvailable
                    }

                    Log.i(mTag, "BNPL is available: $isAvailable")
                },
                {
                    // Errors
                    hideLoading()
                    mBinding?.btnCheckout?.isEnabled = false
                }
            )
        mCompositeDisposable.add(disposable)
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}