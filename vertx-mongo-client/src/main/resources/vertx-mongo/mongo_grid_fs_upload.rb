require 'vertx/util/utils.rb'
# Generated from io.vertx.ext.mongo.MongoGridFsUpload
module VertxMongo
  class MongoGridFsUpload
    # @private
    # @param j_del [::VertxMongo::MongoGridFsUpload] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxMongo::MongoGridFsUpload] the underlying java delegate
    def j_del
      @j_del
    end
    @@j_api_type = Object.new
    def @@j_api_type.accept?(obj)
      obj.class == MongoGridFsUpload
    end
    def @@j_api_type.wrap(obj)
      MongoGridFsUpload.new(obj)
    end
    def @@j_api_type.unwrap(obj)
      obj.j_del
    end
    def self.j_api_type
      @@j_api_type
    end
    def self.j_class
      Java::IoVertxExtMongo::MongoGridFsUpload.java_class
    end
    #  Accepts a string of base 64 encoded bytes and adds them to the file to be saved in gridfs.
    # @param [String] base64EncodedBytes string of base 64 encoded bytes
    # @yield the number of bytes saved to the file.
    # @return [self]
    def upload_buffer(base64EncodedBytes=nil)
      if base64EncodedBytes.class == String && block_given?
        @j_del.java_method(:uploadBuffer, [Java::java.lang.String.java_class,Java::IoVertxCore::Handler.java_class]).call(base64EncodedBytes,(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling upload_buffer(#{base64EncodedBytes})"
    end
    #  Ends the upload of bytes saved in gridfs.
    # @yield the ID of the file saved in gridfs.
    # @return [self]
    def end
      if block_given?
        @j_del.java_method(:end, [Java::IoVertxCore::Handler.java_class]).call((Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling end()"
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
