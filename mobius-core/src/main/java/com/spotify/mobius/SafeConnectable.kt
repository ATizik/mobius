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



import com.spotify.mobius.disposables.CompositeDisposable
import com.spotify.mobius.functions.Consumer
import kotlin.jvm.Synchronized

/**
 * A [Connectable] that ensures that an inner [Connection] doesn't emit or receive any
 * values after being disposed.
 *
 *
 * This only acts as a safeguard, you still need to make sure that the Connectable disposes of
 * resources correctly.
 */
internal class SafeConnectable<F, E>(actual: Connectable<F, E>) : Connectable<F, E> {

    private val actual: Connectable<F, E> = checkNotNull(actual)


    override fun connect(output: Consumer<E>): Connection<F> {
        val safeEventConsumer = SafeConsumer(checkNotNull(output))
        val effectConsumer = SafeEffectConsumer(checkNotNull(actual.connect(safeEventConsumer)))
        val disposable = CompositeDisposable.from(safeEventConsumer, effectConsumer)
        return object : Connection<F> {
            @Synchronized
            override fun accept(effect: F) {
                effectConsumer.accept(effect)
            }

            @Synchronized
            override fun dispose() {
                disposable.dispose()
            }
        }
    }

    class SafeEffectConsumer<F> constructor(private val actual: Connection<F>) : Connection<F> {
        private var disposed: Boolean = false

        @Synchronized
        override fun accept(effect: F) {
            if (disposed) {
                return
            }
            actual.accept(effect)
        }

        @Synchronized
        override fun dispose() {
            disposed = true
            actual.dispose()
        }
    }

    class SafeConsumer<E> constructor(private val actual: Consumer<E>) : Connection<E> {
        private var disposed: Boolean = false

        @Synchronized
        override fun accept(value: E) {
            if (disposed) {
                return
            }
            actual.accept(value)
        }

        @Synchronized
        override fun dispose() {
            disposed = true
        }
    }
}
