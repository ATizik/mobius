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
package com.spotify.mobius.runners

import kotlinx.coroutines.experimental.*

/** A [WorkRunner] implementation that is backed by an [ExecutorService].  */
class ExecutorServiceWorkRunner() : WorkRunner {

    override fun post(runnable: Runnable) {
        //service.submit(runnable)
    }

    override fun dispose() {
        /*try {
            val runnables = service.shutdownNow()

            if (!runnables.isEmpty()) {
/*                LOGGER.warn(
                        "Disposing ExecutorServiceWorkRunner with {} outstanding tasks.", runnables.size)*/
            }

            if (!service.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                //LOGGER.error("ExecutorService shutdown timed out; there are still tasks executing")
            }
        } catch (e: InterruptedException) {
            //LOGGER.error("Timeout when disposing work runner", e)
        }*/

    }

    companion object {

        //private val LOGGER = LoggerFactory.getLogger(ExecutorServiceWorkRunner::class.java)//TODO:Logger
    }
}

