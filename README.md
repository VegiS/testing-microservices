# Testing Microservices: A case study

This repository contains an experiment on how to properly test microservices. The implementation is done in
Java with Spring Boot, the message queue in question is Kafka. The principle should work everywhere though.

## Setup

The setup consists of two constellations. We limited ourselves to three services (A,B,C), three events (E1, E2, E3).
The next two paragraphs will describe the examples that you can see in this
project.

### Example 1 - simple A-B communication

This example is the easy use case. Service A exposes a rest endpoint named "triggerE1" accepting POST
requests. Upon triggering it the service emits a message of type E1. This event is consumed by service B.
As soon as B consumes an event of type E1 it will issue a get request to some other service.

![simple A-B setup](doc/setupAB.png)

### Example 2 - three way communication

This example is the more complicated use case. We have Service A publishing an event of type E2 as soon as
the "triggerE2" endpoint is called.
This event is consumed by service B and C. On receiving event E2 the service C will emit an event of type E3.

The service B will wait for both events of type E2 and E3 to appear to trigger another external endpoint.

![A-B-C setup](doc/setupABC.png)

## Theory

## How to run the experiment