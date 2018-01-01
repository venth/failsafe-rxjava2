package com.github.venth.failsafe.reactivex.singleobservation;

import io.reactivex.disposables.Disposable;

public class ObservationDisposed<T> implements ObservationState<T> {

    @Override
    public ObservationState<T> activate(Disposable disposable) {
        return this;
    }

    @Override
    public void onSuccess(T t) {

    }

    @Override
    public void onError(Throwable e) {

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
