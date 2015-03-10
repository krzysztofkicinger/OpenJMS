# OpenJMS

Project presents simple implementation of Java Messaging Service Specification. 

### Version
1.0

### OpenJSM Management

How to start/shutdown OpenJSM Server?
```sh
$ startup
```
```sh
$ shutdown
```

How to start OpenJSM Administration Console?
```sh
$ openjsm admin
```

### Development

Project comprises of couple modules which provides simple implementation of JMS basics.
* **asynchronous package** - implementation of asynchronous message receiver
* **durable package** - implementation for Durable Subscriptions mechanism
* **filtering package** - implementation of message filtering
* **messages package** - presents usage of two kinds of message - object/bytes
* **pointtopoint package** - implementation of Point-To-Point communication type (Queue)
* **publishsubscribe package** - implementation of Publish/Subscribe pattern (Topics)
* **rmi package** - receives context through RMI
