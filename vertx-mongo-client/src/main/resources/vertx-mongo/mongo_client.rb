require 'vertx/vertx'
require 'vertx-mongo/mongo_grid_fs_client'
require 'vertx/util/utils.rb'
# Generated from io.vertx.ext.mongo.MongoClient
module VertxMongo
  #  A Vert.x service used to interact with MongoDB server instances.
  #  <p>
  #  Some of the operations might change <i>_id</i> field of passed  document.
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
    @@j_api_type = Object.new
    def @@j_api_type.accept?(obj)
      obj.class == MongoClient
    end
    def @@j_api_type.wrap(obj)
      MongoClient.new(obj)
    end
    def @@j_api_type.unwrap(obj)
      obj.j_del
    end
    def self.j_api_type
      @@j_api_type
    end
    def self.j_class
      Java::IoVertxExtMongo::MongoClient.java_class
    end
    #  Create a Mongo client which maintains its own data source.
    # @param [::Vertx::Vertx] vertx the Vert.x instance
    # @param [Hash{String => Object}] config the configuration
    # @return [::VertxMongo::MongoClient] the client
    def self.create_non_shared(vertx=nil,config=nil)
      if vertx.class.method_defined?(:j_del) && config.class == Hash && !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtMongo::MongoClient.java_method(:createNonShared, [Java::IoVertxCore::Vertx.java_class,Java::IoVertxCoreJson::JsonObject.java_class]).call(vertx.j_del,::Vertx::Util::Utils.to_json_object(config)),::VertxMongo::MongoClient)
      end
      raise ArgumentError, "Invalid arguments when calling create_non_shared(#{vertx},#{config})"
    end
    #  Create a Mongo client which shares its data source with any other Mongo clients created with the same
    #  data source name
    # @param [::Vertx::Vertx] vertx the Vert.x instance
    # @param [Hash{String => Object}] config the configuration
    # @param [String] dataSourceName the data source name
    # @return [::VertxMongo::MongoClient] the client
    def self.create_shared(vertx=nil,config=nil,dataSourceName=nil)
      if vertx.class.method_defined?(:j_del) && config.class == Hash && !block_given? && dataSourceName == nil
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtMongo::MongoClient.java_method(:createShared, [Java::IoVertxCore::Vertx.java_class,Java::IoVertxCoreJson::JsonObject.java_class]).call(vertx.j_del,::Vertx::Util::Utils.to_json_object(config)),::VertxMongo::MongoClient)
      elsif vertx.class.method_defined?(:j_del) && config.class == Hash && dataSourceName.class == String && !block_given?
        return ::Vertx::Util::Utils.safe_create(Java::IoVertxExtMongo::MongoClient.java_method(:createShared, [Java::IoVertxCore::Vertx.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::java.lang.String.java_class]).call(vertx.j_del,::Vertx::Util::Utils.to_json_object(config),dataSourceName),::VertxMongo::MongoClient)
      end
      raise ArgumentError, "Invalid arguments when calling create_shared(#{vertx},#{config},#{dataSourceName})"
    end
    #  Save a document in the specified collection
    #  <p>
    #  This operation might change <i>_id</i> field of <i>document</i> parameter
    # @param [String] collection the collection
    # @param [Hash{String => Object}] document the document
    # @yield result handler will be provided with the id if document didn't already have one
    # @return [self]
    def save(collection=nil,document=nil)
      if collection.class == String && document.class == Hash && block_given?
        @j_del.java_method(:save, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(document),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling save(#{collection},#{document})"
    end
    #  Save a document in the specified collection with the specified write option
    #  <p>
    #  This operation might change <i>_id</i> field of <i>document</i> parameter
    # @param [String] collection the collection
    # @param [Hash{String => Object}] document the document
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption the write option to use
    # @yield result handler will be provided with the id if document didn't already have one
    # @return [self]
    def save_with_options(collection=nil,document=nil,writeOption=nil)
      if collection.class == String && document.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:saveWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(document),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption.to_s),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling save_with_options(#{collection},#{document},#{writeOption})"
    end
    #  Insert a document in the specified collection
    #  <p>
    #  This operation might change <i>_id</i> field of <i>document</i> parameter
    # @param [String] collection the collection
    # @param [Hash{String => Object}] document the document
    # @yield result handler will be provided with the id if document didn't already have one
    # @return [self]
    def insert(collection=nil,document=nil)
      if collection.class == String && document.class == Hash && block_given?
        @j_del.java_method(:insert, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(document),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling insert(#{collection},#{document})"
    end
    #  Insert a document in the specified collection with the specified write option
    #  <p>
    #  This operation might change <i>_id</i> field of <i>document</i> parameter
    # @param [String] collection the collection
    # @param [Hash{String => Object}] document the document
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption the write option to use
    # @yield result handler will be provided with the id if document didn't already have one
    # @return [self]
    def insert_with_options(collection=nil,document=nil,writeOption=nil)
      if collection.class == String && document.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:insertWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(document),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption.to_s),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling insert_with_options(#{collection},#{document},#{writeOption})"
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
      raise ArgumentError, "Invalid arguments when calling update(#{collection},#{query},#{update})"
    end
    #  Update matching documents in the specified collection and return the handler with MongoClientUpdateResult result
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match the documents
    # @param [Hash{String => Object}] update used to describe how the documents will be updated
    # @yield will be called when complete
    # @return [self]
    def update_collection(collection=nil,query=nil,update=nil)
      if collection.class == String && query.class == Hash && update.class == Hash && block_given?
        @j_del.java_method(:updateCollection, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(update),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling update_collection(#{collection},#{query},#{update})"
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
      raise ArgumentError, "Invalid arguments when calling update_with_options(#{collection},#{query},#{update},#{options})"
    end
    #  Update matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match the documents
    # @param [Hash{String => Object}] update used to describe how the documents will be updated
    # @param [Hash] options options to configure the update
    # @yield will be called when complete
    # @return [self]
    def update_collection_with_options(collection=nil,query=nil,update=nil,options=nil)
      if collection.class == String && query.class == Hash && update.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:updateCollectionWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::UpdateOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(update),Java::IoVertxExtMongo::UpdateOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling update_collection_with_options(#{collection},#{query},#{update},#{options})"
    end
    #  Replace matching documents in the specified collection
    #  <p>
    #  This operation might change <i>_id</i> field of <i>replace</i> parameter
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
      raise ArgumentError, "Invalid arguments when calling replace(#{collection},#{query},#{replace})"
    end
    #  Replace matching documents in the specified collection and return the handler with MongoClientUpdateResult result
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match the documents
    # @param [Hash{String => Object}] replace all matching documents will be replaced with this
    # @yield will be called when complete
    # @return [self]
    def replace_documents(collection=nil,query=nil,replace=nil)
      if collection.class == String && query.class == Hash && replace.class == Hash && block_given?
        @j_del.java_method(:replaceDocuments, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(replace),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling replace_documents(#{collection},#{query},#{replace})"
    end
    #  Replace matching documents in the specified collection, specifying options
    #  <p>
    #  This operation might change <i>_id</i> field of <i>replace</i> parameter
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
      raise ArgumentError, "Invalid arguments when calling replace_with_options(#{collection},#{query},#{replace},#{options})"
    end
    #  Replace matching documents in the specified collection, specifying options and return the handler with MongoClientUpdateResult result
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match the documents
    # @param [Hash{String => Object}] replace all matching documents will be replaced with this
    # @param [Hash] options options to configure the replace
    # @yield will be called when complete
    # @return [self]
    def replace_documents_with_options(collection=nil,query=nil,replace=nil,options=nil)
      if collection.class == String && query.class == Hash && replace.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:replaceDocumentsWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::UpdateOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(replace),Java::IoVertxExtMongo::UpdateOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling replace_documents_with_options(#{collection},#{query},#{replace},#{options})"
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
      raise ArgumentError, "Invalid arguments when calling find(#{collection},#{query})"
    end
    #  Find matching documents in the specified collection.
    #  This method use batchCursor for returning each found document.
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match documents
    # @yield will be provided with each found document
    # @return [self]
    def find_batch(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:findBatch, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_batch(#{collection},#{query})"
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
      raise ArgumentError, "Invalid arguments when calling find_with_options(#{collection},#{query},#{options})"
    end
    #  Find matching documents in the specified collection, specifying options.
    #  This method use batchCursor for returning each found document.
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match documents
    # @param [Hash] options options to configure the find
    # @yield will be provided with each found document
    # @return [self]
    def find_batch_with_options(collection=nil,query=nil,options=nil)
      if collection.class == String && query.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:findBatchWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::FindOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::FindOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_batch_with_options(#{collection},#{query},#{options})"
    end
    #  Find a single matching document in the specified collection
    #  <p>
    #  This operation might change <i>_id</i> field of <i>query</i> parameter
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
      raise ArgumentError, "Invalid arguments when calling find_one(#{collection},#{query},#{fields})"
    end
    #  Find a single matching document in the specified collection and update it.
    #  <p>
    #  This operation might change <i>_id</i> field of <i>query</i> parameter
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query the query used to match the document
    # @param [Hash{String => Object}] update used to describe how the documents will be updated
    # @yield will be provided with the document, if any
    # @return [self]
    def find_one_and_update(collection=nil,query=nil,update=nil)
      if collection.class == String && query.class == Hash && update.class == Hash && block_given?
        @j_del.java_method(:findOneAndUpdate, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(update),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_one_and_update(#{collection},#{query},#{update})"
    end
    #  Find a single matching document in the specified collection and update it.
    #  <p>
    #  This operation might change <i>_id</i> field of <i>query</i> parameter
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query the query used to match the document
    # @param [Hash{String => Object}] update used to describe how the documents will be updated
    # @param [Hash] findOptions options to configure the find
    # @param [Hash] updateOptions options to configure the update
    # @yield will be provided with the document, if any
    # @return [self]
    def find_one_and_update_with_options(collection=nil,query=nil,update=nil,findOptions=nil,updateOptions=nil)
      if collection.class == String && query.class == Hash && update.class == Hash && findOptions.class == Hash && updateOptions.class == Hash && block_given?
        @j_del.java_method(:findOneAndUpdateWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::FindOptions.java_class,Java::IoVertxExtMongo::UpdateOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(update),Java::IoVertxExtMongo::FindOptions.new(::Vertx::Util::Utils.to_json_object(findOptions)),Java::IoVertxExtMongo::UpdateOptions.new(::Vertx::Util::Utils.to_json_object(updateOptions)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_one_and_update_with_options(#{collection},#{query},#{update},#{findOptions},#{updateOptions})"
    end
    #  Find a single matching document in the specified collection and replace it.
    #  <p>
    #  This operation might change <i>_id</i> field of <i>query</i> parameter
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query the query used to match the document
    # @param [Hash{String => Object}] replace the replacement document
    # @yield will be provided with the document, if any
    # @return [self]
    def find_one_and_replace(collection=nil,query=nil,replace=nil)
      if collection.class == String && query.class == Hash && replace.class == Hash && block_given?
        @j_del.java_method(:findOneAndReplace, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(replace),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_one_and_replace(#{collection},#{query},#{replace})"
    end
    #  Find a single matching document in the specified collection and replace it.
    #  <p>
    #  This operation might change <i>_id</i> field of <i>query</i> parameter
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query the query used to match the document
    # @param [Hash{String => Object}] replace the replacement document
    # @param [Hash] findOptions options to configure the find
    # @param [Hash] updateOptions options to configure the update
    # @yield will be provided with the document, if any
    # @return [self]
    def find_one_and_replace_with_options(collection=nil,query=nil,replace=nil,findOptions=nil,updateOptions=nil)
      if collection.class == String && query.class == Hash && replace.class == Hash && findOptions.class == Hash && updateOptions.class == Hash && block_given?
        @j_del.java_method(:findOneAndReplaceWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::FindOptions.java_class,Java::IoVertxExtMongo::UpdateOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),::Vertx::Util::Utils.to_json_object(replace),Java::IoVertxExtMongo::FindOptions.new(::Vertx::Util::Utils.to_json_object(findOptions)),Java::IoVertxExtMongo::UpdateOptions.new(::Vertx::Util::Utils.to_json_object(updateOptions)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_one_and_replace_with_options(#{collection},#{query},#{replace},#{findOptions},#{updateOptions})"
    end
    #  Find a single matching document in the specified collection and delete it.
    #  <p>
    #  This operation might change <i>_id</i> field of <i>query</i> parameter
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query the query used to match the document
    # @yield will be provided with the deleted document, if any
    # @return [self]
    def find_one_and_delete(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:findOneAndDelete, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_one_and_delete(#{collection},#{query})"
    end
    #  Find a single matching document in the specified collection and delete it.
    #  <p>
    #  This operation might change <i>_id</i> field of <i>query</i> parameter
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query the query used to match the document
    # @param [Hash] findOptions options to configure the find
    # @yield will be provided with the deleted document, if any
    # @return [self]
    def find_one_and_delete_with_options(collection=nil,query=nil,findOptions=nil)
      if collection.class == String && query.class == Hash && findOptions.class == Hash && block_given?
        @j_del.java_method(:findOneAndDeleteWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::FindOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::FindOptions.new(::Vertx::Util::Utils.to_json_object(findOptions)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_one_and_delete_with_options(#{collection},#{query},#{findOptions})"
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
      raise ArgumentError, "Invalid arguments when calling count(#{collection},#{query})"
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
      raise ArgumentError, "Invalid arguments when calling remove(#{collection},#{query})"
    end
    #  Remove matching documents from a collection and return the handler with MongoClientDeleteResult result
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match documents
    # @yield will be called when complete
    # @return [self]
    def remove_documents(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:removeDocuments, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_documents(#{collection},#{query})"
    end
    #  Remove matching documents from a collection with the specified write option
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match documents
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption the write option to use
    # @yield will be called when complete
    # @return [self]
    def remove_with_options(collection=nil,query=nil,writeOption=nil)
      if collection.class == String && query.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:removeWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption.to_s),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_with_options(#{collection},#{query},#{writeOption})"
    end
    #  Remove matching documents from a collection with the specified write option and return the handler with MongoClientDeleteResult result
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match documents
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption the write option to use
    # @yield will be called when complete
    # @return [self]
    def remove_documents_with_options(collection=nil,query=nil,writeOption=nil)
      if collection.class == String && query.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:removeDocumentsWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption.to_s),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_documents_with_options(#{collection},#{query},#{writeOption})"
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
      raise ArgumentError, "Invalid arguments when calling remove_one(#{collection},#{query})"
    end
    #  Remove a single matching document from a collection and return the handler with MongoClientDeleteResult result
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match document
    # @yield will be called when complete
    # @return [self]
    def remove_document(collection=nil,query=nil)
      if collection.class == String && query.class == Hash && block_given?
        @j_del.java_method(:removeDocument, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_document(#{collection},#{query})"
    end
    #  Remove a single matching document from a collection with the specified write option
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match document
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption the write option to use
    # @yield will be called when complete
    # @return [self]
    def remove_one_with_options(collection=nil,query=nil,writeOption=nil)
      if collection.class == String && query.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:removeOneWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption.to_s),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_one_with_options(#{collection},#{query},#{writeOption})"
    end
    #  Remove a single matching document from a collection with the specified write option and return the handler with MongoClientDeleteResult result
    # @param [String] collection the collection
    # @param [Hash{String => Object}] query query used to match document
    # @param [:ACKNOWLEDGED,:UNACKNOWLEDGED,:FSYNCED,:JOURNALED,:REPLICA_ACKNOWLEDGED,:MAJORITY] writeOption the write option to use
    # @yield will be called when complete
    # @return [self]
    def remove_document_with_options(collection=nil,query=nil,writeOption=nil)
      if collection.class == String && query.class == Hash && writeOption.class == Symbol && block_given?
        @j_del.java_method(:removeDocumentWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::WriteOption.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(query),Java::IoVertxExtMongo::WriteOption.valueOf(writeOption.to_s),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.toJson.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling remove_document_with_options(#{collection},#{query},#{writeOption})"
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
      raise ArgumentError, "Invalid arguments when calling create_collection(#{collectionName})"
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
      raise ArgumentError, "Invalid arguments when calling drop_collection(#{collection})"
    end
    #  Creates an index.
    # @param [String] collection the collection
    # @param [Hash{String => Object}] key A document that contains the field and value pairs where the field is the index key and the value describes the type of index for that field. For an ascending index on a field, specify a value of 1; for descending index, specify a value of -1.
    # @yield will be called when complete
    # @return [self]
    def create_index(collection=nil,key=nil)
      if collection.class == String && key.class == Hash && block_given?
        @j_del.java_method(:createIndex, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(key),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling create_index(#{collection},#{key})"
    end
    #  Creates an index.
    # @param [String] collection the collection
    # @param [Hash{String => Object}] key A document that contains the field and value pairs where the field is the index key and the value describes the type of index for that field. For an ascending index on a field, specify a value of 1; for descending index, specify a value of -1.
    # @param [Hash] options the options for the index
    # @yield will be called when complete
    # @return [self]
    def create_index_with_options(collection=nil,key=nil,options=nil)
      if collection.class == String && key.class == Hash && options.class == Hash && block_given?
        @j_del.java_method(:createIndexWithOptions, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxExtMongo::IndexOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,::Vertx::Util::Utils.to_json_object(key),Java::IoVertxExtMongo::IndexOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling create_index_with_options(#{collection},#{key},#{options})"
    end
    #  Get all the indexes in this collection.
    # @param [String] collection the collection
    # @yield will be called when complete
    # @return [self]
    def list_indexes(collection=nil)
      if collection.class == String && block_given?
        @j_del.java_method(:listIndexes, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling list_indexes(#{collection})"
    end
    #  Drops the index given its name.
    # @param [String] collection the collection
    # @param [String] indexName the name of the index to remove
    # @yield will be called when complete
    # @return [self]
    def drop_index(collection=nil,indexName=nil)
      if collection.class == String && indexName.class == String && block_given?
        @j_del.java_method(:dropIndex, [Java::java.lang.String.java_class,Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,indexName,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling drop_index(#{collection},#{indexName})"
    end
    #  Run an arbitrary MongoDB command.
    # @param [String] commandName the name of the command
    # @param [Hash{String => Object}] command the command
    # @yield will be called with the result.
    # @return [self]
    def run_command(commandName=nil,command=nil)
      if commandName.class == String && command.class == Hash && block_given?
        @j_del.java_method(:runCommand, [Java::java.lang.String.java_class,Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(commandName,::Vertx::Util::Utils.to_json_object(command),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling run_command(#{commandName},#{command})"
    end
    #  Gets the distinct values of the specified field name.
    #  Return a JsonArray containing distinct values (eg: [ 1 , 89 ])
    # @param [String] collection the collection
    # @param [String] fieldName the field name
    # @param [String] resultClassname 
    # @yield will be provided with array of values.
    # @return [self]
    def distinct(collection=nil,fieldName=nil,resultClassname=nil)
      if collection.class == String && fieldName.class == String && resultClassname.class == String && block_given?
        @j_del.java_method(:distinct, [Java::java.lang.String.java_class,Java::java.lang.String.java_class,Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,fieldName,resultClassname,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling distinct(#{collection},#{fieldName},#{resultClassname})"
    end
    #  Gets the distinct values of the specified field name.
    #  This method use batchCursor for returning each found value.
    #  Each value is a json fragment with fieldName key (eg: {"num": 1}).
    # @param [String] collection the collection
    # @param [String] fieldName the field name
    # @param [String] resultClassname 
    # @yield will be provided with each found value
    # @return [self]
    def distinct_batch(collection=nil,fieldName=nil,resultClassname=nil)
      if collection.class == String && fieldName.class == String && resultClassname.class == String && block_given?
        @j_del.java_method(:distinctBatch, [Java::java.lang.String.java_class,Java::java.lang.String.java_class,Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(collection,fieldName,resultClassname,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result != nil ? JSON.parse(ar.result.encode) : nil : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling distinct_batch(#{collection},#{fieldName},#{resultClassname})"
    end
    #  Creates a {::VertxMongo::MongoGridFsClient} used to interact with Mongo GridFS.
    # @param [String] bucketName the name of the GridFS bucket
    # @yield the {::VertxMongo::MongoGridFsClient} to interact with the bucket named bucketName
    # @return [self]
    def create_grid_fs_bucket_service(bucketName=nil)
      if bucketName.class == String && block_given?
        @j_del.java_method(:createGridFsBucketService, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(bucketName,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ::Vertx::Util::Utils.safe_create(ar.result,::VertxMongo::MongoGridFsClient) : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling create_grid_fs_bucket_service(#{bucketName})"
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
