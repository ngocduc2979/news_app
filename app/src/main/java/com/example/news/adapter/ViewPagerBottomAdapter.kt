package com.example.news.adapter

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.news.Category
import com.example.news.News
import com.example.news.fragment.BottomFavoriteFragment
import com.example.news.fragment.BottomHomeFragment
import com.example.news.fragment.NewsFragment
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

public class ViewPagerBottomAdapter(fm: FragmentManager, var context: Context) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {

        if (position == 0) {
            return BottomHomeFragment()
        } else {
            return BottomFavoriteFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {

        if (position == 0) {
            return "Trang chủ"
        } else {
            return "Yêu thích"
        }
    }
}