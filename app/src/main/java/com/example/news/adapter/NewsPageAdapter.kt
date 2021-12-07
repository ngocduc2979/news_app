package com.example.news.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.AppConfig
import com.example.news.NewsPage
import com.example.news.R
import com.example.news.listener.NewsPageListener
import kotlinx.android.synthetic.main.item_news_page.view.*

class NewsPageAdapter(var list: ArrayList<NewsPage>, var context: Context): RecyclerView.Adapter<NewsPageAdapter.NewsPageViewHolder>() {

    lateinit var newsPageListener: NewsPageListener

    fun setOnNewsPageListener(newsPageListener: NewsPageListener) {
        this.newsPageListener = newsPageListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsPageViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_news_page,parent, false)
        return NewsPageViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsPageViewHolder, position: Int) {
        Glide.with(context)
            .load(list.get(position).drawable)
            .into(holder.imvImageCover)

        holder.tvNewsName.setText(list.get(position).name)

        holder.layoutNewsPage.setOnClickListener {
            AppConfig.getInstance(context).setNewsPage(list.get(position).name)
            newsPageListener.setOnNewPage(context)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class NewsPageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var imvImageCover = itemView.imv_Image_news_page
        var tvNewsName = itemView.tv_newsPage_name
        var layoutNewsPage = itemView.layout_news_page
    }
}