package com.android.example.wordlistsql.extension

import android.content.Context
import android.view.LayoutInflater

/**
 * Created by Boonya Kitpitak on 7/22/18.
 */
val Context.layoutInflater: LayoutInflater get() = LayoutInflater.from(this)