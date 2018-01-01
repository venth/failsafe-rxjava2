package com.github.venth.failsafe.rxjava2.observation;

import io.reactivex.disposables.Disposable;

public class ObservationDisposed<T> implements ObservationState<T> {

    @Override
    public ObservationState<T> activate(Disposable disposable) {
        return this;
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public ObservationState<T> dispose() {
        return this;
    }

    @Override
    public boolean isDisposed() {
        return true;
    }
}
