package com.github.venth.failsafe.rxjava2;

import com.github.venth.failsafe.rxjava2.completableobservation.CircuitBreakerCompletableObserver;
import com.github.venth.failsafe.rxjava2.maybeobservation.CircuitBreakerMaybeObserver;
import com.github.venth.failsafe.rxjava2.observation.CircuitBreakerObserver;
import com.github.venth.failsafe.rxjava2.singleobservation.CircuitBreakerSingleObserver;
import com.github.venth.failsafe.rxjava2.subscription.CircuitBreakerSubscriber;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOperator;
import io.reactivex.FlowableOperator;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeOperator;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOperator;
import net.jodah.failsafe.CircuitBreaker;
import org.reactivestreams.Subscriber;

public class CircuitBreakerOperator<T> implements
        FlowableOperator<T, T>,
        ObservableOperator<T, T>,
        SingleOperator<T, T>,
        MaybeOperator<T, T>,
        CompletableOperator {

    private final CircuitBreaker circuitBreaker;

    CircuitBreakerOperator(CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    public static <R> CircuitBreakerOperator<R> of(CircuitBreaker circuitBreaker) {
        return new CircuitBreakerOperator<>(circuitBreaker);
    }

    @Override
    public Subscriber<? super T> apply(Subscriber<? super T> observer) throws Exception {
        return new CircuitBreakerSubscriber<>(observer, circuitBreaker);
    }

    @Override
    public Observer<? super T> apply(Observer<? super T> observer) throws Exception {
        return new CircuitBreakerObserver<>(observer, circuitBreaker);
    }

    @Override
    public SingleObserver<? super T> apply(SingleObserver<? super T> observer) throws Exception {
        return new CircuitBreakerSingleObserver<>(observer, circuitBreaker);
    }

    @Override
    public MaybeObserver<? super T> apply(MaybeObserver<? super T> observer) throws Exception {
        return new CircuitBreakerMaybeObserver<>(observer, circuitBreaker);
    }

    @Override
    public CompletableObserver apply(CompletableObserver observer) throws Exception {
        return new CircuitBreakerCompletableObserver(observer, circuitBreaker);
    }
}
