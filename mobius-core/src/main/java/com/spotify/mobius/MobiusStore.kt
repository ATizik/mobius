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


import com.spotify.mobius.internal_util.*
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile

/** Responsible for holding and updating the current model.  */
class MobiusStore<M:Any, E, F> private constructor(init: Init<M, F>, update: Update<M, E, F>, startModel: M) {

    private val init: Init<M, F> = checkNotNull(init)
    private val update: Update<M, E, F> = checkNotNull(update)

    @Volatile
    private var currentModel: M

    init {
        this.currentModel = checkNotNull(startModel)
    }

    @Synchronized
    fun init(): First<M, F> {
        val first = init.init(currentModel)
        currentModel = first.model
        return first
    }

    @Synchronized
    fun update(event: E): Next<M, F> {
        val next = update.update(currentModel, checkNotNull(event))
        currentModel = next.modelOrElse(currentModel)
        return next
    }

    companion object {

        fun <M:Any, E, F> create(
                init: Init<M, F>, update: Update<M, E, F>, startModel: M): MobiusStore<M, E, F> {
            return MobiusStore(init, update, startModel)
        }
    }
}
