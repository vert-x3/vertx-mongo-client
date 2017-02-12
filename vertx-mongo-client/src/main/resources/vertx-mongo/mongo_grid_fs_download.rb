require 'vertx/util/utils.rb'
# Generated from io.vertx.ext.mongo.MongoGridFsDownload
module VertxMongo
  class MongoGridFsDownload
    # @private
    # @param j_del [::VertxMongo::MongoGridFsDownload] the java delegate
    def initialize(j_del)
      @j_del = j_del
    end
    # @private
    # @return [::VertxMongo::MongoGridFsDownload] the underlying java delegate
    def j_del
      @j_del
    end
    @@j_api_type = Object.new
    def @@j_api_type.accept?(obj)
      obj.class == MongoGridFsDownload
    end
    def @@j_api_type.wrap(obj)
      MongoGridFsDownload.new(obj)
    end
    def @@j_api_type.unwrap(obj)
      obj.j_del
    end
    def self.j_api_type
      @@j_api_type
    end
    def self.j_class
      Java::IoVertxExtMongo::MongoGridFsDownload.java_class
    end
    #  Read bytes and returns them as a base 64 encoded string. Use a decoder to turn
    #  them back into an array of bytes.
    # @param [Fixnum] bufferSize the maximum number of bytes to return
    # @yield a string of base 64 encoded bytes.
    # @return [self]
    def read(bufferSize=nil)
      if bufferSize.class == Fixnum && block_given?
        @j_del.java_method(:read, [Java::JavaLang::Integer.java_class,Java::IoVertxCore::Handler.java_class]).call(::Vertx::Util::Utils.to_integer(bufferSize),(Proc.new { |ar| yield(ar.failed ? ar.cause : nil, ar.succeeded ? ar.result : nil) }))
        return self
      end
      raise ArgumentError, "Invalid arguments when calling read(#{bufferSize})"
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
