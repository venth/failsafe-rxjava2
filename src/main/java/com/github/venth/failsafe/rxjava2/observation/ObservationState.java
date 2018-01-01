package com.github.venth.failsafe.rxjava2.observation;

import io.reactivex.disposables.Disposable;

interface ObservationState<T> {

    ObservationState<T> activate(Disposable disposable);

    void onNext(T t);

    void onError(Throwable e);

    void onComplete();

    ObservationState<T> dispose();

    boolean isDisposed();
}
