package one.credify.marketsample.bnplandroid.activity.main.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import one.credify.marketsample.bnplandroid.databinding.FragmentMyPageBinding
import one.credify.marketsample.bnplandroid.model.AppState
import one.credify.marketsample.bnplandroid.model.Constant
import one.credify.marketsample.bnplandroid.model.User
import one.credify.marketsample.bnplandroid.requester.UserRequester
import one.credify.sdk.CredifySDK
import one.credify.sdk.core.model.ProductType

class MyPageFragment : Fragment() {
    private val mTag = "MyPageFragment"

    private var mBinding: FragmentMyPageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMyPageBinding.inflate(inflater, container, false)
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        mBinding?.run {
            bindingUserProfile(AppState.instance.user)

            btnCredifyAccountPage.setOnClickListener {
                openCredifyAccountPage(context = it.context)
            }

            btnBNPLDetails.setOnClickListener {
                openBNPLDetailsPage(context = it.context)
            }
        }
    }

    private fun bindingUserProfile(user: User?) {
        if (user == null) return

        mBinding?.run {
            stvID.bind("ID", user.id)
            stvFirstName.bind("First name", user.firstName)
            stvLastName.bind("Last name", user.lastName)
            stvEmail.bind("Email", user.email)
            stvPhoneNumber.bind("Phone number", "${user.phoneCountryCode} ${user.phoneNumber}")
            stvAddress.bind("Address", user.address)
            stvTier.bind("Tier", user.tier ?: "")
            stvTotalPoint.bind("Total point", user.totalPoint ?: "")
            stvCredifyId.bind("Credify ID", user.credifyId ?: "")
        }
    }

    private fun openBNPLDetailsPage(context: Context) {
        AppState.instance.userProfile?.run {
            CredifySDK.instance.passportApi.showServiceInstance(
                context = context,
                userProfile = this,
                productTypeList = listOf(ProductType.BNPL_CONSUMER),
                callback = object : CredifySDK.PageCallback {
                    override fun onClose() {
                        Log.i(mTag, "BNPL details page showed")
                    }

                    override fun onShow() {
                        Log.i(mTag, "BNPL details page closed")
                    }
                },
            )
        }
    }

    private fun openCredifyAccountPage(context: Context) {
        AppState.instance.userProfile?.run {
            CredifySDK.instance.passportApi.showPassport(
                context = context,
                userProfile = this,
                pushClaimCallback = object : CredifySDK.PushClaimCallback {
                    override fun onPushClaim(
                        credifyId: String,
                        resultCallback: CredifySDK.PushClaimResultCallback
                    ) {
                        UserRequester().pushClaims(
                            context = context,
                            useId = id,
                            credifyId = credifyId
                        ) { isSuccess ->
                            resultCallback.onPushClaimResult(isSuccess)
                        }
                    }
                },
                callback = object : CredifySDK.PassportPageCallback {
                    override fun onClose() {
                        Log.i(mTag, "Account page showed")
                    }

                    override fun onShow() {
                        Log.i(mTag, "Account page closed")
                    }
                }
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyPageFragment()
    }
}