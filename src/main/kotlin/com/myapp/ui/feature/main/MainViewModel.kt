package com.myapp.ui.feature.main

import com.myapp.data.local.MyServer
import com.myapp.data.repo.MyRepo
import com.myapp.util.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val myRepo: MyRepo,
    private val myServer: MyServer
    // Inject your repos here...
) : ViewModel() {
    companion object {
        const val INIT_SERVER_MSG = "Start server"
    }

    private val _currentIp = MutableStateFlow("")
    val currentIp: StateFlow<String> = _currentIp

    fun startServer(): Boolean {
        return myServer.startServer()
    }

    fun stopServer(): Boolean {
        return myServer.stopServer()
    }

    fun showIp() {
        _currentIp.value = myServer.findMyIp()
    }
}