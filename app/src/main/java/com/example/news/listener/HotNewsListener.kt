package com.example.news.listener

import android.content.Context

interface HotNewsListener {
    fun setOnHotNews(link: String, context: Context)
}