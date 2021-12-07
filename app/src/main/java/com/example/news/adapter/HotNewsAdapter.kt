package com.example.news.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.Content
import com.example.news.listener.Listener
import com.example.news.News
import com.example.news.R
import kotlinx.android.synthetic.main.item_news.view.*

class HotNewsAdapter(var listHotNews: ArrayList<News>, var context: Context, var listContentNews: ArrayList<Content>): RecyclerView.Adapter<HotNewsAdapter.HotNewsViewHolder>() {

    lateinit var listener: Listener

    fun setOnItemListNews(listener: Listener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotNewsViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return HotNewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: HotNewsViewHolder, position: Int) {
        holder.tvTitle.setText(listHotNews.get(position).title)
        holder.tvPubDate.setText(listHotNews.get(position).pubDate)

        Glide.with(context)
            .load(listHotNews.get(position).image)
            .placeholder(R.drawable.news_default)
            .into(holder.imvImage)

        holder.layout_item_news.setOnClickListener(View.OnClickListener {
            listener.onItemListNews(listHotNews.get(position))
        })
    }

    override fun getItemCount(): Int {
        return 3
    }

    class HotNewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var imvImage    = itemView.imvImage
        var tvTitle     = itemView.tv_title_news
        var tvPubDate   = itemView.tv_pubDate
        var layout_item_news = itemView.layout_item_news
    }
}