package com.github.venth.failsafe.rxjava2.subscription;

import org.reactivestreams.Subscription;

interface SubscriptionState<T> {
    SubscriptionState<T> activate(Subscription subscription);
    SubscriptionState<T> cancel();

    void request(long n);
    void onNext(T t);
    void onError(Throwable err);
    void onComplete();
}
