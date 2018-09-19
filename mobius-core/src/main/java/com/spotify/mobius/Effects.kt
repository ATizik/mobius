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


/** Utility class for working with effects.  */
object Effects {

    /**
     * Convenience method for instantiating a set of effects. Note that this returns a mutable set of
     * effects to avoid creating too many copies - the set will normally be added to a [Next] or
     * [First], leading to another safe-copy being made.
     *
     * @return a *mutable* set of effects
     */
    //@SafeVarargs
    // implementation note: the type signature of this method helps ensure that you can get a set of a
    // super type even if you only submit items of a sub type. Hence the 'G extends F' type parameter.
    fun <F:Any, G : F> effects(vararg effects: G): Set<F> {
        return effects.toHashSet()
    }
}// prevent instantiation
