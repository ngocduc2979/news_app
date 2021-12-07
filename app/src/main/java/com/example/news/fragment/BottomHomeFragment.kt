package com.example.news.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.news.AppConfig
import com.example.news.Category
import com.example.news.NewsPage
import com.example.news.R
import com.example.news.activity.SearchNewsActivity
import com.example.news.adapter.NewsPageAdapter
import com.example.news.adapter.ViewPagerTablayoutAdapter
import com.example.news.listener.NewsPageListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_bottom_home.*
import kotlinx.android.synthetic.main.fragment_bottom_home.view.*

class BottomHomeFragment : Fragment(R.layout.fragment_bottom_home), NewsPageListener{

    private val listTopTab: ArrayList<Category> = ArrayList()
    private val listNewsPage: ArrayList<NewsPage> = ArrayList()

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var imbSearch: ImageView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var menu: ImageView
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        return inflater.inflate(R.layout.fragment_bottom_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager   = view.findViewById(R.id.viewPager_bottom)
        tabLayout   = view.findViewById(R.id.tabLayout)
        imbSearch   = view.findViewById(R.id.imSearch)
        menu        = view.findViewById(R.id.imMenu)
        toolbar     = view.findViewById(R.id.toolbar_navigation)
        drawer      = view.findViewById(R.id.drawer)
        navigationView   = view.findViewById(R.id.navigation_view)

        getListTab()
        setListPage()
        setAdapter()
        setAdapterNavigation()

    }

    @SuppressLint("UseRequireInsteadOfGet")
    fun getListTab() {
        listTopTab.clear()
        if (AppConfig.getInstance(context!!).getNewsPage().equals("VN Express")) {
            listTopTab.add(Category("https://vnexpress.net/rss/the-gioi.rss", "Thế giới"))
            listTopTab.add(Category("https://vnexpress.net/rss/thoi-su.rss", "Thời sự"))
            listTopTab.add(Category("https://vnexpress.net/rss/kinh-doanh.rss", "Kinh doanh"))
            listTopTab.add(Category("https://vnexpress.net/rss/giai-tri.rss", "Giải trí"))
            listTopTab.add(Category("https://vnexpress.net/rss/the-thao.rss", "Thể thao"))
        } else if (AppConfig.getInstance(context!!).getNewsPage().equals("24h")) {
            listTopTab.add(Category("https://cdn.24h.com.vn/upload/rss/tintuctrongngay.rss", "Trang chủ"))
            listTopTab.add(Category("https://cdn.24h.com.vn/upload/rss/bongda.rss", "Bóng đá"))
            listTopTab.add(Category("https://cdn.24h.com.vn/upload/rss/thoitrang.rss", "Thời trang"))
            listTopTab.add(Category("https://cdn.24h.com.vn/upload/rss/phim.rss", "Phim"))
        } else if (AppConfig.getInstance(context!!).getNewsPage().equals("Tuổi trẻ online")) {
            listTopTab.add(Category("https://tuoitre.vn/rss/tin-moi-nhat.rss", "Trang chủ"))
            listTopTab.add(Category("https://tuoitre.vn/rss/khoa-hoc.rss", "Khoa học"))
            listTopTab.add(Category("https://tuoitre.vn/rss/xe.rss", "Xe"))
            listTopTab.add(Category("https://tuoitre.vn/rss/du-lich.rss", "Du lịch"))
            listTopTab.add(Category("https://tuoitre.vn/rss/nhip-song-tre.rss", "Nhịp sống trẻ"))
            listTopTab.add(Category("https://tuoitre.vn/rss/nhip-song-so.rss", "Công nghệ"))
        }
    }

    fun setListPage() {
        listNewsPage.clear()
        listNewsPage.add(NewsPage("24h", R.drawable.bao_24h))
        listNewsPage.add(NewsPage("VN Express", R.drawable.vnxpress))
        listNewsPage.add(NewsPage("Tuổi trẻ online", R.drawable.tto_default_avatar))

        for (i in 0..(listNewsPage.size-1)) {
            if (AppConfig.getInstance(requireContext()).getNewsPage().equals(listNewsPage.get(i).name)) {
                listNewsPage.remove(listNewsPage.get(i))
                break
            }
        }
    }

    fun setAdapterNavigation() {
        var newsPageAdapter = NewsPageAdapter(listNewsPage, requireContext())
        newsPageAdapter.setOnNewsPageListener(this)
        recycle_naavigation.adapter = newsPageAdapter
        recycle_naavigation.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recycle_naavigation.adapter!!.notifyDataSetChanged()
    }

    fun setAdapter() {
        var viewPagerTablayoutAdapter = ViewPagerTablayoutAdapter(requireActivity().supportFragmentManager, requireContext())
        listTopTab.forEach {
            viewPagerTablayoutAdapter.addFragment(NewsFragment.newInstance(it), it)
        }

        viewPager.adapter = viewPagerTablayoutAdapter
//        viewPager.offscreenPageLimit = listTopTab.size
        tabLayout.setupWithViewPager(viewPager)

        imbSearch.setOnClickListener {
            SearchNewsActivity.launch(requireContext(), listTopTab)
        }

        menu.setOnClickListener {
            drawer.openDrawer(navigationView)
        }

        toolbar.setOnClickListener {
            drawer.closeDrawer(navigationView)
        }
    }

    override fun setOnNewPage(context: Context) {
        drawer.closeDrawer(navigationView)
        getListTab()
        setListPage()
        setAdapter()
        setAdapterNavigation()
    }
}