require 'vertx/vertx'
require 'vertx-mongo/mongo_client'
require 'vertx/util/utils.rb'
# Generated from io.vertx.ext.mongo.MongoService
module VertxMongo
  #  @author <a href="http://tfox.org">Tim Fox</a>
  class MongoService < ::VertxMongo::MongoClient
    # @private
    # @param j_del [::VertxMongo::MongoService] the java delegate
    def initialize(j_del)
      super(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxMongo::MongoService] the underlying java delegate
    def j_del
      @j_del
    end
    #  Create a proxy to a service that is deployed somewhere on the event bus
    # @param [::Vertx::Vertx] vertx the Vert.x instance
    # @param [String] address the address the service is listening on on the event bus
    # @return [::VertxMongo::MongoService] the service
    def self.create_event_bus_proxy(vertx=nil,address=nil)
      if vertx.class.method_defined?(:j_del) && address.class == String && !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtMongo::MongoService.java_method(:createEventBusProxy, [Java::IoVertxCore::Vertx.java_class,Java::java.lang.String.java_class]).call(vertx.j_del,address),::VertxMongo::MongoService)
      end
      raise ArgumentError, "Invalid arguments when calling create_event_bus_proxy(vertx,address)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] document 
    # @yield 
    # @return [self]
    def save(collection=nil,document=nil)
      if collection.class == String && document.class == Hash && block_given?
        @j_del.java_method(:save, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(document),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling save(collection,document)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] document 
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption 
    # @yield 
    # @return [self]
    def save_with_options(collection=nil,document=nil,writeOption=nil)
      if collection.class == String && document.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:saveWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(document),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling save_with_options(collection,document,writeOption)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] document 
    # @yield 
    # @return [self]
    def insert(collection=nil,document=nil)
      if collection.class == String && document.class == Hash && block_given?
        @j_del.java_method(:insert, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(document),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling insert(collection,document)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] document 
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption 
    # @yield 
    # @return [self]
    def insert_with_options(collection=nil,document=nil,writeOption=nil)
      if collection.class == String && document.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:insertWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(document),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling insert_with_options(collection,document,writeOption)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [Hash{String => Object}] update 
    # @yield 
    # @return [self]
    def update(collection=nil,query=nil,update=nil)
      if collection.class == String && query.class == Hash && update.class == Hash && block_given?
        @j_del.java_method(:update, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(update),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling update(collection,query,update)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [Hash{String => Object}] update 
    # @yield 
    # @return [self]
    def update_collection(collection=nil,query=nil,update=nil)
      if collection.class == String && query.class == Hash && update.class == Hash && block_given?
        @j_del.java_method(:updateCollection, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(update),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling update_collection(collection,query,update)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [Hash{String => Object}] update 
    # @param [Hash] options 
    # @yield 
    # @return [self]
    def update_with_options(collection=nil,query=nil,update=nil,options=nil)
      if collection.class == String && query.class == Hash && update.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:updateWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::UpdateOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(update),Java::IoVertxExtMongo::UpdateOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling update_with_options(collection,query,update,options)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [Hash{String => Object}] update 
    # @param [Hash] options 
    # @yield 
    # @return [self]
    def update_collection_with_options(collection=nil,query=nil,update=nil,options=nil)
      if collection.class == String && query.class == Hash && update.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:updateCollectionWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::UpdateOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(update),Java::IoVertxExtMongo::UpdateOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling update_collection_with_options(collection,query,update,options)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [Hash{String => Object}] replace 
    # @yield 
    # @return [self]
    def replace(collection=nil,query=nil,replace=nil)
      if collection.class == String && query.class == Hash && replace.class == Hash && block_given?
        @j_del.java_method(:replace, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(replace),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling replace(collection,query,replace)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [Hash{String => Object}] replace 
    # @yield 
    # @return [self]
    def replace_documents(collection=nil,query=nil,replace=nil)
      if collection.class == String && query.class == Hash && replace.class == Hash && block_given?
        @j_del.java_method(:replaceDocuments, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(replace),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling replace_documents(collection,query,replace)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [Hash{String => Object}] replace 
    # @param [Hash] options 
    # @yield 
    # @return [self]
    def replace_with_options(collection=nil,query=nil,replace=nil,options=nil)
      if collection.class == String && query.class == Hash && replace.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:replaceWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::UpdateOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(replace),Java::IoVertxExtMongo::UpdateOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling replace_with_options(collection,query,replace,options)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [Hash{String => Object}] replace 
    # @param [Hash] options 
    # @yield 
    # @return [self]
    def replace_documents_with_options(collection=nil,query=nil,replace=nil,options=nil)
      if collection.class == String && query.class == Hash && replace.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:replaceDocumentsWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::UpdateOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(replace),Java::IoVertxExtMongo::UpdateOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling replace_documents_with_options(collection,query,replace,options)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @yield 
    # @return [self]
    def find(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:find, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt != nil ? JSON.parse(elt.encode) : nil } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find(collection,query)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @yield 
    # @return [self]
    def find_batch(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:findBatch, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_batch(collection,query)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [Hash] options 
    # @yield 
    # @return [self]
    def find_with_options(collection=nil,query=nil,options=nil)
      if collection.class == String && query.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:findWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::FindOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::FindOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt != nil ? JSON.parse(elt.encode) : nil } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_with_options(collection,query,options)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [Hash] options 
    # @yield 
    # @return [self]
    def find_batch_with_options(collection=nil,query=nil,options=nil)
      if collection.class == String && query.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:findBatchWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::FindOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::FindOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_batch_with_options(collection,query,options)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [Hash{String => Object}] fields 
    # @yield 
    # @return [self]
    def find_one(collection=nil,query=nil,fields=nil)
      if collection.class == String && query.class == Hash && fields.class == Hash && block_given?
        @j_del.java_method(:findOne, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(fields),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_one(collection,query,fields)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @yield 
    # @return [self]
    def count(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:count, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling count(collection,query)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @yield 
    # @return [self]
    def remove(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:remove, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove(collection,query)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @yield 
    # @return [self]
    def remove_documents(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:removeDocuments, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_documents(collection,query)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption 
    # @yield 
    # @return [self]
    def remove_with_options(collection=nil,query=nil,writeOption=nil)
      if collection.class == String && query.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:removeWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_with_options(collection,query,writeOption)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption 
    # @yield 
    # @return [self]
    def remove_documents_with_options(collection=nil,query=nil,writeOption=nil)
      if collection.class == String && query.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:removeDocumentsWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_documents_with_options(collection,query,writeOption)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @yield 
    # @return [self]
    def remove_one(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:removeOne, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_one(collection,query)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @yield 
    # @return [self]
    def remove_document(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:removeDocument, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_document(collection,query)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption 
    # @yield 
    # @return [self]
    def remove_one_with_options(collection=nil,query=nil,writeOption=nil)
      if collection.class == String && query.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:removeOneWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_one_with_options(collection,query,writeOption)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] query 
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption 
    # @yield 
    # @return [self]
    def remove_document_with_options(collection=nil,query=nil,writeOption=nil)
      if collection.class == String && query.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:removeDocumentWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_document_with_options(collection,query,writeOption)"
    end
    # @param [String] collectionName 
    # @yield 
    # @return [self]
    def create_collection(collectionName=nil)
      if collectionName.class == String && block_given?
        @j_del.java_method(:createCollection, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(collectionName,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling create_collection(collectionName)"
    end
    # @yield 
    # @return [self]
    def get_collections
      if block_given?
        @j_del.java_method(:getCollections, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling get_collections()"
    end
    # @param [String] collection 
    # @yield 
    # @return [self]
    def drop_collection(collection=nil)
      if collection.class == String && block_given?
        @j_del.java_method(:dropCollection, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling drop_collection(collection)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] key 
    # @yield 
    # @return [self]
    def create_index(collection=nil,key=nil)
      if collection.class == String && key.class == Hash && block_given?
        @j_del.java_method(:createIndex, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(key),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling create_index(collection,key)"
    end
    # @param [String] collection 
    # @param [Hash{String => Object}] key 
    # @param [Hash] options 
    # @yield 
    # @return [self]
    def create_index_with_options(collection=nil,key=nil,options=nil)
      if collection.class == String && key.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:createIndexWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::IndexOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(key),Java::IoVertxExtMongo::IndexOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling create_index_with_options(collection,key,options)"
    end
    # @param [String] collection 
    # @yield 
    # @return [self]
    def list_indexes(collection=nil)
      if collection.class == String && block_given?
        @j_del.java_method(:listIndexes, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling list_indexes(collection)"
    end
    # @param [String] collection 
    # @param [String] indexName 
    # @yield 
    # @return [self]
    def drop_index(collection=nil,indexName=nil)
      if collection.class == String && indexName.class == String && block_given?
        @j_del.java_method(:dropIndex, [Java::java.lang.String.java_class,Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,indexName,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling drop_index(collection,indexName)"
    end
    # @param [String] commandName 
    # @param [Hash{String => Object}] command 
    # @yield 
    # @return [self]
    def run_command(commandName=nil,command=nil)
      if commandName.class == String && command.class == Hash && block_given?
        @j_del.java_method(:runCommand, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(commandName,::Vertx::Util::Utils.to_json_object(command),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling run_command(commandName,command)"
    end
    # @param [String] collection 
    # @param [String] fieldName 
    # @param [String] resultClassname 
    # @yield 
    # @return [self]
    def distinct(collection=nil,fieldName=nil,resultClassname=nil)
      if collection.class == String && fieldName.class == String && resultClassname.class == String && block_given?
        @j_del.java_method(:distinct, [Java::java.lang.String.java_class,Java::java.lang.String.java_class,Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,fieldName,resultClassname,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling distinct(collection,fieldName,resultClassname)"
    end
    # @param [String] collection 
    # @param [String] fieldName 
    # @param [String] resultClassname 
    # @yield 
    # @return [self]
    def distinct_batch(collection=nil,fieldName=nil,resultClassname=nil)
      if collection.class == String && fieldName.class == String && resultClassname.class == String && block_given?
        @j_del.java_method(:distinctBatch, [Java::java.lang.String.java_class,Java::java.lang.String.java_class,Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,fieldName,resultClassname,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling distinct_batch(collection,fieldName,resultClassname)"
    end
    # @return [void]
    def close
      if !block_given?
        return @j_del.java_method(:close, []).call()
      end
      raise ArgumentError, "Invalid arguments when calling close()"
    end
  end
end
