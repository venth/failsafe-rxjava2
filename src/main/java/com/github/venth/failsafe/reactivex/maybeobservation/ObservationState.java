package com.github.venth.failsafe.reactivex.maybeobservation;

import io.reactivex.disposables.Disposable;

interface ObservationState<T> {

    ObservationState<T> activate(Disposable disposable);

    void onSuccess(T t);

    void onError(Throwable e);

    ObservationState<T> dispose();

    boolean isDisposed();

    void onComplete();
}
