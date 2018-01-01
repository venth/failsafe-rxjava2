package com.github.venth.failsafe.rxjava2

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import net.jodah.failsafe.CircuitBreaker
import net.jodah.failsafe.CircuitBreakerOpenException
import spock.lang.Shared
import spock.lang.Specification

class CircuitBreakerOperatorTest extends Specification {

    def "flowable passes every call when breaker is closed"() {
        given:
            def sequence = Flowable.fromCallable(successCall).lift(CircuitBreakerOperator.of(closedCircuitBreaker))
        when:
            def results = sequence.test()
        then:
            results.assertValue(successResult)
    }

    def "observable passes every call when breaker is closed"() {
        given:
            def sequence = Observable.fromCallable(successCall).lift(CircuitBreakerOperator.of(closedCircuitBreaker))
        when:
            def results = sequence.test()
        then:
            results.assertValue(successResult)
    }

    def "single passes every call when breaker is closed"() {
        given:
            def sequence = Single.fromCallable(successCall).lift(CircuitBreakerOperator.of(closedCircuitBreaker))
        when:
            def results = sequence.test()
        then:
            results.assertValue(successResult)
    }

    def "completable passes every call when breaker is closed"() {
        given:
            def sequence = Completable.fromCallable(successCall).lift(CircuitBreakerOperator.of(closedCircuitBreaker))
        when:
            def results = sequence.test()
        then:
            results.assertComplete()
    }

    def "maybe passes every call when breaker is closed"() {
        given:
            def sequence = Maybe.fromCallable(successCall).lift(CircuitBreakerOperator.of(closedCircuitBreaker))
        when:
            def results = sequence.test()
        then:
            results.assertComplete()
    }

    def "flowable emits exception when circuit breaker is open"() {
        given:
            def sequence = Flowable.fromCallable(failingCall).lift(CircuitBreakerOperator.of(openCircuitBreaker))
        when:
            def results = sequence.test()
        then:
            results.assertError(CircuitBreakerOpenException)
        and:
            0 * failingCall.call()
    }

    def "observable emits exception when circuit breaker is open"() {
        given:
            def sequence = Observable.fromCallable(failingCall).lift(CircuitBreakerOperator.of(openCircuitBreaker))
        when:
            def results = sequence.test()
        then:
            results.assertError(CircuitBreakerOpenException)
        and:
            0 * failingCall.call()
    }

    def "single emits exception when circuit breaker is open"() {
        given:
            def sequence = Single.fromCallable(failingCall).lift(CircuitBreakerOperator.of(openCircuitBreaker))
        when:
            def results = sequence.test()
        then:
            results.assertError(CircuitBreakerOpenException)
        and:
            0 * failingCall.call()
    }

    def "completable emits exception when circuit breaker is open"() {
        given:
            def sequence = Completable.fromCallable(failingCall).lift(CircuitBreakerOperator.of(openCircuitBreaker))
        when:
            def results = sequence.test()
        then:
            results.assertError(CircuitBreakerOpenException)
        and:
            0 * failingCall.call()
    }

    def "maybe emits exception when circuit breaker is open"() {
        given:
            def sequence = Maybe.fromCallable(failingCall).lift(CircuitBreakerOperator.of(openCircuitBreaker))
        when:
            def results = sequence.test()
        then:
            results.assertError(CircuitBreakerOpenException)
        and:
            0 * failingCall.call()
    }

    @Shared
    def successResult = new Object()
    @Shared
    def successCall = { successResult }
    @Shared
    def failingCall = { throw new RuntimeException() }

    def closedCircuitBreaker = Mock(CircuitBreaker) {
        allowsExecution() >> true
    }
    def openCircuitBreaker = Mock(CircuitBreaker) {
        allowsExecution() >> false
    }
}
