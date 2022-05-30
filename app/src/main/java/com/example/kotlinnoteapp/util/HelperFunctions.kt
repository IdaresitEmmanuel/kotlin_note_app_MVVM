package com.example.kotlinnoteapp.util

import java.text.SimpleDateFormat
import java.util.*

fun ISOStringToDate(dateString: String) : Date =
    SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(dateString)!!