/*
 * Copyright (C) 2013 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.jira.rest.client.internal.async;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.util.concurrent.Effect;
import com.atlassian.util.concurrent.Promise;
import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
* This class delegates all calls to given delegate Promise. Additionally it throws new RestClientException
 * with original RestClientException given as a cause, which gives a more useful stack trace.
*/
public class DelegatingPromise<T> implements Promise<T> {

	private final Promise<T> delegate;

	public DelegatingPromise(Promise<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public T claim() {
		try {
			return delegate.claim();
		} catch (RestClientException e) {
			throw new RestClientException(e);
		}
	}

	public Promise<T> done(Effect<T> e) {
		return delegate.done(e);
	}

	@Override
	public Promise<T> fail(Effect<Throwable> e) {
		return delegate.fail(e);
	}

	public Promise<T> then(FutureCallback<T> callback) {
		return delegate.then(callback);
	}

	public <B> Promise<B> map(Function<? super T, ? extends B> function) {
		return delegate.map(function);
	}

	public <B> Promise<B> flatMap(Function<? super T, Promise<B>> function) {
		return delegate.flatMap(function);
	}

	public Promise<T> recover(Function<Throwable, ? extends T> handleThrowable) {
		return delegate.recover(handleThrowable);
	}

	public <B> Promise<B> fold(Function<Throwable, ? extends B> handleThrowable, Function<? super T, ? extends B> function) {
		return delegate.fold(handleThrowable, function);
	}

	@Override
	public void addListener(Runnable listener, Executor executor) {
		delegate.addListener(listener, executor);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return delegate.cancel(mayInterruptIfRunning);
	}

	@Override
	public boolean isCancelled() {
		return delegate.isCancelled();
	}

	@Override
	public boolean isDone() {
		return delegate.isDone();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return delegate.get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return delegate.get(timeout, unit);
	}
}
