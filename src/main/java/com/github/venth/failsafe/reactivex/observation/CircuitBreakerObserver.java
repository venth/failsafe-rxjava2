package com.github.venth.failsafe.reactivex.observation;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import net.jodah.failsafe.CircuitBreaker;

public class CircuitBreakerObserver<T> implements Observer<T>, Disposable {

    private final AtomicReference<ObservationState<T>> state;

    public CircuitBreakerObserver(Observer<T> observer, CircuitBreaker circuitBreaker) {
        this.state = new AtomicReference<>(new ObservationDeactivated<>(this, observer, circuitBreaker));
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        state.updateAndGet(state -> state.activate(disposable));
    }

    @Override
    public void onNext(T t) {
        state.get().onNext(t);
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
