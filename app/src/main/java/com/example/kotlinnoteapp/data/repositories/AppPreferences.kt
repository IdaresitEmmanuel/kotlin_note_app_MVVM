package com.example.kotlinnoteapp.data.repositories

import android.content.Context
import android.content.SharedPreferences

import com.example.kotlinnoteapp.MyApplication
import com.example.kotlinnoteapp.constants.NoteFilter

class AppPreferences private constructor() {
    private var sharedPreferences: SharedPreferences? = null
    private val preference: SharedPreferences
        get() {
            if (sharedPreferences == null) {
                sharedPreferences = MyApplication.appContext?.getSharedPreferences(
                    SHARED_PREFERENCE,
                    Context.MODE_PRIVATE
                )
            }
            return sharedPreferences!!
        }
    var filter: NoteFilter
        get() {
            val sharedPreferences = preference
            val filterString= sharedPreferences.getString(
                FILTER_KEY,
                NoteFilter.none.name
            )
            return NoteFilter.valueOf((filterString ?: NoteFilter.none) as String)
        }
        set(filter) {
            val sharedPreferences = preference
            val editor = sharedPreferences.edit()
            editor.putString(
                FILTER_KEY,
                filter.name
            )
            editor.apply()
        }

    companion object {
        var instance: AppPreferences? = null
            get() {
                if (field == null) {
                    field =
                        AppPreferences()
                }
                return field
            }
        private const val SHARED_PREFERENCE = "app_shared_preferences"
        private const val FILTER_KEY = "filter_preference"
    }
}