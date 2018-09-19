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



import com.spotify.mobius.functions.Consumer
import kotlin.jvm.Synchronized


/**
 * Processes events and emits effects and models as a result of that.
 *
 * @param <M> model type
 * @param <E> event type
 * @param <F> effect descriptor type
</F></E></M> */
internal class EventProcessor<M:Any, E, F>(
        store: MobiusStore<M, E, F>, effectConsumer: Consumer<F>, modelConsumer: Consumer<M>) {

    private val store: MobiusStore<M, E, F> = checkNotNull(store)
    private val effectConsumer: Consumer<F> = checkNotNull(effectConsumer)
    private val modelConsumer: Consumer<M> = checkNotNull(modelConsumer)

    // concurrency note: the two below fields are only read and written in synchronized sections,
    // hence no need for further coordination.
    private val eventsReceivedBeforeInit = arrayListOf<E>()
    private var initialised = false

    @Synchronized
    fun init() {
        if (initialised) {
            throw IllegalStateException("already initialised")
        }

        val first = store.init()

        dispatchModel(first.model)
        dispatchEffects(first.effects)

        initialised = true
        for (event in eventsReceivedBeforeInit) {
            update(event)
        }
    }

    @Synchronized
    fun update(event: E) {
        if (!initialised) {
            eventsReceivedBeforeInit.add(event)
            return
        }

        val next = store.update(event)

        next.ifHasModel(
                object : Consumer<M> {
                    override fun accept(model: M) {
                        dispatchModel(model)
                    }
                })
        dispatchEffects(next.effects)
    }

    private fun dispatchModel(model: M) {
        modelConsumer.accept(model)
    }

    private fun dispatchEffects(effects: Iterable<F>) {
        for (effect in effects) {
            effectConsumer.accept(effect)
        }
    }

    /**
     * Factory for event processors.
     *
     * @param <M> model type
     * @param <E> event type
     * @param <F> effect descriptor type
    </F></E></M> */
    internal class Factory<M:Any, E, F>(store: MobiusStore<M, E, F>) {

        private val store: MobiusStore<M, E, F> = checkNotNull(store)

        fun create(effectConsumer: Consumer<F>, modelConsumer: Consumer<M>): EventProcessor<M, E, F> {
            return EventProcessor(store, checkNotNull(effectConsumer), checkNotNull(modelConsumer))
        }
    }
}
