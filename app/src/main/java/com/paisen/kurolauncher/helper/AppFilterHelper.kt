package com.paisen.kurolauncher.helper

import com.paisen.kurolauncher.data.AppModel

interface AppFilterHelper {
    fun onAppFiltered(items:List<AppModel>)
}