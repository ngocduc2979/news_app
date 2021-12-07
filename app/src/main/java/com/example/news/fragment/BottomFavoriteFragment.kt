package com.example.news.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.News
import com.example.news.NewsDatabase
import com.example.news.R
import com.example.news.activity.MainActivity
import com.example.news.activity.NewsActivity
import com.example.news.adapter.NewsAdapter
import com.example.news.listener.Listener


class BottomFavoriteFragment : Fragment(), Listener {

    private lateinit var recyclerView: RecyclerView
    private var listFavorite: ArrayList<News> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_favorite)


    }

    private fun getListFavorite() {
        listFavorite.clear()

        val newsDatabase = NewsDatabase(requireContext(), "newsDB", null, 1)
        val createFavoriteTable = "CREATE TABLE IF NOT EXISTS favorite1 (title Text, description Text, " +
                "pubDate Text, link Text, guid Text, image Text, newsPage Text)"
        newsDatabase.querryData(createFavoriteTable)

        val selectTable = "SELECT * FROM favorite1"
        val cursor = newsDatabase.getData(selectTable)
        while (cursor.moveToNext()) {
            val title: String = cursor.getString(0)
            val description: String = cursor.getString(1)
            val pubDate: String = cursor.getString(2)
            val link: String = cursor.getString(3)
            val guid: String = cursor.getString(4)
            val image: String = cursor.getString(5)
            val newsPage: String = cursor.getString(6)
            listFavorite.add(News(title, description, pubDate, link, guid, image, newsPage))
        }

        Log.wtf("BottomFavoriteFragment", listFavorite.size.toString())
    }

    override fun onResume() {
        super.onResume()
        getListFavorite()
        setAdapter()
    }

    private fun setAdapter() {
        var linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        var newsAdapter = NewsAdapter(listFavorite, requireContext())
        newsAdapter.setOnItemListNews(this)
        recyclerView.adapter = newsAdapter
        recyclerView.layoutManager = linearLayoutManager
        newsAdapter.notifyDataSetChanged()
    }

    override fun onItemListNews(news: News) {
        NewsActivity.launch(news, requireContext())
    }

}