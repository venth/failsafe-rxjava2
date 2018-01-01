package com.github.venth.failsafe.rxjava2.subscription;

import java.util.function.Supplier;

import net.jodah.failsafe.CircuitBreaker;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

class SubscriptionActivated<T> implements SubscriptionState<T> {

    private final Subscriber<? super T> subscriber;

    private final Subscription subscription;

    private final CircuitBreaker circuitBreaker;

    private final Supplier<SubscriptionCancelled<T>> subscriptionCancelledSupplier;

    SubscriptionActivated(Subscriber<? super T> subscriber, Subscription subscription, CircuitBreaker circuitBreaker) {
        this(subscriber, subscription, circuitBreaker, SubscriptionCancelled::new);
    }

    SubscriptionActivated(Subscriber<? super T> subscriber,
                          Subscription subscription,
                          CircuitBreaker circuitBreaker,
                          Supplier<SubscriptionCancelled<T>> subscriptionCancelledSupplier) {
        this.subscriber = subscriber;
        this.subscription = subscription;
        this.circuitBreaker = circuitBreaker;
        this.subscriptionCancelledSupplier = subscriptionCancelledSupplier;
    }

    @Override
    public void request(long n) {
        subscription.request(n);
    }

    @Override
    public void onNext(T t) {
        subscriber.onNext(t);
    }

    @Override
    public void onError(Throwable err) {
        circuitBreaker.recordFailure(err);
        subscriber.onError(err);
    }

    @Override
    public void onComplete() {
        circuitBreaker.recordSuccess();
        subscriber.onComplete();
    }

    @Override
    public SubscriptionState<T> activate(Subscription subscription) {
        return this;
    }

    @Override
    public SubscriptionState<T> cancel() {
        subscription.cancel();
        return subscriptionCancelledSupplier.get();
    }
}
