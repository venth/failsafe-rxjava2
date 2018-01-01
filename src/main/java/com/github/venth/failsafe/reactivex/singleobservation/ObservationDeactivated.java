package com.github.venth.failsafe.reactivex.singleobservation;

import java.util.function.Function;
import java.util.function.Supplier;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.CircuitBreakerOpenException;

class ObservationDeactivated<T> implements ObservationState<T> {

    private final Disposable owner;

    private final SingleObserver<T> observer;

    private final CircuitBreaker circuitBreaker;

    private final Function<Disposable, ObservationActivated<T>> observationActivatedSupplier;

    private final Supplier<ObservationDisposed<T>> observationDisposedSupplier;

    ObservationDeactivated(Disposable owner, SingleObserver<T> observer, CircuitBreaker circuitBreaker) {
        this(owner,
                observer,
                circuitBreaker,
                disposable -> new ObservationActivated<T>(disposable, observer, circuitBreaker),
                ObservationDisposed::new);
    }

    ObservationDeactivated(Disposable owner,
                           SingleObserver<T> observer,
                           CircuitBreaker circuitBreaker,
                           Function<Disposable, ObservationActivated<T>> observationActivatedSupplier,
                           Supplier<ObservationDisposed<T>> observationDisposedSupplier) {
        this.owner = owner;
        this.observer = observer;
        this.circuitBreaker = circuitBreaker;
        this.observationActivatedSupplier = observationActivatedSupplier;
        this.observationDisposedSupplier = observationDisposedSupplier;
    }

    @Override
    public ObservationState<T> activate(Disposable disposable) {
        if (circuitBreaker.allowsExecution()) {
            observer.onSubscribe(owner);
            return observationActivatedSupplier.apply(disposable);
        } else {
            disposable.dispose();
            observer.onSubscribe(owner);
            observer.onError(new CircuitBreakerOpenException());
            return observationDisposedSupplier.get();
        }
    }

    @Override
    public void onSuccess(T t) {
        observer.onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        observer.onError(e);
    }

    @Override
    public ObservationState<T> dispose() {
        return this;
    }

    @Override
    public boolean isDisposed() {
        return false;
    }
}
