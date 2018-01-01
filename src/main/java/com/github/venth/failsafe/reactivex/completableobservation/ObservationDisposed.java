package com.github.venth.failsafe.reactivex.completableobservation;

import io.reactivex.disposables.Disposable;

public class ObservationDisposed implements ObservationState {

    @Override
    public ObservationState activate(Disposable disposable) {
        return this;
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public ObservationState dispose() {
        return this;
    }

    @Override
    public boolean isDisposed() {
        return true;
    }

    @Override
    public void onComplete() {
        
    }
}
