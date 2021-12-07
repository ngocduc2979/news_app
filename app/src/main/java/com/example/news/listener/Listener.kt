package com.example.news.listener

import android.content.Context
import com.example.news.Content
import com.example.news.News

public interface Listener {
    fun onItemListNews(news: News)
}