package com.example.news

import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class Category( var link: String,  var title: String): Parcelable

@Parcelize
data class CategoryBottom( var icon: Int,  var title: String): Parcelable

@Parcelize
data class News(var title: String, var description: String, var pubDate: String, var link: String, var guid: String,
                var image: String, var newsPage: String): Parcelable, Serializable

@Parcelize
data class NewsPage(var name: String, var drawable: Int): Parcelable

data class Content(var title: String, var content: String, var img: String, var date: String)