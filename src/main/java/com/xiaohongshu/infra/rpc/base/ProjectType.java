/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.xiaohongshu.infra.rpc.base;


/**
 * multi-app 枚举类型
 * 
 */
public enum ProjectType implements org.apache.thrift.TEnum {
  XHS(0),
  TOP(1);

  private final int value;

  private ProjectType(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  @org.apache.thrift.annotation.Nullable
  public static ProjectType findByValue(int value) { 
    switch (value) {
      case 0:
        return XHS;
      case 1:
        return TOP;
      default:
        return null;
    }
  }
}
