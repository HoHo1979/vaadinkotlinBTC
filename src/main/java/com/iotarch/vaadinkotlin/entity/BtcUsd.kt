package com.iotarch.vaadinkotlin.entity

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

data class BtcUsd(val mid:String, val bid:String, val ask:String, val last_price:String,
                  val low:String, val high:String, val volume:String, val timestamp:String){



    fun getDateAndTime():String{

        val time = timestamp.toFloat().toLong()

        val instant = Instant.ofEpochSecond(time)

        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm:ss"))

    }


}


