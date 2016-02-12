# Testing Microservices: A case study

This repository contains an experiment on how to properly test microservices that are communicating over a message queue.
The implementation is done in Java with Spring Boot, the message queue in question is Kafka.
The principle should work everywhere though.

It is assumed that you know about [event sourcing](http://martinfowler.com/eaaDev/EventSourcing.html) and
[CQRS](http://martinfowler.com/bliki/CQRS.html).

## Setup

The setup consists of two constellations. We limited ourselves to three services (A,B,C) and three events (E1, E2, E3).
The next two paragraphs will describe the examples that you can see in this project.

### Example 1 - simple A-B communication

This example is the easy use case. Service A exposes a rest endpoint named "triggerE1" accepting POST
requests. Upon triggering it the service emits a message of type E1. This event is consumed by service B.
As soon as B consumes an event of type E1 it will issue a get request to some other service.

![simple A-B setup](doc/setupAB.png)

### Example 2 - three way communication

This example is the more complicated use case. We have Service A publishing an event of type E2 as soon as
the "triggerE2" endpoint is called.
This event is consumed by service B and C. On receiving event E2 the service C will emit an event of type E3.

The service B will wait for **both** events of type E2 and E3 to appear to trigger another external endpoint.

![A-B-C setup](doc/setupABC.png)

## The problem

The central problem that exists in a distributed system is how to test it? How can you do changes without breaking the
existing live system and how do you prove that your change actually works?

Currently this is mostly done by having a staging system running that somehow resembles the setup of your live system.
This has multiple disadvantages as you suddenly have a second system of the same complexity to maintain. But then the
load pattern is not the same so your tests on the staging system actually do not reflect the full behaviour of the live
system.

The alternative is to actually divert live traffic to test your change. This approach brings other problems with it.
You have to actually be able to risk live traffic going wrong. You have to synchronize the old versions of the service
that it ignores the traffic that is handled by the new version. Or you insert specific test data into your system which
then has to be distinguished by the application. All of this is doable but not perfect.

## The solution

The solution that we propose with this document is to do `contract based black box testing`.

A contract based black box test (CBBBT or C3B Test [speak KEB Test]) is a construct that combines [consumer driven contract
testing](http://martinfowler.com/articles/consumerDrivenContracts.html) with black box testing. What we are proposing is to
create requirements for certain features on the consumer side and then documenting these in contracts.
These contracts are then used as input for the blackbox tests of the consumer as well as as contract for the producing side.

Lets consider example 1 again.
![simple A-B setup](doc/setupAB.png)

First we create a C3B test for service B. This test specifies how the event E1 should look like. It uses a DSL to create
requirements for the event structure which also serves as input for the blackbox tests. These blackbox tests verify that
 given a certain E2 the right outgoing behaviour is triggered (in this case calling a rest endpoint).

![C3B for service B](doc/C3BAB-B.png)

With this test we can verify that given a certain E2 we will trigger the right action on our side. Great. Now we know
that our service behaves correctly.

The next step is to get the other side to deliver. To ensure this we create another C3B test for service A.
This time we use the contract from B as verification for the output. This is done to ensure service B confirms to the
contract.

![C3B for service A](doc/C3BAB-A.png)

We now know that given the right impulse (a POST to "triggerE1") in service A will result in the correct event of type
E1 published to the message queue. We also know that B reacts to events of type E1 in the correct way. With these two
tests we can now show that our system actually works as expected. One of the upsides is that you can do this on component
level. You do not have to maintain a staging system where you actually have a copy of your live system up and running.

### Making changes

So far we only discussed how it works for a static system. As we know systems are never static. Everything is changing all
the time. In this paragraph we will be discussing how to make changes to such a system and still ensuring that it works.

First lets look at the current situation:

![The example from above](doc/simple-version-1.png)

We have service B in version 1. For the sake of simplicity the version of B will always be the same as the current
version of the contract (B in version 1 means contract with version 1). **A consumer always has to confirm to its own
contract**. Service A in version 1 confirms to the contract of B (represented by the arrow).
This means we can deploy both services together and they can interact. The tests described above are showing us that
this works.

Now the requirements for B change. We have to change the behaviour of the service in a way that actually affects the
contract. This results in a new version of the contract being published.

![A new version for B](doc/simple-version-2.png)

The first thing we have to make sure is that our consumer is backwards compatible. As the old events will stay in the
history this is the only way to make sure that B is still able to consume the whole history of events without struggling.

![A new version for B](doc/simple-version-2-contract.png)

Now that we know our consumer will still work we can run the tests for A again and discover that the producer is not
confirming to the new contract.

![Broken contracts](doc/simple-version-2-broken.png)

We have to create a new version of service A that confirms to the new version of the contract.

![A new version for A](doc/simple-version-2-fixed.png)

Now we have a new version of both services which again can communicate with each other.

#### Additional stability on the producer side

In theory it is enough for the producer to confirm to the current contract. We still recommend to use a stronger rule
for stability.

The problem with the described approach is that there is no fallback in case you have to roll back the consumer after
the new version of the producer is live. To make sure that you only have to rollback one service and therefore contain
the changes to a minimum we recommend to also let the producer confirm to at least the next older version of the consumer
contract.

![Including the history](doc/simple-version-2-history.png)

This way we can always be sure that the producer is emitting events that can be understood and that the consumer is able
to work with the current events and the ones in the history, even if you have to rollback the consumer to the previous
version.

You can go even stronger from here and force the producer to confirm to all versions of all contracts of all consumers.
this forces full backwards compatibility, which generally helps to keep the system evolvable. Also it ensures that
the system still works even if your current consumer relies on features that are not present in the current contract
but only in older versions. This in general is an error in the design then, but avoidable with this technique.

The exact tradeoffs here are highly individual and depend solely on the situation at hand.

### How does it work in larger systems?

The interesting thing about this approach is that it scales with your system. So far we only covered the approach for
two services talking to each other. But in fact it works for infinitly large systems.



## How to run the experiment