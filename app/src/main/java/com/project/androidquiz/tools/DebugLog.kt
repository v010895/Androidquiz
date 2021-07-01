package com.project.androidquiz.tools

import android.util.Log

class DebugLog {
    companion object{
        private const val enable = true
        fun d(tag:String, message:String)
        {
            Log.d(tag,message)
        }
    }
}