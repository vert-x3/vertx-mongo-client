# mongoDB for Vert.x

A Vert.x library allowing applications to seamlessly interact with a mongoDB instance, whether that's
saving, retrieving, searching, or deleting documents. Mongo is a great match for persisting data in a Vert.x application
since it natively handles JSON (BSON) documents.

**Features**
 - Completely Async and Non-blocking
 - Custom codec to support fast serialization to/from Vert.x JSON
 - Supports most configuration options you get with Java driver
 - Event Bus Proxy Support

*Note: The driver this library uses is still under heavy development.*

# Getting Started

## Maven

Add the following dependency to your maven project

```xml
<dependencies>
  <dependency>
    <groupId>io.vertx</groupId>
    <artifactId>ext-mongo</artifactId>
    <version>$version</version>
  </dependency>
</dependencies>
```

## Gradle ##

Add the following dependency to your gradle project

```groovy
dependencies {
  compile("io.vertx:ext-mongo:$version")
}
```

## Deploy as a service

The easiest way to get started is to deploy the mongo service like so:

```java
Vertx vertx = Vertx.vertx();
vertx.deployVerticle("service:io.vertx:ext-mongo");
```

This will start up the mongo service with the default configuration. To specify your
own configuration see the [Configuration](#configuration) below.

## Use in a Verticle

Assuming you already have the service deployed, the best way to call the MongoService from within a verticle
is to use the event bus proxy feature. For example

```java
public class MyVerticle extends AbstractVerticle

  @Override
  public void start() {
    MongoService service = MongoService.createEventBusProxy(vertx, "vertx.mongo");
  }
```

This will create the service proxy allowing you to call the MongoService API methods instead of having to send
messages over the event bus. See [Service Proxy](https://github.com/vert-x3/service-proxy) for more information.

# Operations

## Save

Saves a document to the database

```java
JsonObject document = new JsonObject().put("title", "The Hobbit");
service.save("books", document, new WriteOptions(), outcome -> {
  if (outcome.succeeded()) {
    String id = outcome.result();
    System.out.println("Saved book with id " + id);
  } else {
    outcome.cause().printStackTrace();
  }
});
}
```

# Configuration

The following configuration is supported by the mongo service. The sections are broken out for clarity, but
it's important to note that this is **one** configuration file.

## Service Configuration

```javascript
{
  "address" : "some.address", // string
  "db_name" : "some.db"       // string
}
```

 - `address` The event bus address used by the service proxy. Defaults to `vertx.mongo`
 - `db_name` Name of the database in the mongoDB instance to use. Defaults to `default_db`

## Driver Configuration

The mongo service tries to support most options that are allowed by the driver. There are two ways to configure mongo
for use by the driver, either by a [Connection String](#connection-string) or by separate [Configuration Options](#configuration-options)

*Note: If the connection string is used the mongo service will ignore any [Configuration Options](#configuration-options)*

## Connection String

To specify the connection string to configure mongo

```javascript
{
  "connection_string" : "mongodb://localhost:27017"
}
```

 - `connection_string` The connection string the mongo driver uses to create the Mongo Client.

For more information on the format of the connection string consult the driver documentation.

## Configuration Options

Below are the supported options to configure the mongo client for the java driver.

```javascript
{
  // Single Cluster Settings
  "host" : "example.org", // string
  "port" : 27000,         // int

  // Multiple Cluster Settings
  "hosts" : [
    {
      "host" : "cluster1", // string
      "port" : 27000       // int
    },
    {
      "host" : "cluster2", // string
      "port" : 28000       // int
    },
    ...
  ]
  "replicaSet" :  "foo"         // string
  "clusterType" : "REPLICA_SET" // string

  // Connection Pool Settings
  "maxPoolSize" : 50,                // int
  "minPoolSize" : 25,                // int
  "maxIdleTimeMS" : 300000,          // long
  "maxLifeTimeMS" : 3600000,         // long
  "waitQueueMultiple"  : 10,         // int
  "waitQueueTimeoutMS" : 10000,      // long
  "maintenanceFrequencyMS" : 2000,   // long
  "maintenanceInitialDelayMS" : 500, // long

  // Credentials / Auth
  "username"   : "john",     // string
  "password"   : "passw0rd", // string
  "authSource" : "some.db"   // string
     // Auth mechanism
  "authMechanism"     : "GSSAPI",        // string
  "gssapiServiceName" : "myservicename", // string

  // Socket Settings
  "connectTimeoutMS" : 300000, // int
  "socketTimeoutMS"  : 100000, // int
  "receiveBufferSize" : 8192,  // int
  "receiveBufferSize" : 8192,  // int
  "keepAlive" : true           // boolean

  // Heartbeat socket settings
  "heartbeat.socket" : {
    "connectTimeoutMS" : 300000, // int
    "socketTimeoutMS"  : 100000, // int
    "receiveBufferSize" : 8192,  // int
    "receiveBufferSize" : 8192,  // int
    "keepAlive" : true           // boolean
  }

  // Server Settings
  "heartbeatFrequencyMS" :    1000 // long
  "minHeartbeatFrequencyMS" : 500 // long
}
```

*Note: The options above are made up and do not represent default values*

TODO
 - finish off operations
 - finish off option descriptions
