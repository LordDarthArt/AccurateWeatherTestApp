package tk.lorddarthart.accurateweathertestapp.util

import android.os.AsyncTask

class TaskLoader private constructor() {

    init {
        throw UnsupportedOperationException()
    }

    companion object {

        private var task: AsyncTask<*, *, *>? = null

        fun setTask(task: AsyncTask<*, *, *>) {
            Companion.task = task
        }

        fun cancel() {
            task!!.cancel(true)
        }
    }
}
