require 'vertx/util/utils.rb'
# Generated from io.vertx.ext.mongo.MongoClient
module VertxMongo
  #  A Vert.x service used to interact with MongoDB server instances.
  class MongoClient
    # @private
    # @param j_del [::VertxMongo::MongoClient] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxMongo::MongoClient] the underlying java delegate
    def j_del
      @j_del
    end
    #  Create a Mongo client which maintains its own data source.
    # @param [::Vertx::Vertx] vertx the Vert.x instance
    # @param [Hash{String => Object}] config the configuration
    # @return [::VertxMongo::MongoClient] the client
    def self.create_non_shared(vertx=nil,config=nil)
      if vertx.class.method_defined?(:j_del) && config.class == Hash && !block_given?
        return ::VertxMongo::MongoClient.new(Java::IoVertxExtMongo::MongoClient.java_method(:createNonShared, [Java::IoVertxCore::Vertx.java_class,Java::IoVertxCoreJson::JsonObject.java_class]).call(vertx.j_del,::Vertx::Util::Utils.to_json_object(config)))
      end
      raise ArgumentError, "Invalid arguments when calling create_non_shared(vertx,config)"
    end
    #  Create a Mongo client which shares its data source with any other Mongo clients created with the same
    #  data source name
    # @param [::Vertx::Vertx] vertx the Vert.x instance
    # @param [Hash{String => Object}] config the configuration
    # @param [String] dataSourceName the data source name
    # @return [::VertxMongo::MongoClient] the client
    def self.create_shared(vertx=nil,config=nil,dataSourceName=nil)
      if vertx.class.method_defined?(:j_del) && config.class == Hash && !block_given? && dataSourceName == nil
        return ::VertxMongo::MongoClient.new(Java::IoVertxExtMongo::MongoClient.java_method(:createShared, [Java::IoVertxCore::Vertx.java_class,Java::IoVertxCoreJson::JsonObject.java_class]).call(vertx.j_del,::Vertx::Util::Utils.to_json_object(config)))
      elsif vertx.class.method_defined?(:j_del) && config.class == Hash && dataSourceName.class == String && !block_given?
        return ::VertxMongo::MongoClient.new(Java::IoVertxExtMongo::MongoClient.java_method(:createShared, [Java::IoVertxCore::Vertx.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::java.lang.String.java_class]).call(vertx.j_del,::Vertx::Util::Utils.to_json_object(config),dataSourceName))
      end
      raise ArgumentError, "Invalid arguments when calling create_shared(vertx,config,dataSourceName)"
    end
    #  Save a document in the specified collection
    # @param [String] collection the collection
    # @param [Hash{String => Object}] document the document
    # @yield result handler will be provided with the id if document didn't already have one
    # @return [self]
    def save(collection=nil,document=nil)
      if collection.class == String && document.class == Hash && block_given?
        @j_del.java_method(:save, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(document),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling save(collection,document)"
    end
    #  Save a document in the specified collection with the specified write option
    # @param [String] collection the collection
    # @param [Hash{String => Object}] document the document
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption the write option to use
    # @yield result handler will be provided with the id if document didn't already have one
    # @return [self]
    def save_with_options(collection=nil,document=nil,writeOption=nil)
      if collection.class == String && document.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:saveWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(document),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling save_with_options(collection,document,writeOption)"
    end
    #  Insert a document in the specified collection
    # @param [String] collection the collection
    # @param [Hash{String => Object}] document the document
    # @yield result handler will be provided with the id if document didn't already have one
    # @return [self]
    def insert(collection=nil,document=nil)
      if collection.class == String && document.class == Hash && block_given?
        @j_del.java_method(:insert, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(document),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling insert(collection,document)"
    end
    #  Insert a document in the specified collection with the specified write option
    # @param [String] collection the collection
    # @param [Hash{String => Object}] document the document
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption the write option to use
    # @yield result handler will be provided with the id if document didn't already have one
    # @return [self]
    def insert_with_options(collection=nil,document=nil,writeOption=nil)
      if collection.class == String && document.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:insertWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(document),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling insert_with_options(collection,document,writeOption)"
    end
    #  Update matching documents in the specified collection
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match the documents
    # @param [Hash{String => Object}] update used to describe how the documents will be updated
    # @yield will be called when complete
    # @return [self]
    def update(collection=nil,query=nil,update=nil)
      if collection.class == String && query.class == Hash && update.class == Hash && block_given?
        @j_del.java_method(:update, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(update),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling update(collection,query,update)"
    end
    #  Update matching documents in the specified collection, specifying options
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match the documents
    # @param [Hash{String => Object}] update used to describe how the documents will be updated
    # @param [Hash] options options to configure the update
    # @yield will be called when complete
    # @return [self]
    def update_with_options(collection=nil,query=nil,update=nil,options=nil)
      if collection.class == String && query.class == Hash && update.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:updateWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::UpdateOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(update),Java::IoVertxExtMongo::UpdateOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling update_with_options(collection,query,update,options)"
    end
    #  Replace matching documents in the specified collection
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match the documents
    # @param [Hash{String => Object}] replace all matching documents will be replaced with this
    # @yield will be called when complete
    # @return [self]
    def replace(collection=nil,query=nil,replace=nil)
      if collection.class == String && query.class == Hash && replace.class == Hash && block_given?
        @j_del.java_method(:replace, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(replace),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling replace(collection,query,replace)"
    end
    #  Replace matching documents in the specified collection, specifying options
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match the documents
    # @param [Hash{String => Object}] replace all matching documents will be replaced with this
    # @param [Hash] options options to configure the replace
    # @yield will be called when complete
    # @return [self]
    def replace_with_options(collection=nil,query=nil,replace=nil,options=nil)
      if collection.class == String && query.class == Hash && replace.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:replaceWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::UpdateOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(replace),Java::IoVertxExtMongo::UpdateOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling replace_with_options(collection,query,replace,options)"
    end
    #  Find matching documents in the specified collection
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match documents
    # @yield will be provided with list of documents
    # @return [self]
    def find(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:find, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt != nil ? JSON.parse(elt.encode) : nil } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find(collection,query)"
    end
    #  Find matching documents in the specified collection, specifying options
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match documents
    # @param [Hash] options options to configure the find
    # @yield will be provided with list of documents
    # @return [self]
    def find_with_options(collection=nil,query=nil,options=nil)
      if collection.class == String && query.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:findWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::FindOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::FindOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt != nil ? JSON.parse(elt.encode) : nil } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_with_options(collection,query,options)"
    end
    #  Find a single matching document in the specified collection
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query the query used to match the document
    # @param [Hash{String => Object}] fields the fields
    # @yield will be provided with the document, if any
    # @return [self]
    def find_one(collection=nil,query=nil,fields=nil)
      if collection.class == String && query.class == Hash && fields.class == Hash && block_given?
        @j_del.java_method(:findOne, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(fields),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_one(collection,query,fields)"
    end
    #  Count matching documents in a collection.
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match documents
    # @yield will be provided with the number of matching documents
    # @return [self]
    def count(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:count, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling count(collection,query)"
    end
    #  Remove matching documents from a collection
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match documents
    # @yield will be called when complete
    # @return [self]
    def remove(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:remove, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove(collection,query)"
    end
    #  Remove matching documents from a collection with the specified write option
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match documents
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption the write option to use
    # @yield will be called when complete
    # @return [self]
    def remove_with_options(collection=nil,query=nil,writeOption=nil)
      if collection.class == String && query.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:removeWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_with_options(collection,query,writeOption)"
    end
    #  Remove a single matching document from a collection
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match document
    # @yield will be called when complete
    # @return [self]
    def remove_one(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:removeOne, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_one(collection,query)"
    end
    #  Remove a single matching document from a collection with the specified write option
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match document
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption the write option to use
    # @yield will be called when complete
    # @return [self]
    def remove_one_with_options(collection=nil,query=nil,writeOption=nil)
      if collection.class == String && query.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:removeOneWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_one_with_options(collection,query,writeOption)"
    end
    #  Create a new collection
    # @param [String] collectionName the name of the collection
    # @yield will be called when complete
    # @return [self]
    def create_collection(collectionName=nil)
      if collectionName.class == String && block_given?
        @j_del.java_method(:createCollection, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(collectionName,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling create_collection(collectionName)"
    end
    #  Get a list of all collections in the database.
    # @yield will be called with a list of collections.
    # @return [self]
    def get_collections
      if block_given?
        @j_del.java_method(:getCollections, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling get_collections()"
    end
    #  Drop a collection
    # @param [String] collection the collection
    # @yield will be called when complete
    # @return [self]
    def drop_collection(collection=nil)
      if collection.class == String && block_given?
        @j_del.java_method(:dropCollection, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling drop_collection(collection)"
    end
    #  Run an arbitrary MongoDB command.
    # @param [Hash{String => Object}] command the command
    # @yield will be called with the result.
    # @return [self]
    def run_command(command=nil)
      if command.class == Hash && block_given?
        @j_del.java_method(:runCommand, [Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(::Vertx::Util::Utils.to_json_object(command),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling run_command(command)"
    end
    #  Close the client and release its resources
    # @return [void]
    def close
      if !block_given?
        return @j_del.java_method(:close, []).call()
      end
      raise ArgumentError, "Invalid arguments when calling close()"
    end
  end
end
