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


import com.spotify.mobius.internal_util.ImmutableUtil

/** Defines the entry into the initial state of a Mobius loop.  */

data class First<M, F>(val model: M,val effects: Set<F>) {

    /** @property model the initial model to use
     */

    fun model() = model
    fun effects() = effects

    /** @property effects the possibly empty set of effects to initially dispatch
     */


    /** Check if this First contains effects  */
    fun hasEffects(): Boolean {
        return !effects.isEmpty()
    }

    companion object {

        /**
         * Create a [First] with the provided model and no initial effects.
         *
         * @param model the model to initialize the loop with
         * @param <M> the model type
         * @param <F> the effect type
        </F></M> */
        fun <M, F> first(model: M): First<M, F> {
            return First(model, ImmutableUtil.emptySet<F>())
        }

        /**
         * Create a [First] with the provided model and the supplied initial effects.
         *
         * @param model the model to initialize the loop with
         * @param <M> the model type
         * @param <F> the effect type
        </F></M> */
        fun <M, F> first(model: M, effects: Set<F>): First<M, F> {
            return First(model, effects)
        }
    }
}
