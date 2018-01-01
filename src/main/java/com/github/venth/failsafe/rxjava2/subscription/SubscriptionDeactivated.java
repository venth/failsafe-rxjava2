package com.github.venth.failsafe.rxjava2.subscription;

import java.util.function.Function;
import java.util.function.Supplier;

import net.jodah.failsafe.CircuitBreaker;
import net.jodah.failsafe.CircuitBreakerOpenException;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

class SubscriptionDeactivated<T> implements SubscriptionState<T> {

    private final Subscription owner;

    private final Subscriber<? super T> subscriber;

    private final CircuitBreaker circuitBreaker;

    private final Function<Subscription, SubscriptionActivated<T>> subscriptionActivatedSupplier;

    private final Supplier<SubscriptionCancelled<T>> subscriptionCancelledSupplier;

    private Subscription subscription;

    public SubscriptionDeactivated(Subscription owner, Subscriber<? super T> subscriber, CircuitBreaker circuitBreaker) {
        this(owner,
                subscriber,
                circuitBreaker,
                subscription -> new SubscriptionActivated<>(subscriber, subscription, circuitBreaker),
                SubscriptionCancelled::new);
    }

    SubscriptionDeactivated(Subscription owner,
                            Subscriber<? super T> subscriber,
                            CircuitBreaker circuitBreaker,
                            Function<Subscription, SubscriptionActivated<T>> subscriptionActivatedSupplier,
                            Supplier<SubscriptionCancelled<T>> subscriptionCancelledSupplier) {
        this.owner = owner;
        this.subscriber = subscriber;
        this.circuitBreaker = circuitBreaker;
        this.subscriptionActivatedSupplier = subscriptionActivatedSupplier;
        this.subscriptionCancelledSupplier = subscriptionCancelledSupplier;
    }

    @Override
    public SubscriptionState<T> activate(Subscription subscription) {
        this.subscription = subscription;
        if (circuitBreaker.allowsExecution()) {
            subscriber.onSubscribe(owner);
            return subscriptionActivatedSupplier.apply(subscription);
        } else {
            subscription.cancel();
            subscriber.onSubscribe(owner);
            subscriber.onError(new CircuitBreakerOpenException());
            return subscriptionCancelledSupplier.get();
        }
    }

    @Override
    public SubscriptionState<T> cancel() {
        return this;
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
        subscriber.onError(err);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }
}
