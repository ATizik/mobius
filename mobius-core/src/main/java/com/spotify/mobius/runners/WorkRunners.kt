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



import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Job

/**
 * Interface for posting runnables to be executed on a thread. The runnables must all be executed on
 * the same thread for a given WorkRunner.
 */
object WorkRunners {

    fun immediate(): WorkRunner {
        return ImmediateWorkRunner()
    }

    /*fun singleThread(): WorkRunner {
        return from(Executors.newSingleThreadExecutor())
    }

    fun fixedThreadPool(n: Int): WorkRunner {
        return from(Executors.newFixedThreadPool(n))
    }

    fun cachedThreadPool(): WorkRunner {
        return from(Executors.newCachedThreadPool())
    }*/

    fun from(coroutineScope: CoroutineDispatcher, job: Job): WorkRunner {
        return CoroutineWorkRunner(coroutineScope,job)
    }
}
