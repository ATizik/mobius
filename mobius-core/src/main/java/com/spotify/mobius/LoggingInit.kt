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


import com.spotify.mobius.internal_util.Throwables

internal class LoggingInit<M:Any, F>(actualInit: Init<M, F>, logger: MobiusLoop.Logger<M, *, F>) : Init<M, F> {

    private val actualInit: Init<M, F> = checkNotNull(actualInit)
    private val logger: MobiusLoop.Logger<M, *, F> = checkNotNull(logger)

    override fun init(model: M): First<M, F> {
        logger.beforeInit(model)
        val result = safeInvokeInit(model)
        logger.afterInit(model, result)
        return result
    }

    private fun safeInvokeInit(model: M): First<M, F> {
        try {
            return actualInit.init(model)
        } catch (e: Exception) {
            logger.exceptionDuringInit(model, e)
            throw Throwables.propagate(e)
        }

    }
}
