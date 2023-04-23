package cn.edu.sustech.cs209.chatting.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Request implements Serializable {

  private Type type;
  private Map<String, Object> attributesMap;

  public Request() {
    this.attributesMap = new HashMap<String, Object>();
  }

  public Request(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Map<String, Object> getAttributesMap() {
    return attributesMap;
  }

  public Object getAttribute(String name) {
    return this.attributesMap.get(name);
  }

  public void setAttribute(String name, Object value) {
    this.attributesMap.put(name, value);
  }

  public void removeAttribute(String name) {
    this.attributesMap.remove(name);
  }

  public void clearAttribute() {
    this.attributesMap.clear();
  }
}
