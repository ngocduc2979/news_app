package com.example.news.adapter

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.news.Category
import com.example.news.News
import com.example.news.fragment.NewsFragment
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

public class ViewPagerTablayoutAdapter(fm: FragmentManager, var context: Context) : FragmentStatePagerAdapter(fm) {

    companion object var KEY_LINK: String = "link"

    var listFragment : ArrayList<Fragment> = ArrayList()
    var listCategory: ArrayList<Category> = ArrayList()

    fun addFragment (fragment: Fragment, category: Category) {
        listFragment.add(fragment)
        listCategory.add(category)
    }

    override fun getItem(position: Int): Fragment {
        var newsFragment: NewsFragment = NewsFragment()
        var bundle: Bundle = Bundle()
        bundle.putString(KEY_LINK, listCategory.get(position).link)
        newsFragment.arguments = bundle
        Log.wtf("ViewPagerAdapter", " getItem")
        return newsFragment
    }

    override fun getCount(): Int {
        return listFragment.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        Log.wtf("ViewPagerAdapter", " getPageTitle")
        return listCategory.get(position).title
    }
}