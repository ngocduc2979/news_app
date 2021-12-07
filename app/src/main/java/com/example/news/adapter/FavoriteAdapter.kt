package com.example.news.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import kotlinx.android.synthetic.main.item_news.view.*

class FavoriteAdapter(var list: ArrayList<String>, var context: Context): RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class FavoriteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var imvImage    = itemView.imvImage
        var tvTitle     = itemView.tv_title_news
        var tvPubDate   = itemView.tv_pubDate
        var layout_item_news = itemView.layout_item_news
        var tvNewsPage = itemView.tv_page
    }

}