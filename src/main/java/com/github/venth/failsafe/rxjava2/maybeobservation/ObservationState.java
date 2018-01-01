package com.github.venth.failsafe.rxjava2.maybeobservation;

import io.reactivex.disposables.Disposable;

interface ObservationState<T> {

    ObservationState<T> activate(Disposable disposable);

    void onSuccess(T t);

    void onError(Throwable e);

    ObservationState<T> dispose();

    boolean isDisposed();

    void onComplete();
}
