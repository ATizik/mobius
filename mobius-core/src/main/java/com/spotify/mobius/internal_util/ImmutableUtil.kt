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
package com.spotify.mobius.internal_util


/**
 * Defines static utility methods that help working with immutable collections. NOT FOR EXTERNAL
 * USE; this class is not a part of the Mobius API and backwards-incompatible changes may happen
 * between releases. If you want to use methods defined here, make your own copy.
 */
object ImmutableUtil {

    fun <T> emptySet(): Set<T> {
        return kotlin.collections.emptySet()
    }

    //@SafeVarargs
    fun <T> setOf(vararg items: T): Set<T> {
        checkArrayNoNulls(items)

        return setOf(*items)
    }

    fun <T> immutableSet(set: Set<T>): Set<T> {
        checkIterableNoNulls(set)

        return set.toSet()
    }

    //@SafeVarargs
    fun <T> unionSets(vararg sets: Set<T>): Set<T> {
        //checkNotNull<Array<Set<T>>>(sets as Array<Set<T>>)

        return sets.flatMap { checkIterableNoNulls(it) }.toSet()
    }
}
