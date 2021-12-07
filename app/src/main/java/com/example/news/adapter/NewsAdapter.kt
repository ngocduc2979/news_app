package com.example.news.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.*
import com.example.news.listener.Listener
import kotlinx.android.synthetic.main.item_news.view.*
import kotlin.collections.ArrayList

class NewsAdapter(var listNews: ArrayList<News>, var context: Context) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    lateinit var listener: Listener
    var listLink: ArrayList<String> = ArrayList()

    fun setOnItemListNews(listener: Listener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.NewsViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)

        return NewsViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: NewsAdapter.NewsViewHolder, position: Int) {

        val newsDatabase = NewsDatabase(context, "newsDB", null, 1)
        val createTable = "CREATE TABLE IF NOT EXISTS seenNews (link Text)"
        val createFavoriteTable = "CREATE TABLE IF NOT EXISTS favorite (link Text)"
        newsDatabase.querryData(createTable)

        val selectTable = "SELECT * FROM seenNews"
//        val DELETE = "DELETE FROM seenNews"
//        newsDatabase.querryData(DELETE)

        listLink.clear()

        val cursor = newsDatabase.getData(selectTable)
        while (cursor.moveToNext()) {
            val link: String = cursor.getString(0)
            listLink.add(link)
        }

        for (i in 0..(listLink.size-1)){
            Log.wtf("checkTable", listLink.get(i))
        }

        var checkExits = false
        for (link in listLink) {
            if (listNews.get(position).link.equals(link)) {
                checkExits = true
            }
        }

        if (checkExits) {
            holder.tvTitle.setTextColor(R.color.black)
        }

        holder.tvTitle.setText(listNews.get(position).title)
        holder.tvPubDate.setText(listNews.get(position).pubDate)
        holder.tvNewsPage.setText(listNews.get(position).newsPage)

        Glide.with(context)
            .load(listNews.get(position).image)
            .placeholder(R.drawable.news_default)
            .into(holder.imvImage)

        holder.layout_item_news.setOnClickListener(View.OnClickListener {
            var addNews = "INSERT INTO seenNews VALUES ( '" + listNews.get(position).link + "')"

            if (!checkExits) {
                newsDatabase.querryData(addNews)
                notifyDataSetChanged()
            }

            listener.onItemListNews(listNews.get(position))
        })
    }

    override fun getItemCount(): Int = listNews.size

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imvImage    = itemView.imvImage
        var tvTitle     = itemView.tv_title_news
        var tvPubDate   = itemView.tv_pubDate
        var layout_item_news = itemView.layout_item_news
        var tvNewsPage = itemView.tv_page
    }

}