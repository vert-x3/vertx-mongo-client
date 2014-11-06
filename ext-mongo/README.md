# mongoDB for Vert.x

A Vert.x library allowing applications to seamlessly interact with a mongoDB instance, whether that's
saving, retrieving, searching, or deleting documents. Mongo is a great match for persisting data in a Vert.x application
since it natively handles JSON (BSON) documents.

**Features**
 - Completely Async and Non-blocking
 - Custom codec to support fast serialization to/from Vert.x JSON
 - Supports a majority of the configuration options from the mongoDB Java Driver
 - Event Bus Proxy Support

*Note: The mongoDB Java Driver is still under heavy development.*

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

The easiest way to get started is to deploy it as a service

```java
Vertx vertx = Vertx.vertx();
vertx.deployVerticle("service:io.vertx:ext-mongo");
```

This will make the MongoService available as a [Service Proxy](#service-proxy) to be used throughout your application.

You can also configure the service during deployment

```java
JsonObject config = new JsonObject();
config.put("db_name", "mydb");
config.put("username", "john").put("password", "passw0rd");
vertx.deployVerticle("service:io.vertx:ext-mongo", new DeploymentOptions().setConfig(config));
```

See the [Configuration](#configuration) section below for a full list of configuration options.

## Service Proxy

Assuming you already have the service deployed, to retrieve the MongoService (from inside a Verticle for example)

```java
public class MyVerticle extends AbstractVerticle

  @Override
  public void start() {
    MongoService service = MongoService.createEventBusProxy(vertx, "vertx.mongo");
    ...
  }
```

This will create the service proxy allowing you to call the MongoService API methods instead of having to send
messages over the event bus. See [Service Proxy](https://github.com/vert-x3/service-proxy) for more information.

# Operations

The following are some examples of the operations supported by the MongoService API. Consult the javadoc/documentation
for detailed information on all API methods.

## Save

Saves a document into the collection. If the document has no id, it is inserted.  Otherwise, it is upserted
using the document's id as the query filter.

If the document is inserted and has no id, then the id field generated will be returned to the result handler. Otherwise it
is null.

Example of saving a document into the books collection
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
```

## Insert

Inserts a document into the collection.

If the document has no id, then the id field generated will be returned to the result handler. Otherwise it
is null.

Example of inserting a document into the books collection
```java
JsonObject document = new JsonObject().put("title", "The Hobbit");
service.insert("books", document, new WriteOptions(), outcome -> {
  if (outcome.succeeded()) {
    String id = outcome.result();
    System.out.println("Saved book with id " + id);
  } else {
    outcome.cause().printStackTrace();
  }
});
```

## Update

Updates one or multiple documents in a collection. The JsonObject that is passed in as part of the update must contain
[Update Operators](http://docs.mongodb.org/manual/reference/operator/update-field/).

Example of updating a document in the books collection
```java
JsonObject query = new JsonObject().put("title", "The Hobbit");
JsonObject update = new JsonObject().put("$set", new JsonObject().put("author", "J. R. R. Tolkien"));
service.update("books", query, update, new UpdateOptions(), outcome -> {
  if (outcome.succeeded()) {
    System.out.println("Book updated !");
  } else {
    outcome.cause().printStackTrace();
  }
});
```

UpdateOptions
 - `multi` set to true to update multiple documents
 - `upsert` set to true to insert the document if the query doesn't match

## Replace

Replaces one or multiple documents in a collection.

This is similar to the update operation, however it does not take any [Update Operators](http://docs.mongodb.org/manual/reference/operator/update-field/).
Instead it replaces the entire document with the one provided.

Example of replacing a document in the books collection
```java
JsonObject query = new JsonObject().put("title", "The Hobbit");
JsonObject replace = new JsonObject().put("title", "The Lord of the Rings").put("author", "J. R. R. Tolkien");
service.replace("books", query, replace, new UpdateOptions(), outcome -> {
  if (outcome.succeeded()) {
    System.out.println("Book replaced !");
  } else {
    outcome.cause().printStackTrace();
  }
});
```

UpdateOptions
 - `multi` set to true to update multiple documents
 - `upsert` set to true to insert the document if the query doesn't match

## Find

Finds matching documents in a collection

Example of finding all documents in the books collection
```java
JsonObject query = new JsonObject(); // empty query = match any
service.find("books", query, new FindOptions(), outcome -> {
  if (outcome.succeeded()) {
    for (JsonObject json : outcome.result()) {
      System.out.println(json.encodePrettily());
    }
  } else {
    outcome.cause().printStackTrace();
  }
});
```

FindOptions
 - `fields` The fields to return in the results. Defaults to null, meaning all fields will be returned
 - `sort` The fields to sort. Defaults to null.
 - `limit` The limit of the number of results to return. Default to -1, meaning all results will be returned.
 - `skip` The number of documents to skip before returning the results. Defaults to 0.

## Count

Counts the number of documents in a collection

Example of counting the total number of documents in the books collection
```java
JsonObject query = new JsonObject(); // empty query = match any
service.count("books", query, outcome -> {
  if (outcome.succeeded()) {
    long count = outcome.result();
    System.out.println(count + " document(s) found.");
  } else {
    outcome.cause().printStackTrace();
  }
});
```
## Remove

Removes matching documents in a collection

Example of removing all documents in the books collection
```java
JsonObject query = new JsonObject(); // empty query = match any
service.remove("books", query, new WriteOptions(), outcome -> {
  if (outcome.succeeded()) {
    System.out.println("Removed all documents !");
  } else {
    outcome.cause().printStackTrace();
  }
});
```

## Commands

Runs an arbitrary mongoDB command.

Commands can be used to run more advanced mongoDB features, such as using MapReduce.
For more information see the mongo docs for supported [Commands](http://docs.mongodb.org/manual/reference/command).

Example of running a ping command
```java
service.runCommand(new JsonObject().put("ping", 1), outcome -> {
  if (outcome.succeeded()) {
    System.out.println("Result: " + outcome.result().encodePrettily());
  } else {
    outcome.cause().printStackTrace();
  }
});
```

# Configuration

The following configuration is supported by the mongo service. The sections are broken out for clarity, but
it's important to note that this is **one** configuration file.

## Service Configuration

```javascript
{
  "address" : "some.address", // string
  "db_name" : "some.db"       // string
  "useObjectId" : true        // boolean
}
```

 - `address` The event bus address used by the service proxy. Defaults to `vertx.mongo`
 - `db_name` Name of the database in the mongoDB instance to use. Defaults to `default_db`
 - `useObjectId` Toggle this option to support persisting and retrieving ObjectId's as strings. Defaults to false

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

 - `connection_string` The connection string the driver uses to create the client.

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
 - finish off option descriptions
