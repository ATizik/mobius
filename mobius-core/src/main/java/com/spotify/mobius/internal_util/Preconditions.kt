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
 * Contains utilities similar to ones found in Guava's Preconditions class. NOT FOR EXTERNAL USE;
 * this class is not a part of the Mobius API and backwards-incompatible changes may happen between
 * releases. If you want to use methods defined here, make your own copy.
 */



    fun <T> checkNotNull(input: T?): T {
        if (input == null) {
            throw NullPointerException()
        }

        return input
    }

    fun <T> checkArrayNoNulls(input: Array<T>): Array<T> {
        checkNotNull(input)

        for (value in input) {
            checkNotNull(value)
        }

        return input
    }

    fun <I : Iterable<T>, T> checkIterableNoNulls(input: I): I {
        checkNotNull(input)

        for (value in input) {
            checkNotNull(value)
        }

        return input
    }

