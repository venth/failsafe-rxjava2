package com.github.venth.failsafe.rxjava2.completableobservation;

import java.util.function.Function;
import java.util.function.Supplier;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.CircuitBreakerOpenException;

class ObservationDeactivated implements ObservationState {

    private final Disposable owner;

    private final CompletableObserver observer;

    private final CircuitBreaker circuitBreaker;

    private final Function<Disposable, ObservationActivated> observationActivatedSupplier;

    private final Supplier<ObservationDisposed> observationDisposedSupplier;

    ObservationDeactivated(Disposable owner, CompletableObserver observer, CircuitBreaker circuitBreaker) {
        this(owner,
                observer,
                circuitBreaker,
                disposable -> new ObservationActivated(disposable, observer, circuitBreaker),
                ObservationDisposed::new);
    }

    ObservationDeactivated(Disposable owner,
                           CompletableObserver observer,
                           CircuitBreaker circuitBreaker,
                           Function<Disposable, ObservationActivated> observationActivatedSupplier,
                           Supplier<ObservationDisposed> observationDisposedSupplier) {
        this.owner = owner;
        this.observer = observer;
        this.circuitBreaker = circuitBreaker;
        this.observationActivatedSupplier = observationActivatedSupplier;
        this.observationDisposedSupplier = observationDisposedSupplier;
    }

    @Override
    public ObservationState activate(Disposable disposable) {
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
    public void onError(Throwable e) {
        observer.onError(e);
    }

    @Override
    public ObservationState dispose() {
        return this;
    }

    @Override
    public boolean isDisposed() {
        return false;
    }

    @Override
    public void onComplete() {
        observer.onComplete();
    }
}
