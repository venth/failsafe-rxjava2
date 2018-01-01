package com.github.venth.failsafe.rxjava2.singleobservation;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import net.jodah.failsafe.CircuitBreaker;

public class CircuitBreakerSingleObserver<T> implements SingleObserver<T>, Disposable {

    private final AtomicReference<ObservationState<T>> state;

    public CircuitBreakerSingleObserver(SingleObserver<T> observer, CircuitBreaker circuitBreaker) {
        this.state = new AtomicReference<>(new ObservationDeactivated<>(this, observer, circuitBreaker));
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        state.updateAndGet(state -> state.activate(disposable));
    }

    @Override
    public void onError(Throwable e) {
        state.get().onError(e);
    }

    @Override
    public void onSuccess(T o) {
        state.get().onSuccess(o);
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
