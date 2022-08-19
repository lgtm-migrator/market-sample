package one.credify.marketsample.bnplandroid.activity.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import one.credify.marketsample.bnplandroid.activity.main.fragment.HomeFragment
import one.credify.marketsample.bnplandroid.activity.main.fragment.MyPageFragment
import one.credify.marketsample.bnplandroid.activity.main.fragment.SearchFragment

class MainAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> HomeFragment.newInstance()
            1 -> SearchFragment.newInstance()
            else -> MyPageFragment.newInstance()
        }
    }
}