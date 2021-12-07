package com.example.news.activity

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.news.*
import kotlinx.android.synthetic.main.content_news.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder

public class NewsActivity: AppCompatActivity() {

    var content: Content? = null
    var listFavorite: ArrayList<News> = ArrayList()
    val newsDatabase = NewsDatabase(this, "newsDB", null, 1)
    var checkExits = false


    companion object {
        private val KEY_NEWS: String = "com.example.news.activity.news"

        fun launch(news: News, context: Context) {
            var intent = Intent(context, NewsActivity::class.java)
            var bundle = Bundle()
            bundle.putSerializable(KEY_NEWS, news)
            intent.putExtras(bundle)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_news)

        initView()
    }

    fun initView() {

        var news: News = intent.extras!!.get(KEY_NEWS) as News

        ReadHTML().execute(news.link)

        setFavoriteColor(news)

        toolbar.setOnClickListener {
            onBackPressed()
        }

        favorite.setOnClickListener {
            clickFavorite(news)
        }
    }

    private fun setFavoriteColor(news: News) {
        val createFavoriteTable = "CREATE TABLE IF NOT EXISTS favorite1 (title Text, description Text, " +
                "pubDate Text, link Text, guid Text, image Text, newsPage Text)"
        newsDatabase.querryData(createFavoriteTable)

        val selectTable = "SELECT * FROM favorite1"

        listFavorite.clear()

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

        for (link in listFavorite) {
            if (news.link.equals(link.link)) {
                checkExits = true
            }
        }
        if (checkExits) {
            favorite.setImageResource(R.drawable.ic_baseline_favorite_24_red)
        }
    }

    private fun clickFavorite(news: News) {
        if (checkExits) {
            val deleteNews: String = "DELETE FROM favorite1 WHERE link = " + "'" + news.link + "'"
            val deleteall: String = "DELETE FROM favorite1"
            newsDatabase.querryData(deleteNews)
            favorite.setImageResource(R.drawable.ic_baseline_favorite_24_white)
            checkExits = false
        } else {
            var title = news.title
            var description = news.description
            if (news.title.contains("'")) {
                title = news.title.replace("'", "''")
            }
            if (news.description.contains("'")) {
                description = news.description.replace("'", "''")
            }

            val addNews: String = "INSERT INTO favorite1 VALUES ('" + title + "', '" +
                    description + "', '" + news.pubDate + "', '" + news.link + "', '" +
                    news.guid + "', '" + news.image + "', '" + news.newsPage + "')"

            Log.wtf("NewsActivity1", "!equal")
            newsDatabase.querryData(addNews)
            favorite.setImageResource(R.drawable.ic_baseline_favorite_24_red)
            checkExits = true
        }
    }

    private fun showArticle(result: String) {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowContentAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowFileAccess = true

        webView.loadData(result, "text/html", "UTF-8")
        tv_loading.visibility = View.GONE
    }

    inner class ReadHTML: AsyncTask<String, Int, String>(){

        override fun doInBackground(vararg p0: String): String {

            return getContent(p0[0])
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            showArticle(result)
        }

        fun getContent(link: String): String {

            val document: Document = Jsoup.connect(link).get()
//            Log.wtf("HTML", document.toString())
            var titleHtml: Elements? = null
            var decriptionHtml: Elements? = null
            var bodyHtml: Elements? = null
            var author: Elements? = null
            var styleImage = ""

            var bufferedReader: BufferedReader? = null
            try {
                val builder = StringBuilder()
                bufferedReader = BufferedReader(
                    InputStreamReader(assets.open("style.css")))
                var line: String? = bufferedReader.readLine()
                while (line != null) {
                    builder.append(line)
                    line = bufferedReader.readLine()
                }
                styleImage = builder.toString()

            } catch (e: IOException) {
                e.printStackTrace()
                //log the exception
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (e: IOException) {
                        //log the exception
                    }
                }
            }

            if (AppConfig.getInstance(applicationContext).getNewsPage().equals("VN Express")) {
                titleHtml= document.select("h1.title-detail")
                decriptionHtml = document.select("p.description")
                bodyHtml = document.select("article.fck_detail")
                bodyHtml.select("ul.list-news").remove()

                if (titleHtml.isEmpty()) {
                    titleHtml = document.select("h1.title_news_detail")
                    decriptionHtml = document.select("p.short_intro")
                    bodyHtml = document.select("div.article_body")
                    bodyHtml.select("ul.list-news").remove()
//                    Log.wtf("body HTML", bodyHtml.toString())
                }
                return "<html><body>$titleHtml$decriptionHtml$bodyHtml$styleImage</body></html>"
            } else if (AppConfig.getInstance(applicationContext).getNewsPage().equals("Tuổi trẻ online")) {
                titleHtml= document.select("h1.article-title")
                decriptionHtml = document.select("h2.sapo")
                bodyHtml = document.select("#main-detail-body")
                author = document.select("div.author")
                return "<html>$styleImage<body>$titleHtml$decriptionHtml$bodyHtml</br>$author</body></html>"
            } else if (AppConfig.getInstance(applicationContext).getNewsPage().equals("24h")) {
                titleHtml= document.select("h1.tuht_show")
//                decriptionHtml = document.select("h2.cate-24h-foot-arti-deta-sum ctTp tuht_show")
                bodyHtml = document.select("#div_news_content")
                bodyHtml.select("script").remove()
                bodyHtml.select("div#pollContainer").remove()
                bodyHtml.select("div.icoSocial").remove()
                bodyHtml.select("div.viewVideoPlay").remove()
                bodyHtml.select("[style~=display:flex]").remove()
                bodyHtml.select("[align~=center]").remove()
                bodyHtml.select("[style~=position:relative;margin:]").remove()
                bodyHtml.select("div[style~=background-color:#FFFFFF]").remove()

                author = document.select("div.cate-24h-foot-arti-deta-author")

                val html = "<html>$styleImage<body>$titleHtml${bodyHtml.toString().replace("display:none;", "")}</br>$author</body></html>"
                Log.wtf("bodyHTML", html)
                return html

            } else {
                return "<html><body>$titleHtml$decriptionHtml$bodyHtml$styleImage</body></html>"
            }

//            Log.wtf("title HTML", titleHtml.toString())
//            Log.wtf("decription HTML", decriptionHtml.toString())
//            Log.wtf("body HTML", bodyHtml.toString())

        }

    }

}