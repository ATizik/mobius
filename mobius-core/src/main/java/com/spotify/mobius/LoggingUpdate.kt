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

class LoggingUpdate<M:Any, E:Any, F>(actualUpdate: Update<M, E, F>, logger: MobiusLoop.Logger<M, E, F>) : Update<M, E, F> {

    private val actualUpdate: Update<M, E, F> = checkNotNull(actualUpdate)
    private val logger: MobiusLoop.Logger<M, E, F> = checkNotNull(logger)

    override fun update(model: M, event: E): Next<M, F> {
        logger.beforeUpdate(model, event)
        val result = safeInvokeUpdate(model, event)
        logger.afterUpdate(model, event, result)
        return result
    }

    private fun safeInvokeUpdate(model: M, event: E): Next<M, F> {
        try {
            return actualUpdate.update(model, event)
        } catch (e: Exception) {
            logger.exceptionDuringUpdate(model, event, e)
            throw Throwables.propagate(e)
        }

    }
}
