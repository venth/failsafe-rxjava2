package com.github.venth.failsafe.rxjava2.observation;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import net.jodah.failsafe.CircuitBreaker;

class ObservationActivated<T> implements ObservationState<T> {

    private final Disposable disposable;

    private final Observer<T> observer;

    private final CircuitBreaker circuitBreaker;

    ObservationActivated(Disposable disposable, Observer<T> observer, CircuitBreaker circuitBreaker) {

        this.disposable = disposable;
        this.observer = observer;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public ObservationState<T> activate(Disposable disposable) {
        return this;
    }

    @Override
    public void onNext(T t) {
        observer.onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        circuitBreaker.recordFailure(e);
        observer.onError(e);
    }

    @Override
    public void onComplete() {
        circuitBreaker.recordSuccess();
        observer.onComplete();
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
