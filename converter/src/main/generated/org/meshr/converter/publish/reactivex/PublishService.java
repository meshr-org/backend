/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.meshr.converter.publish.reactivex;

import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


@io.vertx.lang.rx.RxGen(org.meshr.converter.publish.PublishService.class)
public class PublishService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PublishService that = (PublishService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.rx.TypeArg<PublishService> __TYPE_ARG = new io.vertx.lang.rx.TypeArg<>(    obj -> new PublishService((org.meshr.converter.publish.PublishService) obj),
    PublishService::getDelegate
  );

  private final org.meshr.converter.publish.PublishService delegate;
  
  public PublishService(org.meshr.converter.publish.PublishService delegate) {
    this.delegate = delegate;
  }

  public PublishService(Object delegate) {
    this.delegate = (org.meshr.converter.publish.PublishService)delegate;
  }

  public org.meshr.converter.publish.PublishService getDelegate() {
    return delegate;
  }

  public org.meshr.converter.publish.reactivex.PublishService publish(JsonObject message, Handler<AsyncResult<JsonObject>> resultHandler) { 
    delegate.publish(message, resultHandler);
    return this;
  }

  public Single<JsonObject> rxPublish(JsonObject message) { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      publish(message, handler);
    });
  }

  public static PublishService newInstance(org.meshr.converter.publish.PublishService arg) {
    return arg != null ? new PublishService(arg) : null;
  }

}
