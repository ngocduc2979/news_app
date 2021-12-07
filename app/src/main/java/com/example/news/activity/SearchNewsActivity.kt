package com.example.news.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.*
import com.example.news.listener.Listener
import com.example.news.adapter.HotNewsAdapter
import com.example.news.adapter.NewsAdapter
import kotlinx.android.synthetic.main.layout_search.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.collections.ArrayList

class SearchNewsActivity: AppCompatActivity() {

    var listCategory: ArrayList<Category> = ArrayList()
    var listNewsSearch: ArrayList<News> = ArrayList()
    var listAllNews: ArrayList<News> = ArrayList()
    var listHotNews: ArrayList<News> = ArrayList()
    var listContentNews: ArrayList<Content> = ArrayList()

    companion object {
        var KEY_LIST_CATEGORY: String = "list category"

        fun launch(context: Context, listTopTab: ArrayList<Category>) {
            var intent = Intent(context, SearchNewsActivity::class.java)
            intent.putParcelableArrayListExtra(KEY_LIST_CATEGORY, listTopTab)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_search)

        initView()
        ReadXML().execute()
    }

    override fun onResume() {
        super.onResume()
        search.setOnSearchClickListener {
        }
    }

    private fun initView() {
        search.onActionViewExpanded()
        close_search_view_button.setOnClickListener {
            onBackPressed()
        }
    }

    inner class ReadXML: AsyncTask<String, Int, Any>(), Listener {

        override fun doInBackground(vararg p0: String): Unit? {
            listAllNews.clear()
            listHotNews.clear()

            listCategory = intent.getParcelableArrayListExtra<Category>(KEY_LIST_CATEGORY) as ArrayList<Category>
            for (i in 0..(listCategory.size - 1)) {
                getData(listCategory.get(i).link)
            }

            getDataHotNews("https://vnexpress.net/rss/tin-noi-bat.rss")

            search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    searchNews(query)
                    setHideView()
                    setAdapter()
                    return false
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })



