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

/**
 * Interface for posting runnables to be executed on a thread. The runnables must all be executed on
 * the same thread for a given WorkRunner.
 */
object WorkRunners {

    fun immediate(): WorkRunner {
        return ImmediateWorkRunner()
    }

    fun sequential(): WorkRunner {
        val job = Job()


        return from(job, 1)
    }

    fun cachedFixedPool(capacity: Int = 8): WorkRunner {
        val job = Job()

        return from(job,8)
    }


    fun from(job: Job, capacity: Int): WorkRunner {
        return CoroutineWorkRunner(job, capacity)
    }
}

