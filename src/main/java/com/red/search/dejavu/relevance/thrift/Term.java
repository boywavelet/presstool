/**
 * Autogenerated by Thrift Compiler (0.12.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.red.search.dejavu.relevance.thrift;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
public class Term implements org.apache.thrift.TBase<Term, Term._Fields>, java.io.Serializable, Cloneable, Comparable<Term> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("Term");

  private static final org.apache.thrift.protocol.TField NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("name", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField WEIGHT_FIELD_DESC = new org.apache.thrift.protocol.TField("weight", org.apache.thrift.protocol.TType.DOUBLE, (short)2);
  private static final org.apache.thrift.protocol.TField IDF_WEIGHT_FIELD_DESC = new org.apache.thrift.protocol.TField("idf_weight", org.apache.thrift.protocol.TType.DOUBLE, (short)3);
  private static final org.apache.thrift.protocol.TField PHRASE_SLOP_FIELD_DESC = new org.apache.thrift.protocol.TField("phrase_slop", org.apache.thrift.protocol.TType.BOOL, (short)4);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new TermStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new TermTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable java.lang.String name; // required
  public double weight; // required
  public double idf_weight; // required
  public boolean phrase_slop; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    NAME((short)1, "name"),
    WEIGHT((short)2, "weight"),
    IDF_WEIGHT((short)3, "idf_weight"),
    PHRASE_SLOP((short)4, "phrase_slop");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // NAME
          return NAME;
        case 2: // WEIGHT
          return WEIGHT;
        case 3: // IDF_WEIGHT
          return IDF_WEIGHT;
        case 4: // PHRASE_SLOP
          return PHRASE_SLOP;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __WEIGHT_ISSET_ID = 0;
  private static final int __IDF_WEIGHT_ISSET_ID = 1;
  private static final int __PHRASE_SLOP_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.NAME, new org.apache.thrift.meta_data.FieldMetaData("name", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.WEIGHT, new org.apache.thrift.meta_data.FieldMetaData("weight", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields.IDF_WEIGHT, new org.apache.thrift.meta_data.FieldMetaData("idf_weight", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE)));
    tmpMap.put(_Fields.PHRASE_SLOP, new org.apache.thrift.meta_data.FieldMetaData("phrase_slop", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(Term.class, metaDataMap);
  }

  public Term() {
  }

  public Term(
    java.lang.String name,
    double weight,
    double idf_weight,
    boolean phrase_slop)
  {
    this();
    this.name = name;
    this.weight = weight;
    setWeightIsSet(true);
    this.idf_weight = idf_weight;
    setIdf_weightIsSet(true);
    this.phrase_slop = phrase_slop;
    setPhrase_slopIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public Term(Term other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetName()) {
      this.name = other.name;
    }
    this.weight = other.weight;
    this.idf_weight = other.idf_weight;
    this.phrase_slop = other.phrase_slop;
  }

  public Term deepCopy() {
    return new Term(this);
  }

  @Override
  public void clear() {
    this.name = null;
    setWeightIsSet(false);
    this.weight = 0.0;
    setIdf_weightIsSet(false);
    this.idf_weight = 0.0;
    setPhrase_slopIsSet(false);
    this.phrase_slop = false;
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.String getName() {
    return this.name;
  }

  public Term setName(@org.apache.thrift.annotation.Nullable java.lang.String name) {
    this.name = name;
    return this;
  }

  public void unsetName() {
    this.name = null;
  }

  /** Returns true if field name is set (has been assigned a value) and false otherwise */
  public boolean isSetName() {
    return this.name != null;
  }

  public void setNameIsSet(boolean value) {
    if (!value) {
      this.name = null;
    }
  }

  public double getWeight() {
    return this.weight;
  }

  public Term setWeight(double weight) {
    this.weight = weight;
    setWeightIsSet(true);
    return this;
  }

  public void unsetWeight() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __WEIGHT_ISSET_ID);
  }

  /** Returns true if field weight is set (has been assigned a value) and false otherwise */
  public boolean isSetWeight() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __WEIGHT_ISSET_ID);
  }

  public void setWeightIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __WEIGHT_ISSET_ID, value);
  }

  public double getIdf_weight() {
    return this.idf_weight;
  }

  public Term setIdf_weight(double idf_weight) {
    this.idf_weight = idf_weight;
    setIdf_weightIsSet(true);
    return this;
  }

  public void unsetIdf_weight() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __IDF_WEIGHT_ISSET_ID);
  }

  /** Returns true if field idf_weight is set (has been assigned a value) and false otherwise */
  public boolean isSetIdf_weight() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __IDF_WEIGHT_ISSET_ID);
  }

  public void setIdf_weightIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __IDF_WEIGHT_ISSET_ID, value);
  }

  public boolean isPhrase_slop() {
    return this.phrase_slop;
  }

  public Term setPhrase_slop(boolean phrase_slop) {
    this.phrase_slop = phrase_slop;
    setPhrase_slopIsSet(true);
    return this;
  }

  public void unsetPhrase_slop() {
    __isset_bitfield = org.apache.thrift.EncodingUtils.clearBit(__isset_bitfield, __PHRASE_SLOP_ISSET_ID);
  }

  /** Returns true if field phrase_slop is set (has been assigned a value) and false otherwise */
  public boolean isSetPhrase_slop() {
    return org.apache.thrift.EncodingUtils.testBit(__isset_bitfield, __PHRASE_SLOP_ISSET_ID);
  }

  public void setPhrase_slopIsSet(boolean value) {
    __isset_bitfield = org.apache.thrift.EncodingUtils.setBit(__isset_bitfield, __PHRASE_SLOP_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable java.lang.Object value) {
    switch (field) {
    case NAME:
      if (value == null) {
        unsetName();
      } else {
        setName((java.lang.String)value);
      }
      break;

    case WEIGHT:
      if (value == null) {
        unsetWeight();
      } else {
        setWeight((java.lang.Double)value);
      }
      break;

    case IDF_WEIGHT:
      if (value == null) {
        unsetIdf_weight();
      } else {
        setIdf_weight((java.lang.Double)value);
      }
      break;

    case PHRASE_SLOP:
      if (value == null) {
        unsetPhrase_slop();
      } else {
        setPhrase_slop((java.lang.Boolean)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case NAME:
      return getName();

    case WEIGHT:
      return getWeight();

    case IDF_WEIGHT:
      return getIdf_weight();

    case PHRASE_SLOP:
      return isPhrase_slop();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case NAME:
      return isSetName();
    case WEIGHT:
      return isSetWeight();
    case IDF_WEIGHT:
      return isSetIdf_weight();
    case PHRASE_SLOP:
      return isSetPhrase_slop();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof Term)
      return this.equals((Term)that);
    return false;
  }

  public boolean equals(Term that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_name = true && this.isSetName();
    boolean that_present_name = true && that.isSetName();
    if (this_present_name || that_present_name) {
      if (!(this_present_name && that_present_name))
        return false;
      if (!this.name.equals(that.name))
        return false;
    }

    boolean this_present_weight = true;
    boolean that_present_weight = true;
    if (this_present_weight || that_present_weight) {
      if (!(this_present_weight && that_present_weight))
        return false;
      if (this.weight != that.weight)
        return false;
    }

    boolean this_present_idf_weight = true;
    boolean that_present_idf_weight = true;
    if (this_present_idf_weight || that_present_idf_weight) {
      if (!(this_present_idf_weight && that_present_idf_weight))
        return false;
      if (this.idf_weight != that.idf_weight)
        return false;
    }

    boolean this_present_phrase_slop = true;
    boolean that_present_phrase_slop = true;
    if (this_present_phrase_slop || that_present_phrase_slop) {
      if (!(this_present_phrase_slop && that_present_phrase_slop))
        return false;
      if (this.phrase_slop != that.phrase_slop)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetName()) ? 131071 : 524287);
    if (isSetName())
      hashCode = hashCode * 8191 + name.hashCode();

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(weight);

    hashCode = hashCode * 8191 + org.apache.thrift.TBaseHelper.hashCode(idf_weight);

    hashCode = hashCode * 8191 + ((phrase_slop) ? 131071 : 524287);

    return hashCode;
  }

  @Override
  public int compareTo(Term other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetName()).compareTo(other.isSetName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.name, other.name);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetWeight()).compareTo(other.isSetWeight());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetWeight()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.weight, other.weight);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetIdf_weight()).compareTo(other.isSetIdf_weight());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIdf_weight()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.idf_weight, other.idf_weight);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetPhrase_slop()).compareTo(other.isSetPhrase_slop());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPhrase_slop()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.phrase_slop, other.phrase_slop);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("Term(");
    boolean first = true;

    sb.append("name:");
    if (this.name == null) {
      sb.append("null");
    } else {
      sb.append(this.name);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("weight:");
    sb.append(this.weight);
    first = false;
    if (!first) sb.append(", ");
    sb.append("idf_weight:");
    sb.append(this.idf_weight);
    first = false;
    if (!first) sb.append(", ");
    sb.append("phrase_slop:");
    sb.append(this.phrase_slop);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TermStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TermStandardScheme getScheme() {
      return new TermStandardScheme();
    }
  }

  private static class TermStandardScheme extends org.apache.thrift.scheme.StandardScheme<Term> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, Term struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.name = iprot.readString();
              struct.setNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // WEIGHT
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.weight = iprot.readDouble();
              struct.setWeightIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // IDF_WEIGHT
            if (schemeField.type == org.apache.thrift.protocol.TType.DOUBLE) {
              struct.idf_weight = iprot.readDouble();
              struct.setIdf_weightIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // PHRASE_SLOP
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.phrase_slop = iprot.readBool();
              struct.setPhrase_slopIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, Term struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.name != null) {
        oprot.writeFieldBegin(NAME_FIELD_DESC);
        oprot.writeString(struct.name);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(WEIGHT_FIELD_DESC);
      oprot.writeDouble(struct.weight);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(IDF_WEIGHT_FIELD_DESC);
      oprot.writeDouble(struct.idf_weight);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(PHRASE_SLOP_FIELD_DESC);
      oprot.writeBool(struct.phrase_slop);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TermTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public TermTupleScheme getScheme() {
      return new TermTupleScheme();
    }
  }

  private static class TermTupleScheme extends org.apache.thrift.scheme.TupleScheme<Term> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, Term struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetName()) {
        optionals.set(0);
      }
      if (struct.isSetWeight()) {
        optionals.set(1);
      }
      if (struct.isSetIdf_weight()) {
        optionals.set(2);
      }
      if (struct.isSetPhrase_slop()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetName()) {
        oprot.writeString(struct.name);
      }
      if (struct.isSetWeight()) {
        oprot.writeDouble(struct.weight);
      }
      if (struct.isSetIdf_weight()) {
        oprot.writeDouble(struct.idf_weight);
      }
      if (struct.isSetPhrase_slop()) {
        oprot.writeBool(struct.phrase_slop);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, Term struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.name = iprot.readString();
        struct.setNameIsSet(true);
      }
      if (incoming.get(1)) {
        struct.weight = iprot.readDouble();
        struct.setWeightIsSet(true);
      }
      if (incoming.get(2)) {
        struct.idf_weight = iprot.readDouble();
        struct.setIdf_weightIsSet(true);
      }
      if (incoming.get(3)) {
        struct.phrase_slop = iprot.readBool();
        struct.setPhrase_slopIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}
