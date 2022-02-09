package com.myapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ActionItem(val name: String, val age: Int) {
    var id: Int? = null
}
