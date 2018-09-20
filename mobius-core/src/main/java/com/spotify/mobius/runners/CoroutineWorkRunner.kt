package com.spotify.mobius.runners

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.consumeEach
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

class CoroutineWorkRunner(override val coroutineContext: CoroutineContext, capacity: Int): WorkRunner, CoroutineScope {
    private val jobPool = Job()
    private val channel = Channel<Runnable>(Channel.UNLIMITED)
    init {
            launch {
                channel.consumeEach {
                    while (coroutineContext.isActive && jobPool.children.count() > capacity) {
                        delay(1, TimeUnit.MICROSECONDS)
                    }

                    if (coroutineContext.isActive) {
                        launch(jobPool) { it.run() }
                    } else {
                        channel.close()
                    }
                }
            }
    }

    override fun post(runnable: Runnable) {
        if(channel.isClosedForSend) {
            throw IllegalStateException("Worker was disposed")
        }
        channel.offer(runnable)
    }

    override fun dispose() {
        channel.close()
        coroutineContext.cancel()
    }

}