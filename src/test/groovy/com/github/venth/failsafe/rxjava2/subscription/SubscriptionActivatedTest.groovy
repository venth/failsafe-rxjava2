package com.github.venth.failsafe.rxjava2.subscription

import net.jodah.failsafe.CircuitBreaker
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import spock.lang.Specification

class SubscriptionActivatedTest extends Specification {

    def "passes onError, onComplete, onNext to subscriber"() {
        given:
            def subscriptionActivated = new SubscriptionActivated(subscriber,
                    subscription,
                    someBreaker)
        when:
            subscriptionActivated.onError(new RuntimeException())
            subscriptionActivated.onNext(new Object())
            subscriptionActivated.onComplete()
        then:
            1 * subscriber.onNext(_)
        and:
            1 * subscriber.onError(_)
        and:
            1 * subscriber.onComplete()
    }

    def "passes request to subscription"() {
        given:
            def subscriptionActivated = new SubscriptionActivated(subscriber,
                    subscription,
                    someBreaker)
        and:
            def numberOfElements = 10
        when:
            subscriptionActivated.request(numberOfElements)
        then:
            1 * subscription.request(numberOfElements)
    }

    def "cancels subscription when directly cancelled"() {
        given:
            def subscriptionActivated = new SubscriptionActivated(subscriber,
                    subscription,
                    someBreaker,
                    { subscriptionCancelled })
        when:
            def subscriptionState = subscriptionActivated.cancel()
        then:
            1 * subscription.cancel()
        and:
            subscriptionState == subscriptionCancelled
    }

    def "passes information about successful execution to circuit breaker"() {
        given:
            def subscriptionActivated = new SubscriptionActivated(subscriber,
                    subscription,
                    circuitBreaker)
        when:
            subscriptionActivated.onComplete()
        then:
            1 * circuitBreaker.recordSuccess()

    }

    def "passes information about failed execution to circuit breaker"() {
        given:
            def subscriptionActivated = new SubscriptionActivated(subscriber,
                    subscription,
                    circuitBreaker)
        and:
            def error = new RuntimeException()
        when:
            subscriptionActivated.onError(error)
        then:
            1 * circuitBreaker.recordFailure(error)

    }

    def someElement = new Object()
    def subscriptionCancelled = Mock(SubscriptionCancelled)

    def subscriber = Mock(Subscriber)

    def subscription = Mock(Subscription)

    def someBreaker = Stub(CircuitBreaker)
    def circuitBreaker = Mock(CircuitBreaker)
}
