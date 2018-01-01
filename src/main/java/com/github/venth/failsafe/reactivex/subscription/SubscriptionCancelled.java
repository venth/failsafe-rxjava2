package com.github.venth.failsafe.reactivex.subscription;

import org.reactivestreams.Subscription;

class SubscriptionCancelled<T> implements SubscriptionState<T> {

    @Override
    public SubscriptionState<T> activate(Subscription subscription) {
        return this;
    }

    @Override
    public SubscriptionState<T> cancel() {
        return this;
    }

    @Override
    public void request(long n) {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable err) {

    }

    @Override
    public void onComplete() {

    }
}
