package one.credify.marketsample.bnplandroid.activity.main

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import one.credify.marketsample.bnplandroid.R
import one.credify.marketsample.bnplandroid.base.BaseActivity
import one.credify.marketsample.bnplandroid.databinding.ActivityMainBinding
import one.credify.sdk.core.model.UserPhoneNumber
import one.credify.sdk.core.model.UserProfile

class MainActivity : BaseActivity() {
    private lateinit var mBinding: ActivityMainBinding

    private var mUser: UserProfile = UserProfile(
        id = "1463", // Your user's id
        name = null,
        phone = UserPhoneNumber( // Your user's phone number
            countryCode = "+84",
            phoneNumber = "374720660"
        ),
        email = null,
        dob = null,
        address = null,
        credifyId = null
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initView()
    }

    private fun initView() {
        mBinding.run {
            vpPager.adapter = MainAdapter(this@MainActivity)
            vpPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> {
                            bnvBottomNav.menu.findItem(R.id.mHome).isChecked = true
                        }
                        1 -> {
                            bnvBottomNav.menu.findItem(R.id.mSearch).isChecked = true
                        }
                        2 -> {
                            bnvBottomNav.menu.findItem(R.id.mMyPage).isChecked = true
                        }
                        else -> {
                            super.onPageSelected(position)
                        }
                    }

                }
            })

            bnvBottomNav.setOnItemSelectedListener { item ->
                return@setOnItemSelectedListener when (item.itemId) {
                    R.id.mHome -> {
                        vpPager.currentItem = 0
                        true
                    }
                    R.id.mSearch -> {
                        vpPager.currentItem = 1
                        true
                    }
                    R.id.mMyPage -> {
                        vpPager.currentItem = 2
                        true
                    }
                    else -> false
                }
            }
        }
    }
}