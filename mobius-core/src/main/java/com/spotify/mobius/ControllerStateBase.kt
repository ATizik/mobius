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
 * Note: synchronization has to be provided externally, states do not protect themselves from issues
 * related state switching. Use ControllerActions to interact with MobiusControllerActions for any
 * asynchronous action and never call one on-method from another directly.
 */
internal abstract class ControllerStateBase<M, E> {

    protected abstract val stateName: String

    open val isRunning: Boolean
        get() = false

    open fun onConnect(view: Connectable<M, E>) {
        throw IllegalStateException(
                "cannot call connect when in the $stateName state")
    }

    open fun onDisconnect() {
        throw IllegalStateException(
                "cannot call disconnect when in the $stateName state")
    }

    open fun onStart() {
        throw IllegalStateException(
                "cannot call start when in the $stateName state")
    }

    open fun onStop() {
        throw IllegalStateException(
                "cannot call stop when in the $stateName state")
    }

    open fun onReplaceModel(model: M) {
        throw IllegalStateException(
                "cannot call replaceModel when in the $stateName state")
    }

    abstract fun onGetModel(): M

    open fun onDispatchEvent(event: E) {
        /*LOGGER.debug(
                "Dropping event that was dispatched when the program was in the {} state: {}",
                stateName,
                event)*///TODO:Logger
    }

    open fun onUpdateView(model: M) {
        /*LOGGER.debug(
                "Dropping model that was dispatched when the program was in the {} state: {}",
                stateName,
                model)*///TODO:Logger
    }

    companion object {

        //private val LOGGER = LoggerFactory.getLogger(ControllerStateBase::class.java)//TODO:Logger
    }
}
