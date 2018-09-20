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



/**
 * Indicates that a [Connectable] connection caused an unhandled exception.
 *
 *
 * This is a programmer error - connections must never throw unhandled exceptions, instead the
 * connection is expected to catch the exception and emit a special error value to its output
 * consumer.
 *
 *
 * An example of this would be that if loading an HTTP request throws an exception, then the
 * connection should emit a LoadingDataFailed event to communicate the failure to [Update].
 */
class ConnectionException(private val effect: Any, throwable: Throwable) : RuntimeException(checkNotNull(effect).toString(), throwable) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null /*|| javaClass != o.javaClass//TODO:correct check*/) return false

        val that = o as ConnectionException?

        return effect == that!!.effect
    }

    override fun hashCode(): Int {
        return effect.hashCode()
    }
}
