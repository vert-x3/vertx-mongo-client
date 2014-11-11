package io.vertx.ext.mongo;

import io.vertx.codegen.annotations.Options;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:nscavell@redhat.com">Nick Scavelli</a>
 */
@Options
public class FindOptions {

  private JsonObject fields;
  private JsonObject sort;
  private int limit = -1;
  private int skip;

  public FindOptions() {
  }

  public FindOptions(FindOptions other) {
    this.fields = other.fields;
    this.sort = other.sort;
    this.limit = other.limit;
    this.skip = other.skip;
  }

  public FindOptions(JsonObject json) {
    this.fields = json.getJsonObject("fields");
    this.sort = json.getJsonObject("sort");
    this.limit = json.getInteger("limit", limit);
    this.skip = json.getInteger("skip", skip);
  }

  public JsonObject toJson() {
    JsonObject json = new JsonObject();
    if (fields != null) {
      json.put("fields", fields);
    }
    if (sort != null) {
      json.put("sort", sort);
    }
    if (limit != -1) {
      json.put("limit", limit);
    }
    if (skip > 0) {
      json.put("skip", skip);
    }

    return json;
  }

  public JsonObject getFields() {
    return fields;
  }

  public FindOptions setFields(JsonObject fields) {
    this.fields = fields;
    return this;
  }

  public JsonObject getSort() {
    return sort;
  }

  public FindOptions setSort(JsonObject sort) {
    this.sort = sort;
    return this;
  }

  public int getLimit() {
    return limit;
  }

  public FindOptions setLimit(int limit) {
    this.limit = limit;
    return this;
  }

  public int getSkip() {
    return skip;
  }

  public FindOptions setSkip(int skip) {
    this.skip = skip;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FindOptions options = (FindOptions) o;

    if (limit != options.limit) return false;
    if (skip != options.skip) return false;
    if (fields != null ? !fields.equals(options.fields) : options.fields != null) return false;
    if (sort != null ? !sort.equals(options.sort) : options.sort != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = fields != null ? fields.hashCode() : 0;
    result = 31 * result + (sort != null ? sort.hashCode() : 0);
    result = 31 * result + limit;
    result = 31 * result + skip;
    return result;
  }
}
