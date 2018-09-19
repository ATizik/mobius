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

internal class ControllerStateCreated<M, E, F>(
        private val actions: ControllerActions<M, E>, private val renderer: Connection<M>, private var nextModelToStartFrom: M) : ControllerStateBase<M, E>() {

    override val stateName: String
        get() = "created"

    override fun onDisconnect() {
        renderer.dispose()
        actions.goToStateInit(nextModelToStartFrom)
    }

    override fun onStart() {
        actions.goToStateRunning(renderer, nextModelToStartFrom)
    }

    override fun onReplaceModel(model: M) {
        nextModelToStartFrom = model
    }

    override fun onGetModel(): M {
        return nextModelToStartFrom
    }
}
