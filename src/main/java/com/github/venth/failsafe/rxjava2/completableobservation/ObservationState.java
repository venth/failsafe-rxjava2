package com.github.venth.failsafe.rxjava2.completableobservation;

import io.reactivex.disposables.Disposable;

interface ObservationState {

    ObservationState activate(Disposable disposable);

    void onError(Throwable e);

    ObservationState dispose();

    boolean isDisposed();

    void onComplete();
}
