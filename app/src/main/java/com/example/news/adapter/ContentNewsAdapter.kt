package com.example.news.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.AppConfig
import com.example.news.Content
import com.example.news.News
import com.example.news.R
import com.example.news.listener.Listener
import kotlinx.android.synthetic.main.content_news.view.*
import kotlinx.android.synthetic.main.item_news.view.*

class ContentNewsAdapter(var listContentNews: ArrayList<Content>, var context: Context):
    RecyclerView.Adapter<ContentNewsAdapter.ContentNewsViewHolder>() {
    lateinit var listener: Listener

    fun setOnItemListNews(listener: Listener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentNewsAdapter.ContentNewsViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.content_news, parent, false)

        return ContentNewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContentNewsAdapter.ContentNewsViewHolder, position: Int) {
        Log.wtf("NewsAdapter", "onBinViewHolder")

//        holder.tvTitle.setText(listContentNews.get(position).title)
//        holder.tvContent.setText(listContentNews.get(position).content)

    }

    override fun getItemCount(): Int {
        return listContentNews.size
    }

    class ContentNewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var imvImage    = itemView.imvImage
//        var tvTitle     = itemView.tvTitle
//        var tvContent = itemView.tvContent
    }
}