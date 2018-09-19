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


/**
 * An [EventSource] that merges multiple sources into one
 *
 * @param <E> The type of Events the sources will emit
</E> */
class MergedEventSource<E> private constructor(private val eventSources: List<EventSource<E>>) : EventSource<E>() {

    override fun subscribe(eventConsumer: Consumer<E>): Disposable {
        val disposables = eventSources.map { it.subscribe(eventConsumer) }

        return object : Disposable {
            override fun dispose() {
                for (disposable in disposables) {
                    disposable.dispose()
                }
            }
        }
    }

    companion object {

        //@SafeVarargs
        fun <E> from(
                eventSource: EventSource<E>, vararg eventSources: EventSource<E>): EventSource<E> {
            val allSources = eventSources.toList() + eventSource
            return MergedEventSource(allSources)
        }
    }
}
