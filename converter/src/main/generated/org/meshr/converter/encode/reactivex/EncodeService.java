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

package org.meshr.converter.encode.reactivex;

import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


@io.vertx.lang.rx.RxGen(org.meshr.converter.encode.EncodeService.class)
public class EncodeService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EncodeService that = (EncodeService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.rx.TypeArg<EncodeService> __TYPE_ARG = new io.vertx.lang.rx.TypeArg<>(    obj -> new EncodeService((org.meshr.converter.encode.EncodeService) obj),
    EncodeService::getDelegate
  );

  private final org.meshr.converter.encode.EncodeService delegate;
  
  public EncodeService(org.meshr.converter.encode.EncodeService delegate) {
    this.delegate = delegate;
  }

  public EncodeService(Object delegate) {
    this.delegate = (org.meshr.converter.encode.EncodeService)delegate;
  }

  public org.meshr.converter.encode.EncodeService getDelegate() {
    return delegate;
  }

  public org.meshr.converter.encode.reactivex.EncodeService encode(JsonObject message, Handler<AsyncResult<JsonObject>> resultHandler) { 
    delegate.encode(message, resultHandler);
    return this;
  }

  public Single<JsonObject> rxEncode(JsonObject message) { 
    return io.vertx.reactivex.impl.AsyncResultSingle.toSingle(handler -> {
      encode(message, handler);
    });
  }

  public static EncodeService newInstance(org.meshr.converter.encode.EncodeService arg) {
    return arg != null ? new EncodeService(arg) : null;
  }

}
