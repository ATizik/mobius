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
package com.spotify.mobius.rx2;

import com.spotify.mobius.Connectable;
import com.spotify.mobius.Connection;
import com.spotify.mobius.ConnectionLimitExceededException;
import com.spotify.mobius.Mobius;
import com.spotify.mobius.MobiusLoop;
import com.spotify.mobius.Next;
import com.spotify.mobius.Update;
import com.spotify.mobius.functions.Consumer;
import com.spotify.mobius.test.SimpleConnection;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.junit.Before;
import org.junit.Test;

public class RxMobiusLoopTest {
  private RxMobiusLoop<Integer, String> loop;

  @Before
  public void setUp() throws Exception {
    MobiusLoop.Factory<String, Integer, Boolean> factory =
        Mobius.INSTANCE.loop(
            new Update<String, Integer, Boolean>() {
              @Nonnull
              @Override
              public Next<String, Boolean> update(String model, Integer event) {
                return Next.Companion.next(model + event.toString());
              }
            },
            new Connectable<Boolean, Integer>() {
              @Nonnull
              @Override
              public Connection<Boolean> connect(Consumer<Integer> output)
                  throws ConnectionLimitExceededException {
                return new SimpleConnection<Boolean>() {
                  @Override
                  public void accept(Boolean value) {
                    // no implementation, no effects will happen
                  }
                };
              }
            });

    loop = new RxMobiusLoop<>(factory, "");
  }

  @Test
  public void shouldPropagateIncomingErrorsAsUnrecoverable() throws Exception {
    PublishSubject<Integer> input = PublishSubject.create();

    TestObserver<String> subscriber = input.compose(loop).test();

    Exception expected = new RuntimeException("expected");

    input.onError(expected);

    subscriber.awaitTerminalEvent(1, TimeUnit.SECONDS);
    subscriber.assertError(new UnrecoverableIncomingException(expected));
  }
}
