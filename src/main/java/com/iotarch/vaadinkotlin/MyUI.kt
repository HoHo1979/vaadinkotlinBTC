package com.iotarch.vaadinkotlin

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.iotarch.vaadinkotlin.entity.BtcUsd
import com.vaadin.annotations.Push
import javax.servlet.annotation.WebServlet

import com.vaadin.annotations.Theme
import com.vaadin.annotations.VaadinServletConfiguration
import com.vaadin.server.VaadinRequest
import com.vaadin.server.VaadinServlet
import com.vaadin.ui.*
import com.vaadin.ui.renderers.AbstractRenderer
import com.vaadin.ui.renderers.DateRenderer
import com.vaadin.ui.renderers.LocalDateTimeRenderer
import com.vaadin.ui.renderers.Renderer
import okhttp3.*
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.concurrent.timer

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 *
 *
 * The UI is initialized using [.init]. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Push
@Theme("mytheme")
class MyUI : UI() {

    val btcGrid = Grid<BtcUsd>()

    var btclists = mutableSetOf<BtcUsd>()

    override fun init(vaadinRequest: VaadinRequest) {

        val layout = VerticalLayout()

        btcGrid.setSizeFull()
        btcGrid.addColumn(BtcUsd::ask).setCaption("Ask")
        btcGrid.addColumn(BtcUsd::bid).setCaption("Bid")
        btcGrid.addColumn(BtcUsd::high).setCaption("High")
        btcGrid.addColumn(BtcUsd::low).setCaption("Low")
        btcGrid.addColumn(BtcUsd::volume).setCaption("Volume")
        btcGrid.addColumn(BtcUsd::last_price).setCaption("Last Price")
        btcGrid.addColumn(BtcUsd::getDateAndTime).setCaption("Time")
        btcGrid.setItems(btclists)

        val button = Button("Click Me to Start Connect to Bitfinex")
        button.addClickListener { e ->

          val timer = Timer("networkcall",true)

            timer.scheduleAtFixedRate(1000,5000){
                connectToGetInformation()
            }


        }


        layout.addComponents(button, btcGrid)

        content = layout
    }



    fun connectToGetInformation(){

        val url ="http://api.bitfinex.com/v1/pubticker/btcusd"

        val request=Request.Builder().url(url).build()

        val okHttpClient = OkHttpClient.Builder().
                build()

        okHttpClient
                .newCall(request)
                .enqueue(object:Callback{

            override fun onFailure(p0: Call, p1: IOException) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(p0: Call, p1: Response) {

                if (p1.isSuccessful) {

                    //If this is toString it will return a object reference rather then String
                    var data = p1.body()?.string()

                    val type = object : TypeToken<BtcUsd>() {}.type

                    val btcUsd = Gson().fromJson<BtcUsd>(data, type)

                    btclists.add(btcUsd)

                    ui.access(object:Runnable{
                        override fun run() {
                            btcGrid.dataProvider.refreshAll()
                        }
                    })

                } else {
                    print("The network response reading failed")
                }

            }

        })



    }
//
//    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

    @WebServlet(urlPatterns = arrayOf("/*"), name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI::class, productionMode = false)
    class MyUIServlet : VaadinServlet()
}




