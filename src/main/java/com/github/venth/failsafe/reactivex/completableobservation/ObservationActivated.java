package com.github.venth.failsafe.reactivex.completableobservation;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import net.jodah.failsafe.CircuitBreaker;

class ObservationActivated implements ObservationState {

    private final Disposable disposable;

    private final CompletableObserver observer;

    private final CircuitBreaker circuitBreaker;

    ObservationActivated(Disposable disposable, CompletableObserver observer, CircuitBreaker circuitBreaker) {

        this.disposable = disposable;
        this.observer = observer;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public ObservationState activate(Disposable disposable) {
        return this;
    }

    @Override
    public void onError(Throwable e) {
        circuitBreaker.recordFailure(e);
        observer.onError(e);
    }

    @Override
    public ObservationState dispose() {
        disposable.dispose();
        return new ObservationDisposed();
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @Override
    public void onComplete() {
        circuitBreaker.recordSuccess();
        observer.onComplete();
    }
}
