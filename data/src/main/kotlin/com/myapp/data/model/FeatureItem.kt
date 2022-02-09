package com.myapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FeatureItem(val name: String, val age: Int) {
    var id: Int? = null
}
