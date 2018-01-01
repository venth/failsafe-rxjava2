package com.github.venth.failsafe.reactivex.completableobservation;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import net.jodah.failsafe.CircuitBreaker;

public class CircuitBreakerCompletableObserver implements CompletableObserver, Disposable {

    private final AtomicReference<ObservationState> state;

    public CircuitBreakerCompletableObserver(CompletableObserver observer, CircuitBreaker circuitBreaker) {
        this.state = new AtomicReference<>(new ObservationDeactivated(this, observer, circuitBreaker));
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        state.updateAndGet(state -> state.activate(disposable));
    }

    @Override
    public void onComplete() {
        state.get().onComplete();
    }

    @Override
    public void onError(Throwable e) {
        state.get().onError(e);
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
