package com.example.news.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.example.news.*
import com.example.news.adapter.NewsPageAdapter
import com.example.news.adapter.ViewPagerBottomAdapter
import com.example.news.adapter.ViewPagerTablayoutAdapter
import com.example.news.fragment.NewsFragment
import com.example.news.listener.NewsPageListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NewsPageListener {

    private val listNewsPage: ArrayList<NewsPage> = ArrayList()

//    private val listTopTab = arrayListOf<Category>(
//        Category("https://vnexpress.net/rss/tin-moi-nhat.rss", "Trang chủ"),
//        Category("https://vnexpress.net/rss/the-gioi.rss", "Thế giới"),
//        Category("https://vnexpress.net/rss/thoi-su.rss", "Thời sự"),
//        Category("https://vnexpress.net/rss/kinh-doanh.rss", "Kinh doanh"),
//        Category("https://vnexpress.net/rss/giai-tri.rss", "Giải trí"),
//        Category("https://vnexpress.net/rss/the-thao.rss", "Thể thao")
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListPage()
        setBottomAdapter()

    }

    fun setListPage() {
        listNewsPage.clear()
        listNewsPage.add(NewsPage("24h", R.drawable.bao_24h))
        listNewsPage.add(NewsPage("vnexpress", R.drawable.vnxpress))
        listNewsPage.add(NewsPage("Tuổi trẻ online", R.drawable.tto_default_avatar))

        for (i in 0..(listNewsPage.size-1)) {
            if (AppConfig.getInstance(this).getNewsPage().equals(listNewsPage.get(i).name)) {
                listNewsPage.remove(listNewsPage.get(i))
                break
            }
        }
    }

    fun setBottomAdapter() {
        var viewPagerBottomAdapter = ViewPagerBottomAdapter(supportFragmentManager, this)
        viewPager.adapter = viewPagerBottomAdapter
        viewPager.offscreenPageLimit = 2

//        bottom_navigation.setOnNavigationItemReselectedListener {
//            if (it.itemId.equals(R.id.home)) {
//                viewPager.currentItem = 0
//            } else {
//                viewPager.currentItem = 1
//            }
//        }

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.home -> {
                    viewPager.currentItem = 0
                }
                R.id.favorite -> {
                    viewPager.currentItem = 1
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun setOnNewPage(context: Context) {

    }
}