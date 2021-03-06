package com.github.venth.failsafe.rxjava2.maybeobservation;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import net.jodah.failsafe.CircuitBreaker;

public class CircuitBreakerMaybeObserver<T> implements MaybeObserver<T>, Disposable {

    private final AtomicReference<ObservationState<T>> state;

    public CircuitBreakerMaybeObserver(MaybeObserver<T> observer, CircuitBreaker circuitBreaker) {
        this.state = new AtomicReference<>(new ObservationDeactivated<>(this, observer, circuitBreaker));
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        state.updateAndGet(state -> state.activate(disposable));
    }

    @Override
    public void onSuccess(T t) {
        state.get().onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        state.get().onError(e);
    }

    @Override
    public void onComplete() {
        state.get().onComplete();
    }

    @Override
    public void dispose() {
        state.updateAndGet(ObservationState::dispose);
    }

    @Override
    public boolean isDisposed() {
        return state.get().isDisposed();
    }
}
