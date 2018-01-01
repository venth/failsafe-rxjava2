package com.github.venth.failsafe.reactivex.singleobservation;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import net.jodah.failsafe.CircuitBreaker;

class ObservationActivated<T> implements ObservationState<T> {

    private final Disposable disposable;

    private final SingleObserver<T> observer;

    private final CircuitBreaker circuitBreaker;

    ObservationActivated(Disposable disposable, SingleObserver<T> observer, CircuitBreaker circuitBreaker) {

        this.disposable = disposable;
        this.observer = observer;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public ObservationState<T> activate(Disposable disposable) {
        return this;
    }

    @Override
    public void onSuccess(T t) {
        circuitBreaker.recordSuccess();
        observer.onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        circuitBreaker.recordFailure(e);
        observer.onError(e);
    }

    @Override
    public ObservationState<T> dispose() {
        disposable.dispose();
        return new ObservationDisposed<>();
    }

    @Override
    public boolean isDisposed() {
        return false;
    }
}
