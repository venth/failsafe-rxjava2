package com.github.venth.failsafe.reactivex.observation;

import io.reactivex.disposables.Disposable;

interface ObservationState<T> {

    ObservationState<T> activate(Disposable disposable);

    void onNext(T t);

    void onError(Throwable e);

    void onComplete();

    ObservationState<T> dispose();

    boolean isDisposed();
}
