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



import com.spotify.mobius.disposables.Disposable
import com.spotify.mobius.functions.Consumer
import com.spotify.mobius.functions.Producer
import com.spotify.mobius.runners.WorkRunner
import com.spotify.mobius.runners.WorkRunners
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

object Mobius {

    private val NOOP_INIT = O1()

    class O1 : Init<Any, Any> {
        override fun init(model: Any): First<Any, Any> {
            return First.first(model)
        }
    }

    private val NOOP_EVENT_SOURCE = O()

    class O : EventSource<Any>() {
        override fun subscribe(eventConsumer: Consumer<Any>): Disposable {
            return object : Disposable {
                override fun dispose() {}
            }
        }

    }

    private val NOOP_LOGGER = O2()

    class O2 : MobiusLoop.Logger<Any, Any, Any> {
        override fun beforeInit(model: Any) {
            /* noop */
        }

        override fun afterInit(model: Any, result: First<Any, Any>) {
            /* noop */
        }

        override fun exceptionDuringInit(model: Any, exception: Throwable) {
           // System.err.println("error initialising from model: '$model' - $exception")
           // exception.printStackTrace(System.err)
        }

        override fun beforeUpdate(model: Any, event: Any) {
            /* noop */
        }

        override fun afterUpdate(model: Any, event: Any, result: Next<Any, Any>) {
            /* noop */
        }

        override fun exceptionDuringUpdate(model: Any, event: Any, exception: Throwable) {
            //System.err.println(
            //        "error updating model: '$model' with event: '$event' - $exception")
            //exception.printStackTrace(System.err)
        }
    }

    /**
     * Create a [MobiusLoop.Builder] to help you configure a MobiusLoop before starting it.
     *
     *
     * Once done configuring the loop you can start the loop using [ ][MobiusLoop.Factory.startFrom].
     *
     * @param update the [Update] function of the loop
     * @param effectHandler the [Connectable] effect handler of the loop
     * @return a [MobiusLoop.Builder] instance that you can further configure before starting
     * the loop
     */
    fun <M:Any, E:Any, F:Any> loop(
            update: Update<M, E, F>, effectHandler: Connectable<F, E>): MobiusLoop.Builder<M, E, F> {


        return Builder(
                update,
                effectHandler,
                NOOP_INIT as Init<M, F>,
                NOOP_EVENT_SOURCE as EventSource<E>,
                NOOP_LOGGER as MobiusLoop.Logger<M, E, F>,
                object : Producer<WorkRunner> {
                    override fun get(): WorkRunner {
                        val job = Job()
                        val list = mutableListOf<Runnable>()
                        return WorkRunners.from(object :CoroutineDispatcher() {
                            init {
                                (job as CoroutineScope).launch {
                                    while (job.isActive) {
                                        job.joinChildren()
                                        list.removeAt(1).run()
                                    }
                                }
                            }
                            override fun dispatch(context: CoroutineContext, block: Runnable) {
                                list+=block
                            }
                        },job)//Executors.newSingleThreadExecutor(Builder.THREAD_FACTORY))
                    }
                },
                object : Producer<WorkRunner> {
                    val job = Job()
                    override fun get(): WorkRunner {
                        return WorkRunners.from(Dispatchers.Default, job)
                    }
                })
    }

    /**
     * Create a [MobiusLoop.Controller] that allows you to start, stop, and restart MobiusLoops.
     *
     * @param loopFactory a factory for creating loops
     * @param defaultModel the model the controller should start from
     * @return a new controller
     */
    fun <M:Any, E:Any, F:Any> controller(
            loopFactory: MobiusLoop.Factory<M, E, F>, defaultModel: M): MobiusLoop.Controller<M, E> {
        return MobiusLoopController(loopFactory, defaultModel, WorkRunners.immediate())
    }

    /**
     * Create a [MobiusLoop.Controller] that allows you to start, stop, and restart MobiusLoops.
     *
     * @param loopFactory a factory for creating loops
     * @param defaultModel the model the controller should start from
     * @param modelRunner the WorkRunner to use when observing model changes
     * @return a new controller
     */
    fun <M:Any, E:Any, F:Any> controller(
            loopFactory: MobiusLoop.Factory<M, E, F>, defaultModel: M, modelRunner: WorkRunner): MobiusLoop.Controller<M, E> {
        return MobiusLoopController(loopFactory, defaultModel, modelRunner)
    }

    private class Builder<M:Any, E:Any, F:Any> constructor(
            update: Update<M, E, F>,
            effectHandler: Connectable<F, E>,
            init: Init<M, F>,
            eventSource: EventSource<E>,
            logger: MobiusLoop.Logger<M, E, F>,
            eventRunner: Producer<WorkRunner>,
            effectRunner: Producer<WorkRunner>) : MobiusLoop.Builder<M, E, F> {

        private val update: Update<M, E, F> = checkNotNull(update)
        private val effectHandler: Connectable<F, E> = checkNotNull(effectHandler)
        private val init: Init<M, F> = checkNotNull(init)
        private val eventSource: EventSource<E> = checkNotNull(eventSource)
        private val eventRunner: Producer<WorkRunner> = checkNotNull(eventRunner)
        private val effectRunner: Producer<WorkRunner> = checkNotNull(effectRunner)
        private val logger: MobiusLoop.Logger<M, E, F> = checkNotNull(logger)

        override fun init(init: Init<M, F>): MobiusLoop.Builder<M, E, F> {
            return Builder(
                    update, effectHandler, init, eventSource, logger, eventRunner, effectRunner)
        }

        override fun eventSource(eventSource: EventSource<E>): MobiusLoop.Builder<M, E, F> {
            return Builder(
                    update, effectHandler, init, eventSource, logger, eventRunner, effectRunner)
        }

        override fun eventSources(
                eventSource: EventSource<E>, vararg eventSources: EventSource<E>): MobiusLoop.Builder<M, E, F> {
            val mergedSource = MergedEventSource.from(eventSource, *eventSources)
            return Builder(
                    update, effectHandler, init, mergedSource, logger, eventRunner, effectRunner)
        }

        override fun logger(logger: MobiusLoop.Logger<M, E, F>): MobiusLoop.Builder<M, E, F> {
            return Builder(
                    update, effectHandler, init, eventSource, logger, eventRunner, effectRunner)
        }

        override fun eventRunner(eventRunner: Producer<WorkRunner>): MobiusLoop.Builder<M, E, F> {
            return Builder(
                    update, effectHandler, init, eventSource, logger, eventRunner, effectRunner)
        }

        override fun effectRunner(effectRunner: Producer<WorkRunner>): MobiusLoop.Builder<M, E, F> {
            return Builder(
                    update, effectHandler, init, eventSource, logger, eventRunner, effectRunner)
        }

        override fun startFrom(startModel: M): MobiusLoop<M, E, F> {
            val loggingInit = LoggingInit(init, logger)
            val loggingUpdate = LoggingUpdate(update, logger)

            return MobiusLoop.create(
                    MobiusStore.create(loggingInit, loggingUpdate, checkNotNull(startModel)),
                    effectHandler,
                    eventSource,
                    checkNotNull(eventRunner.get()),
                    checkNotNull(effectRunner.get()))
        }


    }
}// prevent instantiation
