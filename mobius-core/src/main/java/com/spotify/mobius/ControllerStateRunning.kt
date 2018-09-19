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

internal class ControllerStateRunning<M:Any, E:Any, F:Any>(
        private val actions: ControllerActions<M, E>,
        private val renderer: Connection<M>,
        loopFactory: MobiusLoop.Factory<M, E, F>,
        private val startModel: M) : ControllerStateBase<M, E>() {
    private val loop: MobiusLoop<M, E, F> = loopFactory.startFrom(startModel)

    override val stateName: String
        get() = "running"

    override val isRunning: Boolean
        get() = true

    fun start() {
        loop.observe(
                object : Consumer<M> {
                    override fun accept(model: M) {
                        actions.postUpdateView(model)
                    }
                })
    }

    override fun onDispatchEvent(event: E) {
        loop.dispatchEvent(event)
    }

    override fun onUpdateView(model: M) {
        renderer.accept(model)
    }

    override fun onStop() {
        loop.dispose()
        val mostRecentModel = loop.mostRecentModel
        actions.goToStateCreated(renderer, mostRecentModel)
    }

    override fun onGetModel(): M {
        val model = loop.mostRecentModel
        return model ?: startModel
    }
}
