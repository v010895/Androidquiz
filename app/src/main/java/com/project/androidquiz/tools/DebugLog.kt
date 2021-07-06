package com.project.androidquiz.tools

import android.util.Log

class DebugLog {
    companion object{
        private const val enable = false
        fun d(tag:String, message:String)
        {
            if(enable)
                Log.d(tag,message)
        }
    }
}