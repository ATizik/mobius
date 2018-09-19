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
import com.spotify.mobius.internal_util.ImmutableUtil


/**
 * This class represents the result of calling an [Update] function.
 *
 *
 * Upon calling an Update function with an Event and Model, a Next object will be returned that
 * contains the new Model (if there is one) and Effect objects that describe which side-effects
 * should take place.
 */

data class Next<M:Any, F> (val model: M?,val effects: Set<F>) {



    /** Check if this Next contains a model.  */
    fun hasModel(): Boolean {
        return model != null
    }

    /**
     * Get the effects of this Next.
     * @param effects
     *
     * Will return an empty set if there are no effects.
     */
    fun effects() = effects


    /** Check if this Next contains effects.  */
    fun hasEffects(): Boolean {
        return !effects.isEmpty()
    }

    /**
     * Try to get the model from this Next, with a fallback if there isn't one.
     *
     * @param fallbackModel the default model to use if the Next doesn't have a model
     */
    fun modelOrElse(fallbackModel: M): M {
        checkNotNull(fallbackModel)
        return if (hasModel()) {
            modelUnsafe()
        } else {
            fallbackModel
        }
    }

    /**
     * Get the model of this Next. This version is unsafe - if this next doesn't have a model, calling
     * this method will cause an exception to be thrown.
     *
     *
     * In almost all cases you should use [.modelOrElse] or [.ifHasModel] instead.
     *
     * @throws NoSuchElementException if this Next has no model
     */
    fun modelUnsafe(): M {
        if (!hasModel()) {
            throw NoSuchElementException("there is no model in this Next<>")
        }

        // we know model is never null here since we just checked it.

        return model!!
    }

    /** If the model is present, call the given consumer with it, otherwise do nothing.  */
    fun ifHasModel(consumer: Consumer<M>) {
        checkNotNull(consumer)
        if (hasModel()) {
            consumer.accept(modelUnsafe())
        }
    }

    companion object {

        ////////////////////////////////////////////////////////////////////////////////////////////////

        /** Create a Next that updates the model and dispatches the supplied set of effects.  */
        fun <M:Any, F> next(model: M, effects: Set<F>): Next<M, F> {
            return Next(model, ImmutableUtil.immutableSet(effects))
        }

        /** Create a Next that updates the model but dispatches no effects.  */
        fun <M:Any, F> next(model: M): Next<M, F> {
            return Next(model, ImmutableUtil.emptySet<F>())
        }

        /** Create a Next that doesn't update the model but dispatches the supplied effects.  */
        fun <M:Any, F> dispatch(effects: Set<F>): Next<M, F> {
            return Next(null, ImmutableUtil.immutableSet(effects))
        }

        /** Create an empty Next that doesn't update the model or dispatch effects.  */
        fun <M:Any, F> noChange(): Next<M, F> {
            return Next(null, ImmutableUtil.emptySet<F>())
        }

        fun <S> effects(vararg s:S) = s.toHashSet()
    }
}

typealias effects<F> = Set<F>