package com.github.venth.failsafe.rxjava2.subscription

import net.jodah.failsafe.CircuitBreaker
import net.jodah.failsafe.CircuitBreakerOpenException
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class SubscriptionDeactivatedTest extends Specification {

    def "activates the subscription when circuit breaker allows execution"() {
        given:
            def subscriptionDeactivated = new SubscriptionDeactivated(owner,
                    subscriber,
                    closedBreaker,
                    { subscription -> subscriptionActivated },
                    { subscriptionCancelled })
        when:
            def subscriptionState = subscriptionDeactivated.activate(someSubscription)
        then:
            subscriptionState == subscriptionActivated
    }

    def "doesn't emit error during subscription when circuit breaker allows execution"() {
        given:
            def subscriptionDeactivated = new SubscriptionDeactivated(owner,
                    subscriber,
                    closedBreaker,
                    { subscription -> subscriptionActivated },
                    { subscriptionCancelled })
        when:
            subscriptionDeactivated.activate(someSubscription)
        then:
            0 * subscriber.onError(_)
    }

    def "doesn't cancel during subscription when circuit breaker allows execution"() {
        given:
            def subscriptionDeactivated = new SubscriptionDeactivated(owner,
                    subscriber,
                    closedBreaker,
                    { subscription -> subscriptionActivated },
                    { subscriptionCancelled })
        when:
            subscriptionDeactivated.activate(subscription)
        then:
            0 * subscription.cancel()
    }

    @Unroll
    def "subscribes to subscriber when activates regardless of circuit breaker state #circuitBreaker"() {
        given:
            def subscriptionDeactivated = new SubscriptionDeactivated(owner,
                    subscriber,
                    circuitBreaker,
                    { subscription -> subscriptionActivated },
                    { subscriptionCancelled })
        when:
            subscriptionDeactivated.activate(someSubscription)
        then:
            1 * subscriber.onSubscribe(owner)
        where:
            circuitBreaker << [ closedBreaker, openedBreaker ]
    }

    def "cancels the subscription when circuit breaker blocks execution"() {
        given:
            def subscriptionDeactivated = new SubscriptionDeactivated(owner,
                    subscriber,
                    openedBreaker,
                    { subscription -> subscriptionActivated },
                    { subscriptionCancelled })
        when:
            def subscriptionState = subscriptionDeactivated.activate(subscription)
        then:
            subscriptionState == subscriptionCancelled
        and:
            1 * subscription.cancel()
    }

    def "emits CircuitBreakerOpenException error circuit breaker blocks execution"() {
        given:
            def subscriptionDeactivated = new SubscriptionDeactivated(owner,
                    subscriber,
                    openedBreaker,
                    { subscription -> subscriptionActivated },
                    { subscriptionCancelled })
        when:
            subscriptionDeactivated.activate(subscription)
        then:
            1 * subscriber.onError(_) >> { error -> error == CircuitBreakerOpenException }

    }

    def "doesn't change subscription state when directly cancelled"() {
        given:
            def subscriptionDeactivated = new SubscriptionDeactivated(owner,
                    subscriber,
                    openedBreaker)
        when:
            def subscriptionState = subscriptionDeactivated.cancel()
        then:
            subscriptionState == subscriptionDeactivated
    }

    def "passes onError, onComplete, onNext to subscriber"() {
        given:
            def subscriptionDeactivated = new SubscriptionDeactivated(owner,
                    subscriber,
                    openedBreaker)
        when:
            subscriptionDeactivated.onError(new RuntimeException())
            subscriptionDeactivated.onNext(new Object())
            subscriptionDeactivated.onComplete()
        then:
            1 * subscriber.onNext(_)
        and:
            1 * subscriber.onError(_)
        and:
            1 * subscriber.onComplete()
    }

    def subscriptionActivated = Mock(SubscriptionActivated)

    def subscriptionCancelled = Mock(SubscriptionCancelled)

    def owner = Mock(Subscription)

    def subscriber = Mock(Subscriber)

    def someSubscription = Stub(Subscription)
    def subscription = Mock(Subscription)

    @Shared
    def closedBreaker = Mock(CircuitBreaker) {
        allowsExecution() >> true
    }

    @Shared
    def openedBreaker = Mock(CircuitBreaker) {
        allowsExecution() >> false
    }
}
