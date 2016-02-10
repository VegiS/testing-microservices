# Testing Microservices: A case study

This repository contains an experiment on how to properly test microservices. The implementation is done in
Java with Spring Boot, the message queue in question is Kafka. The principle should work everywhere though.

## Setup

The setup consists of two constellations. We limited ourselves to three services (A,B,C), two events (e1, e2)
and two versions of each event. The next two paragraphs will describe the examples that you can see in this
project.

### Example 1 - simple A-B communication

This example is the easy use case. Service A exposes a rest endpoint named "triggerE1" accepting POST
requests. Upon triggering it the service emits a message of type E1. This event is consumed by service B.
As soon as B consumes an event of type E1 it will issue a get request to some other service.

![simple A-B setup](doc/setupAB.png)

### Example 2 - three way communication

## Theory

## How to run the experiment