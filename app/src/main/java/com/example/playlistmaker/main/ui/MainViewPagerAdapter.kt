package com.example.playlistmaker.main.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.playlist.ui.MediaLibraryFragment
import com.example.playlistmaker.search.ui.SearchFragment
import com.example.playlistmaker.settings.ui.SettingsFragment

class MainViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchFragment.newInstance()
            1 -> MediaLibraryFragment.newInstance()
            2 -> SettingsFragment.newInstance()
            else -> SearchFragment.newInstance()
        }
    }
}
