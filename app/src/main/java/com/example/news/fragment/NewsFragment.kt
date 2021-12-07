package com.example.news.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.*
import com.example.news.listener.Listener
import com.example.news.activity.NewsActivity
import com.example.news.adapter.NewsAdapter
import kotlinx.android.synthetic.main.fragment_news.*
//import org.w3c.dom.Document
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"

class NewsFragment : Fragment() {

    private var param1: Category? = null
    private var listNews: ArrayList<News> = ArrayList()
    private var listContent: ArrayList<Content> = ArrayList()
    var link: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
            link = it.getString("link")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this com.example.news.fragment
        val view: View = inflater.inflate(R.layout.fragment_news, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ReadXML().execute(link)
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance(category: Category) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, category)
                }
            }
    }

    inner class ReadXML: AsyncTask<String, Int, Any>(), Listener {

        override fun doInBackground(vararg p0: String): Unit? {
            listNews.clear()
            getData(p0[0])
            return null
        }

        override fun onPostExecute(result: Any?) {
            super.onPostExecute(result)
            setAdapter()
            setHideUpdateButton(link)
        }

        private fun setHideUpdateButton(linkRSS: String) {
            var lastScrollOffset = 0
            recycler_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val scrollOffset = recyclerView.computeVerticalScrollOffset()

                    if (scrollOffset == 0) {
                        layout_update.setVisibility(View.GONE)
                    } else if (scrollOffset > lastScrollOffset) {
                        layout_update.setVisibility(View.GONE)
                    } else if (scrollOffset < lastScrollOffset ) {
                        layout_update.setVisibility(View.VISIBLE)
                    }

                    lastScrollOffset = scrollOffset
                }
            })

            layout_update.setOnClickListener {
                recycler_list.smoothScrollToPosition(0)
                ReadXML().execute(linkRSS)
                recycler_list.adapter!!.notifyDataSetChanged()
            }
        }

        private fun setAdapter() {
            var newsAdapter: NewsAdapter? = context?.let { NewsAdapter(listNews, it) }
            newsAdapter?.setOnItemListNews(this)
            recycler_list.adapter = newsAdapter
            recycler_list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recycler_list.adapter!!.notifyDataSetChanged()
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
                    var newsPage: String = AppConfig.getInstance(context!!).getNewsPage()

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
                                        if (AppConfig.getInstance(context!!).getNewsPage().equals("24h", ignoreCase = true)) {
                                            if (description.contains("src=\'")) {
                                                var start: Int = description.indexOf("src=\'")+5
                                                var end: Int = description.indexOf("' alt")
                                                image = description.substring(start, end)
//                                                Log.wtf("checkImage", image)
                                                news.image = image
                                            } else {
                                                news.image = ""
                                            }
                                        } else if (AppConfig.getInstance(context!!).getNewsPage().equals("Tuổi trẻ online", ignoreCase = true)) {
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
//                                    pubDate = text
                                    if (news != null) {
                                        news.pubDate = getTimePostNews(text)
                                    }
                                } else if (tag.equals("link", ignoreCase = true)) {
                                    link = text
                                    if (news != null) {
//                                        var document: Document = Jsoup.connect(text).get()
//                                        var desElement: Elements = document.getElementsByClass("description")
//                                        var descriptionContent: String = desElement.text()
//
//                                        var content: String = ""
//                                        var titleContent: Elements = document.getElementsByClass("Normal")
//                                        for (content1 in titleContent) {
//                                            content = content + content1.text() + "\n" + "\n"
//                                        }
//                                        listContent.add(Content(title, descriptionContent, content))

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
                                        listNews.add(news)
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

        override fun onItemListNews(news: News) {
            context.let {
                if (it != null) {
                    NewsActivity.launch(news, it)
                }
            }
//            setAdapter()
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
            var currenTime: String = Calendar.getInstance().getTime().toString()
            var currentYear: Int = Integer.parseInt(currenTime.substring(30, 34))
            var currentMonth: Int = getMonth(currenTime.substring(4, 7))
            var currentDay: Int = Integer.parseInt(currenTime.substring(8, 10))
            var currentHour: Int = Integer.parseInt(currenTime.substring(11, 13))
            var currentMinustes: Int = Integer.parseInt(currenTime.substring(14, 16))
            var currentSecond: Int = Integer.parseInt(currenTime.substring(17, 19))
            var pubDate: String = ""

            var newsTime: String = text
            var newsYear: String = newsTime.substring(12, 16)
            var newsMonth: Int = getMonth(newsTime.substring(8, 11))
            var newsDay: String = newsTime.substring(5, 7)
            var newsHour: String = newsTime.substring(17, 19)
            var newsMinustes: String = newsTime.substring(20, 22)
            var newsSecond: String = newsTime.substring(23, 25)

            pubDate = newsDay + "/" + newsMonth.toString() + "/" + newsYear + ", " + newsHour + ":" + newsMinustes

            return pubDate
        }

    }

}