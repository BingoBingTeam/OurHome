package com.lotus.ourhome.ui.goods

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GoodsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is goods Fragment"
    }
    val text: LiveData<String> = _text
}