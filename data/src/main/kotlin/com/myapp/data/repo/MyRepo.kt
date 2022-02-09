package com.myapp.data.repo

import com.myapp.data.model.ActionItem
import com.myapp.data.model.FeatureItem
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class MyRepo @Inject constructor() {

    private val idCounter = AtomicInteger()
    private val features = mutableSetOf<FeatureItem>()
    private val actions = mutableSetOf<ActionItem>()

    private fun <R> featuresSynchronized(block: () -> R): R = synchronized(features) {
        return block()
    }

    private fun <R> actionsSynchronized(block: () -> R): R = synchronized(actions) {
        return block()
    }

    //actions
    fun addAction(p: ActionItem): ActionItem = actionsSynchronized {
        if (actions.contains(p)) {
            actions.find { it == p }!!
        }
        p.id = idCounter.incrementAndGet()
        actions.add(p)
        p
    }

    fun getAction(id: Int) = actionsSynchronized { getAction(id.toString()) }
    fun getAllActions() = actionsSynchronized { actions.toList() }
    fun getAction(id: String) = actionsSynchronized {
        actions.find { it.id.toString() == id } ?: throw IllegalArgumentException("No entitiy found for $id")
    }

    //features
    fun addFeature(p: FeatureItem): FeatureItem = featuresSynchronized {
        if (features.contains(p)) {
            features.find { it == p }!!
        }
        p.id = idCounter.incrementAndGet()
        features.add(p)
        p
    }

    fun getFeature(id: Int) = featuresSynchronized { getFeature(id.toString()) }
    fun getAllFeatures() = featuresSynchronized { features.toList() }
    fun getFeature(id: String) = featuresSynchronized {
        features.find { it.id.toString() == id } ?: throw IllegalArgumentException("No entitiy found for $id")
    }
}