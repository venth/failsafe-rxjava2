package com.github.venth.failsafe.rxjava2.maybeobservation;

import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import net.jodah.failsafe.CircuitBreaker;

class ObservationActivated<T> implements ObservationState<T> {

    private final Disposable disposable;

    private final MaybeObserver<T> observer;

    private final CircuitBreaker circuitBreaker;

    ObservationActivated(Disposable disposable, MaybeObserver<T> observer, CircuitBreaker circuitBreaker) {

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

    @Override
    public void onComplete() {
        circuitBreaker.recordSuccess();
        observer.onComplete();
    }
}
