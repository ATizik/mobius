/*
 * -\-\-
 * Mobius
 * --
 * Copyright (c) 2017-2018 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.mobius



import com.spotify.mobius.disposables.Disposable
import com.spotify.mobius.functions.Consumer
import com.spotify.mobius.runners.WorkRunner
import kotlinx.coroutines.experimental.Runnable


/**
 * Dispatches messages to a given runner.
 *
 * @param <M> message type (typically a model, event, or effect descriptor type)
</M> */
internal class MessageDispatcher<M>(runner: WorkRunner, consumer: Consumer<M>) : Consumer<M>, Disposable {

    private val runner: WorkRunner = checkNotNull(runner)
    private val consumer: Consumer<M> = checkNotNull(consumer)

    override fun accept(message: M) {
        runner.post(
                Runnable {
                    try {
                        consumer.accept(message)

                    } catch (throwable: Throwable) {
                        //LOGGER.error(
                        //        "Consumer threw an exception when accepting message: {}", message, throwable)
                    }
                })
    }

    override fun dispose() {
        runner.dispose()
    }

    companion object {

        //private val LOGGER = LoggerFactory.getLogger(MessageDispatcher::class.java)//TODO:logger
    }
}
