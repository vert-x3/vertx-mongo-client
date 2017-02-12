require 'vertx-mongo/mongo_grid_fs_download'
require 'vertx-mongo/mongo_grid_fs_upload'
require 'vertx/util/utils.rb'
# Generated from io.vertx.ext.mongo.MongoGridFsClient
module VertxMongo
  class MongoGridFsClient
    # @private
    # @param j_del [::VertxMongo::MongoGridFsClient] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxMongo::MongoGridFsClient] the underlying java delegate
    def j_del
      @j_del
    end
    @@j_api_type = Object.new
    def @@j_api_type.accept?(obj)
      obj.class == MongoGridFsClient
    end
    def @@j_api_type.wrap(obj)
      MongoGridFsClient.new(obj)
    end
    def @@j_api_type.unwrap(obj)
      obj.j_del
    end
    def self.j_api_type
      @@j_api_type
    end
    def self.j_class
      Java::IoVertxExtMongo::MongoGridFsClient.java_class
    end
    #  Deletes a file by it's ID
    # @param [String] id the identifier of the file
    # @yield will be called when the file is deleted
    # @return [self]
    def delete(id=nil)
      if id.class == String && block_given?
        @j_del.java_method(:delete, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(id,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling delete(#{id})"
    end
    #  Downloads a file into a buffer.
    # @param [String] fileName the name of the file to download
    # @yield called when a {::VertxMongo::MongoGridFsDownload} is ready that can be used to accept the buffer
    # @return [self]
    def download_buffer(fileName=nil)
      if fileName.class == String && block_given?
        @j_del.java_method(:downloadBuffer, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(fileName,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ::Vertx::Util::Utils.safe_create(ar.result,::VertxMongo::MongoGridFsDownload) : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling download_buffer(#{fileName})"
    end
    #  Downloads a file.
    # @param [String] fileName the name of the file to download
    # @yield called when the file is downloaded and returns the length in bytes
    # @return [self]
    def download_file(fileName=nil)
      if fileName.class == String && block_given?
        @j_del.java_method(:downloadFile, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(fileName,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling download_file(#{fileName})"
    end
    #  Downloads a file and gives it a new name.
    # @param [String] fileName the name of the file to download
    # @param [String] newFileName the name the file should be saved as
    # @yield called when the file is downloaded and returns the length in bytes
    # @return [self]
    def download_file_as(fileName=nil,newFileName=nil)
      if fileName.class == String && newFileName.class == String && block_given?
        @j_del.java_method(:downloadFileAs, [Java::java.lang.String.java_class,Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(fileName,newFileName,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling download_file_as(#{fileName},#{newFileName})"
    end
    #  Drops the entire file bucket with all of its contents
    # @yield called when the bucket is dropped
    # @return [self]
    def drop
      if block_given?
        @j_del.java_method(:drop, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling drop()"
    end
    #  Finds all file ids in the bucket
    # @yield called when the list of file ids is available
    # @return [self]
    def find_all_ids
      if block_given?
        @j_del.java_method(:findAllIds, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_all_ids()"
    end
    #  Finds all file ids that match a query.
    # @param [Hash{String => Object}] query a bson query expressed as json that will be used to match files
    # @yield called when the list of file ids is available
    # @return [self]
    def find_ids(query=nil)
      if query.class == Hash && block_given?
        @j_del.java_method(:findIds, [Java::IoVertxCoreJson::JsonObject.java_class,Java::IoVertxCore::Handler.java_class]).call(::Vertx::Util::Utils.to_json_object(query),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result.to_a.map { |elt| elt } : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling find_ids(#{query})"
    end
    #  Upload a file using a buffer
    # @param [String] fileName the name of the file to store in gridfs
    # @yield a {::VertxMongo::MongoGridFsUpload} to interact with with whilst uploaded contents via buffer
    # @return [self]
    def upload_buffer(fileName=nil)
      if fileName.class == String && block_given?
        @j_del.java_method(:uploadBuffer, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(fileName,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ::Vertx::Util::Utils.safe_create(ar.result,::VertxMongo::MongoGridFsUpload) : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling upload_buffer(#{fileName})"
    end
    #  Upload a file using a buffer with options
    # @param [String] fileName the name of the file to store in gridfs
    # @param [Hash] options {Hash} for specifying metadata and chunk size
    # @yield a {::VertxMongo::MongoGridFsUpload} to interact with with whilst uploaded contents via buffer
    # @return [self]
    def upload_buffer_with_options(fileName=nil,options=nil)
      if fileName.class == String && options.class == Hash && block_given?
        @j_del.java_method(:uploadBufferWithOptions, [Java::java.lang.String.java_class,Java::IoVertxExtMongo::UploadOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(fileName,Java::IoVertxExtMongo::UploadOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ::Vertx::Util::Utils.safe_create(ar.result,::VertxMongo::MongoGridFsUpload) : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling upload_buffer_with_options(#{fileName},#{options})"
    end
    #  Upload a file to gridfs
    # @param [String] fileName the name of the file to store in gridfs
    # @yield the id of the file that was uploaded
    # @return [self]
    def upload_file(fileName=nil)
      if fileName.class == String && block_given?
        @j_del.java_method(:uploadFile, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(fileName,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling upload_file(#{fileName})"
    end
    #  Upload a file to gridfs with options
    # @param [String] fileName the name of the file to store in gridfs
    # @param [Hash] options {Hash} for specifying metadata and chunk size
    # @yield the id of the file that was uploaded
    # @return [self]
    def upload_file_with_options(fileName=nil,options=nil)
      if fileName.class == String && options.class == Hash && block_given?
        @j_del.java_method(:uploadFileWithOptions, [Java::java.lang.String.java_class,Java::IoVertxExtMongo::UploadOptions.java_class,Java::IoVertxCore::Handler.java_class]).call(fileName,Java::IoVertxExtMongo::UploadOptions.new(::Vertx::Util::Utils.to_json_object(options)),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling upload_file_with_options(#{fileName},#{options})"
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