            return null
        }

        override fun onPostExecute(result: Any?) {
            super.onPostExecute(result)
            setAdapterHotNews()
        }

        private fun setHideView() {
            if (listNewsSearch.size != 0) {
                recycler_search_news.visibility = View.VISIBLE
                tvHotNews.visibility = View.GONE
                tvMore.visibility = View.GONE
                recycler_list_hot_news.visibility = View.GONE
            } else {
                recycler_search_news.visibility = View.GONE
                tvHotNews.visibility = View.VISIBLE
                tvMore.visibility = View.VISIBLE
                recycler_list_hot_news.visibility = View.VISIBLE
            }
        }

        private fun setAdapterHotNews() {
            var hotNewsAdapter: HotNewsAdapter = HotNewsAdapter(listHotNews, applicationContext, listContentNews)
            hotNewsAdapter.setOnItemListNews(this)
            recycler_list_hot_news.adapter = hotNewsAdapter
            recycler_list_hot_news.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
            recycler_list_hot_news.adapter!!.notifyDataSetChanged()
        }

        private fun setAdapter() {
                var newsAdapter: NewsAdapter = NewsAdapter(listNewsSearch, applicationContext)
                newsAdapter.setOnItemListNews(this)
                recycler_search_news.adapter = newsAdapter
                recycler_search_news.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
                recycler_search_news.adapter!!.notifyDataSetChanged()
        }

        private fun searchNews(search: String) {
            listNewsSearch.clear()
            for (i in 0..(listAllNews.size-1)) {
                if (search != "") {
                    if (listAllNews.get(i).title.contains(search, ignoreCase = true)){
                        listNewsSearch.add(listAllNews.get(i))
//                        Log.wtf("checkSearch", listNewsSearch.size.toString() + " size")
                    }
                }
            }
        }

        override fun onItemListNews(news: News) {
            NewsActivity.launch(news, applicationContext)
        }

        private fun getDataHotNews(linkRSS: String) {
            try {
                //kiểm tra link
                val url = URL(linkRSS)
                //Mở kết nối
                val urlConnection = url.openConnection() as HttpURLConnection
                //lấy data vào qua inputStream ( một đối tượng cho phép đọc )
                val inputStream = urlConnection.inputStream
                //Dùng các đối tượng sau để xử lí file hoặc dữ liệu kiểu xml
                val xmlPullParserFactory = XmlPullParserFactory.newInstance()
                xmlPullParserFactory.isNamespaceAware = false
                val xmlPullParser = xmlPullParserFactory.newPullParser()
                xmlPullParser.setInput(inputStream, "utf-8")

                var event: Int = xmlPullParser.eventType

                var news: News? = null
                var text: String = ""
                var title: String = ""
                var description: String = ""
                var pubDate: String = ""
                var link: String = ""
                var guid: String = ""
                var image: String = ""
                var newsPage: String = AppConfig.getInstance(applicationContext).getNewsPage()

                while (event != XmlPullParser.END_DOCUMENT) {
                    val tag = xmlPullParser.name

                    when (event) {
                        XmlPullParser.START_TAG -> {
                            if (tag.equals("item", ignoreCase = true)) {
                                news = News(title, description, pubDate, link, guid, image, newsPage)
                            }
                        }
                        XmlPullParser.TEXT -> {
                            text = xmlPullParser.text
                        }
                        XmlPullParser.END_TAG -> {
                            if (tag.equals("title", ignoreCase = true)) {
                                title = text
                                if (news != null) {
                                    news.title = title
                                }
                            } else if (tag.equals("description", ignoreCase = true)) {
                                description = text
                                if (news != null) {
                                    news.description = description
                                    if (description.contains("src=\"")) {
                                        var start: Int = description.indexOf("src=\"")+5
                                        var end: Int = description.indexOf("\" >")
                                        image = description.substring(start, end)
                                        news.image = image
                                    } else {
                                        news.image = ""
                                    }
                                }
                            } else if (tag.equals("pubDate", ignoreCase = true)) {
//                                pubDate = text
                                if (news != null) {
                                    news.pubDate = getTimePostNews(text)
                                }
                            } else if (tag.equals("link", ignoreCase = true)) {
                                link = text
                                if (news != null) {
                                    news.link = link
                                }
                            } else if (tag.equals("guid", ignoreCase = true)) {
                                guid = text
                                if (news != null) {
                                    news.guid = guid
                                }
                            }

                            if (tag.equals("item", ignoreCase = true)) {
                                if (news != null) {
                                    listHotNews.add(news)
                                }
                            }
                        }
                    }
                    event = xmlPullParser.next()
                }
            } catch (e: MalformedURLException) {
                Log.e("Error 1 :  " , e.message.toString());
//            e.printStackTrace();
            } catch (e: IOException) {
                Log.e("Error 2 :  " , e.message.toString());
//            e.printStackTrace();
            } catch (e: XmlPullParserException) {
                Log.e("Error 3 :  " , e.message.toString());
//            e.printStackTrace();
            }

        }

        private fun getData(linkRSS: String) {
            try {
                //kiểm tra link
                val url = URL(linkRSS)
                //Mở kết nối
                val urlConnection = url.openConnection() as HttpURLConnection
                //lấy data vào qua inputStream ( một đối tượng cho phép đọc )
                val inputStream = urlConnection.inputStream
                //Dùng các đối tượng sau để xử lí file hoặc dữ liệu kiểu xml
                val xmlPullParserFactory = XmlPullParserFactory.newInstance()
                xmlPullParserFactory.isNamespaceAware = false
                val xmlPullParser = xmlPullParserFactory.newPullParser()
                xmlPullParser.setInput(inputStream, "utf-8")

                var event: Int = xmlPullParser.eventType

                var news: News? = null
                var text: String = ""
                var title: String = ""
                var description: String = ""
                var pubDate: String = ""
                var link: String = ""
                var guid: String = ""
                var image: String = ""
                var newsPage: String = AppConfig.getInstance(applicationContext).getNewsPage()

                while (event != XmlPullParser.END_DOCUMENT) {
                    val tag = xmlPullParser.name

                    when (event) {
                        XmlPullParser.START_TAG -> {
                            if (tag.equals("item", ignoreCase = true)) {
                                news = News(title, description, pubDate, link, guid, image, newsPage)
                            }
                        }
                        XmlPullParser.TEXT -> {
                            text = xmlPullParser.text
                        }
                        XmlPullParser.END_TAG -> {
                            if (tag.equals("title", ignoreCase = true)) {
                                title = text
                                if (news != null) {
                                    news.title = title
                                }
                            } else if (tag.equals("description", ignoreCase = true)) {
                                description = text
//                                    Log.wtf("checkdescription", description)
                                if (news != null) {
                                    if (AppConfig.getInstance(applicationContext).getNewsPage().equals("24h", ignoreCase = true)) {
                                        if (description.contains("src=\'")) {
                                            var start: Int = description.indexOf("src=\'")+5
                                            var end: Int = description.indexOf("' alt")
                                            image = description.substring(start, end)
//                                                Log.wtf("checkImage", image)
                                            news.image = image
                                        } else {
                                            news.image = ""
                                        }
                                    } else if (AppConfig.getInstance(applicationContext!!).getNewsPage().equals("Tuổi trẻ online", ignoreCase = true)) {
                                        if (description.contains("src=\"")) {
                                            var start: Int = description.indexOf("src=\"")+5
                                            var end: Int = description.indexOf("\" />")
                                            image = description.substring(start, end)
                                            news.image = image
                                        } else {
                                            news.image = ""
                                        }
                                    } else {
                                        if (description.contains("src=\"")) {
                                            var start: Int = description.indexOf("src=\"")+5
                                            var end: Int = description.indexOf("\" >")
                                            image = description.substring(start, end)
                                            news.image = image
                                        } else {
                                            news.image = ""
                                        }
                                    }
                                    news.description = description
                                }
                            } else if (tag.equals("pubDate", ignoreCase = true)) {
                                pubDate = text
                                if (news != null) {
                                    news.pubDate = pubDate
                                }
                            } else if (tag.equals("link", ignoreCase = true)) {
                                link = text
                                if (news != null) {
//                                        val urlLink = URL(text)
//                                        val urlConnectionLink = urlLink.openConnection() as HttpURLConnection
//                                        val inputStreamLink = urlConnectionLink.inputStream
//                                        link = CharStreams.toString(InputStreamReader(inputStreamLink, Charsets.UTF_8))
                                    news.link = link
//                                        Log.wtf("checkLink", link)
                                }
                            } else if (tag.equals("guid", ignoreCase = true)) {
                                guid = text
                                if (news != null) {
                                    news.guid = guid
                                }
                            }

                            if (tag.equals("item", ignoreCase = true)) {
                                if (news != null) {
                                    listAllNews.add(news)
                                }
                            }
                        }
                    }
                    event = xmlPullParser.next()
                }
            } catch (e: MalformedURLException) {
                Log.e("Error 1 :  " , e.message.toString());
//            e.printStackTrace();
            } catch (e: IOException) {
                Log.e("Error 2 :  " , e.message.toString());
//            e.printStackTrace();
            } catch (e: XmlPullParserException) {
                Log.e("Error 3 :  " , e.message.toString());
//            e.printStackTrace();
            }

        }

        fun getMonth(month: String): Int {
            if (month.equals("Jan", ignoreCase = true)) {
                return 1
            } else if (month.equals("Feb", ignoreCase = true)) {
                return 2
            } else if (month.equals("Mar", ignoreCase = true)) {
                return 3
            } else if (month.equals("Apr", ignoreCase = true)) {
                return 4
            } else if (month.equals("May", ignoreCase = true)) {
                return 5
            } else if (month.equals("Jun", ignoreCase = true)) {
                return 6
            } else if (month.equals("July", ignoreCase = true)) {
                return 7
            } else if (month.equals("Aug", ignoreCase = true)) {
                return 8
            } else if (month.equals("Sep", ignoreCase = true)) {
                return 9
            } else if (month.equals("Oct", ignoreCase = true)) {
                return 10
            } else if (month.equals("Nov", ignoreCase = true)) {
                return 11
            } else {
                return 12
            }
        }

        fun getTimePostNews(text: String): String {
            var pubDate: String = ""

            var newsTime: String = text
            var newsYear: String = newsTime.substring(12, 16)
            var newsMonth: Int = getMonth(newsTime.substring(8, 11))
            var newsDay: String = newsTime.substring(5, 7)
            var newsHour: String = newsTime.substring(17, 19)
            var newsMinustes: String = newsTime.substring(20, 22)
            pubDate = newsDay + "/" + newsMonth.toString() + "/" + newsYear + ", " + newsHour + ":" + newsMinustes

            return pubDate
        }

    }
}