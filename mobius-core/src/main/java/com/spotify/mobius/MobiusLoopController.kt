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
import com.spotify.mobius.internal_util.*
import com.spotify.mobius.runners.WorkRunner
import kotlinx.coroutines.experimental.Runnable
import kotlin.jvm.Synchronized

internal class MobiusLoopController<M:Any, E:Any, F:Any>(
        loopFactory: MobiusLoop.Factory<M, E, F>, defaultModel: M, mainThreadRunner: WorkRunner) : MobiusLoop.Controller<M, E>, ControllerActions<M, E> {


    private val loopFactory: MobiusLoop.Factory<M, E, F> = checkNotNull(loopFactory)
    private val defaultModel: M = checkNotNull(defaultModel)
    private val mainThreadRunner: WorkRunner = checkNotNull(mainThreadRunner)

    private var currentState: ControllerStateBase<M, E>? = null

    init {

        goToStateInit(defaultModel)
    }


    @Synchronized
    private fun dispatchEvent(event: E) {
        currentState!!.onDispatchEvent(event)
    }

    @Synchronized
    private fun updateView(model: M) {
        currentState!!.onUpdateView(model)
    }

    @Synchronized
    override fun connect(view: Connectable<M, E>) {
        currentState!!.onConnect(checkNotNull(view))
    }

    @Synchronized
    override fun disconnect() {
        currentState!!.onDisconnect()
    }

    @Synchronized
    override fun start() {
        currentState!!.onStart()
    }

    override val isRunning: Boolean
        @Synchronized get() = currentState!!.isRunning

    override val model: M
        @Synchronized get() = currentState!!.onGetModel()

    @Synchronized
    override fun stop() {
        currentState!!.onStop()
    }

    @Synchronized
    override fun replaceModel(model: M) {
        checkNotNull<Any>(model)
        currentState!!.onReplaceModel(model)
    }

    override fun postUpdateView(model: M) {
        mainThreadRunner.post (Runnable {  updateView(model) })
    }

    @Synchronized
    override fun goToStateInit(nextModelToStartFrom: M) {
        currentState = ControllerStateInit<M, E, Any>(this, nextModelToStartFrom)
    }

    @Synchronized
    override fun goToStateCreated(
            renderer: Connection<M>, nextModelToStartFrom: M?) {
        var nextModelToStartFrom = nextModelToStartFrom

        if (nextModelToStartFrom == null) {
            nextModelToStartFrom = defaultModel
        }

        currentState = ControllerStateCreated<M, E, F>(this, renderer, nextModelToStartFrom!!)
    }

    override fun goToStateCreated(view: Connectable<M, E>, nextModelToStartFrom: M) {

        val safeModelHandler = SafeConnectable(checkNotNull(view))

        val modelConnection = safeModelHandler.connect(
                object : Consumer<E> {
                    override fun accept(event: E) {
                        dispatchEvent(event)
                    }
                })

        goToStateCreated(checkNotNull(modelConnection), nextModelToStartFrom)
    }

    @Synchronized
    override fun goToStateRunning(renderer: Connection<M>, nextModelToStartFrom: M) {
        val stateRunning = ControllerStateRunning(this, renderer, loopFactory, nextModelToStartFrom)

        currentState = stateRunning

        stateRunning.start()
    }
}
