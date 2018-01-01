package com.github.venth.failsafe.rxjava2.subscription;

import java.util.concurrent.atomic.AtomicReference;

import net.jodah.failsafe.CircuitBreaker;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class CircuitBreakerSubscriber<T> implements Subscriber<T>, Subscription {

    private final AtomicReference<SubscriptionState<T>> state;

    public CircuitBreakerSubscriber(Subscriber<? super T> subscriber, CircuitBreaker circuitBreaker) {
        this.state = new AtomicReference<>(new SubscriptionDeactivated<T>(this, subscriber, circuitBreaker));
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        state.updateAndGet(state -> state.activate(subscription));
    }

    @Override
    public void onNext(T t) {
        state.get().onNext(t);
    }

    @Override
    public void onError(Throwable err) {
        state.get().onError(err);
    }

    @Override
    public void onComplete() {
        state.get().onComplete();
    }

    @Override
    public void request(long n) {
        state.get().request(n);
    }

    @Override
    public void cancel() {
        state.updateAndGet(SubscriptionState::cancel);
    }
}
