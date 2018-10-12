package io.github.ejiang.roomtests2.addvisit

import android.os.Handler
import android.os.HandlerThread

// this is for DB write operations
class WriterThread(name: String) : HandlerThread(name) {

    private lateinit var mWriterHandler: Handler

    override fun onLooperPrepared() {
        super.onLooperPrepared()
        mWriterHandler = Handler(looper)
    }

    fun postTask(r: Runnable) {
        mWriterHandler.post(r)
    }
}
