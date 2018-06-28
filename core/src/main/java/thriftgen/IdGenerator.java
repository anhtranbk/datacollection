/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package thriftgen;

import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.server.AbstractNonblockingServer.AsyncFrameBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2018-02-08")
public class IdGenerator {

  public interface Iface {

    public long generate(List<String> seeds, long def_val) throws TException;

  }

  public interface AsyncIface {

    public void generate(List<String> seeds, long def_val, AsyncMethodCallback resultHandler) throws TException;

  }

  public static class Client extends org.apache.thrift.TServiceClient implements Iface {
    public static class Factory implements org.apache.thrift.TServiceClientFactory<Client> {
      public Factory() {}
      public Client getClient(org.apache.thrift.protocol.TProtocol prot) {
        return new Client(prot);
      }
      public Client getClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
        return new Client(iprot, oprot);
      }
    }

    public Client(org.apache.thrift.protocol.TProtocol prot)
    {
      super(prot, prot);
    }

    public Client(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
      super(iprot, oprot);
    }

    public long generate(List<String> seeds, long def_val) throws TException
    {
      send_generate(seeds, def_val);
      return recv_generate();
    }

    public void send_generate(List<String> seeds, long def_val) throws TException
    {
      generate_args args = new generate_args();
      args.setSeeds(seeds);
      args.setDef_val(def_val);
      sendBase("generate", args);
    }

    public long recv_generate() throws TException
    {
      generate_result result = new generate_result();
      receiveBase(result, "generate");
      if (result.isSetSuccess()) {
        return result.success;
      }
      throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "generate failed: unknown result");
    }

  }
  public static class AsyncClient extends org.apache.thrift.async.TAsyncClient implements AsyncIface {
    public static class Factory implements org.apache.thrift.async.TAsyncClientFactory<AsyncClient> {
      private org.apache.thrift.async.TAsyncClientManager clientManager;
      private org.apache.thrift.protocol.TProtocolFactory protocolFactory;
      public Factory(org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.protocol.TProtocolFactory protocolFactory) {
        this.clientManager = clientManager;
        this.protocolFactory = protocolFactory;
      }
      public AsyncClient getAsyncClient(org.apache.thrift.transport.TNonblockingTransport transport) {
        return new AsyncClient(protocolFactory, clientManager, transport);
      }
    }

    public AsyncClient(org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.transport.TNonblockingTransport transport) {
      super(protocolFactory, clientManager, transport);
    }

    public void generate(List<String> seeds, long def_val, AsyncMethodCallback resultHandler) throws TException {
      checkReady();
      generate_call method_call = new generate_call(seeds, def_val, resultHandler, this, ___protocolFactory, ___transport);
      this.___currentMethod = method_call;
      ___manager.call(method_call);
    }

    public static class generate_call extends org.apache.thrift.async.TAsyncMethodCall {
      private List<String> seeds;
      private long def_val;
      public generate_call(List<String> seeds, long def_val, AsyncMethodCallback resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws TException {
        super(client, protocolFactory, transport, resultHandler, false);
        this.seeds = seeds;
        this.def_val = def_val;
      }

      public void write_args(org.apache.thrift.protocol.TProtocol prot) throws TException {
        prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("generate", org.apache.thrift.protocol.TMessageType.CALL, 0));
        generate_args args = new generate_args();
        args.setSeeds(seeds);
        args.setDef_val(def_val);
        args.write(prot);
        prot.writeMessageEnd();
      }

      public long getResult() throws TException {
        if (getState() != State.RESPONSE_READ) {
          throw new IllegalStateException("Method call not finished!");
        }
        org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
        org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
        return (new Client(prot)).recv_generate();
      }
    }

  }

  public static class Processor<I extends Iface> extends org.apache.thrift.TBaseProcessor<I> implements org.apache.thrift.TProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class.getName());
    public Processor(I iface) {
      super(iface, getProcessMap(new HashMap<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
    }

    protected Processor(I iface, Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      super(iface, getProcessMap(processMap));
    }

    private static <I extends Iface> Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> getProcessMap(Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      processMap.put("generate", new generate());
      return processMap;
    }

    public static class generate<I extends Iface> extends org.apache.thrift.ProcessFunction<I, generate_args> {
      public generate() {
        super("generate");
      }

      public generate_args getEmptyArgsInstance() {
        return new generate_args();
      }

      protected boolean isOneway() {
        return false;
      }

      public generate_result getResult(I iface, generate_args args) throws TException {
        generate_result result = new generate_result();
        result.success = iface.generate(args.seeds, args.def_val);
        result.setSuccessIsSet(true);
        return result;
      }
    }

  }

  public static class AsyncProcessor<I extends AsyncIface> extends org.apache.thrift.TBaseAsyncProcessor<I> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncProcessor.class.getName());
    public AsyncProcessor(I iface) {
      super(iface, getProcessMap(new HashMap<String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>>()));
    }

    protected AsyncProcessor(I iface, Map<String,  org.apache.thrift.AsyncProcessFunction<I, ? extends  org.apache.thrift.TBase, ?>> processMap) {
      super(iface, getProcessMap(processMap));
    }

    private static <I extends AsyncIface> Map<String,  org.apache.thrift.AsyncProcessFunction<I, ? extends  org.apache.thrift.TBase,?>> getProcessMap(Map<String,  org.apache.thrift.AsyncProcessFunction<I, ? extends  org.apache.thrift.TBase, ?>> processMap) {
      processMap.put("generate", new generate());
      return processMap;
    }

    public static class generate<I extends AsyncIface> extends org.apache.thrift.AsyncProcessFunction<I, generate_args, Long> {
      public generate() {
        super("generate");
      }

      public generate_args getEmptyArgsInstance() {
        return new generate_args();
      }

      public AsyncMethodCallback<Long> getResultHandler(final AsyncFrameBuffer fb, final int seqid) {
        final org.apache.thrift.AsyncProcessFunction fcall = this;
        return new AsyncMethodCallback<Long>() {
          public void onComplete(Long o) {
            generate_result result = new generate_result();
            result.success = o;
            result.setSuccessIsSet(true);
            try {
              fcall.sendResponse(fb,result, org.apache.thrift.protocol.TMessageType.REPLY,seqid);
              return;
            } catch (Exception e) {
              LOGGER.error("Exception writing to internal frame buffer", e);
            }
            fb.close();
          }
          public void onError(Exception e) {
            byte msgType = org.apache.thrift.protocol.TMessageType.REPLY;
            org.apache.thrift.TBase msg;
            generate_result result = new generate_result();
            {
              msgType = org.apache.thrift.protocol.TMessageType.EXCEPTION;
              msg = (org.apache.thrift.TBase)new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.INTERNAL_ERROR, e.getMessage());
            }
            try {
              fcall.sendResponse(fb,msg,msgType,seqid);
              return;
            } catch (Exception ex) {
              LOGGER.error("Exception writing to internal frame buffer", ex);
            }
            fb.close();
          }
        };
      }

      protected boolean isOneway() {
        return false;
      }

      public void start(I iface, generate_args args, AsyncMethodCallback<Long> resultHandler) throws TException {
        iface.generate(args.seeds, args.def_val,resultHandler);
      }
    }

  }

  public static class generate_args implements org.apache.thrift.TBase<generate_args, generate_args._Fields>, java.io.Serializable, Cloneable, Comparable<generate_args>   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("generate_args");

    private static final org.apache.thrift.protocol.TField SEEDS_FIELD_DESC = new org.apache.thrift.protocol.TField("seeds", org.apache.thrift.protocol.TType.LIST, (short)1);
    private static final org.apache.thrift.protocol.TField DEF_VAL_FIELD_DESC = new org.apache.thrift.protocol.TField("def_val", org.apache.thrift.protocol.TType.I64, (short)2);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new generate_argsStandardSchemeFactory());
      schemes.put(TupleScheme.class, new generate_argsTupleSchemeFactory());
    }

    public List<String> seeds; // required
    public long def_val; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      SEEDS((short)1, "seeds"),
      DEF_VAL((short)2, "def_val");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 1: // SEEDS
            return SEEDS;
          case 2: // DEF_VAL
            return DEF_VAL;
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
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    private static final int __DEF_VAL_ISSET_ID = 0;
    private byte __isset_bitfield = 0;
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.SEEDS, new org.apache.thrift.meta_data.FieldMetaData("seeds", org.apache.thrift.TFieldRequirementType.DEFAULT,
          new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST,
              new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
      tmpMap.put(_Fields.DEF_VAL, new org.apache.thrift.meta_data.FieldMetaData("def_val", org.apache.thrift.TFieldRequirementType.DEFAULT,
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(generate_args.class, metaDataMap);
    }

    public generate_args() {
    }

    public generate_args(
      List<String> seeds,
      long def_val)
    {
      this();
      this.seeds = seeds;
      this.def_val = def_val;
      setDef_valIsSet(true);
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public generate_args(generate_args other) {
      __isset_bitfield = other.__isset_bitfield;
      if (other.isSetSeeds()) {
        List<String> __this__seeds = new ArrayList<String>(other.seeds);
        this.seeds = __this__seeds;
      }
      this.def_val = other.def_val;
    }

    public generate_args deepCopy() {
      return new generate_args(this);
    }

    @Override
    public void clear() {
      this.seeds = null;
      setDef_valIsSet(false);
      this.def_val = 0;
    }

    public int getSeedsSize() {
      return (this.seeds == null) ? 0 : this.seeds.size();
    }

    public java.util.Iterator<String> getSeedsIterator() {
      return (this.seeds == null) ? null : this.seeds.iterator();
    }

    public void addToSeeds(String elem) {
      if (this.seeds == null) {
        this.seeds = new ArrayList<String>();
      }
      this.seeds.add(elem);
    }

    public List<String> getSeeds() {
      return this.seeds;
    }

    public generate_args setSeeds(List<String> seeds) {
      this.seeds = seeds;
      return this;
    }

    public void unsetSeeds() {
      this.seeds = null;
    }

    /** Returns true if field seeds is set (has been assigned a value) and false otherwise */
    public boolean isSetSeeds() {
      return this.seeds != null;
    }

    public void setSeedsIsSet(boolean value) {
      if (!value) {
        this.seeds = null;
      }
    }

    public long getDef_val() {
      return this.def_val;
    }

    public generate_args setDef_val(long def_val) {
      this.def_val = def_val;
      setDef_valIsSet(true);
      return this;
    }

    public void unsetDef_val() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __DEF_VAL_ISSET_ID);
    }

    /** Returns true if field def_val is set (has been assigned a value) and false otherwise */
    public boolean isSetDef_val() {
      return EncodingUtils.testBit(__isset_bitfield, __DEF_VAL_ISSET_ID);
    }

    public void setDef_valIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __DEF_VAL_ISSET_ID, value);
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case SEEDS:
        if (value == null) {
          unsetSeeds();
        } else {
          setSeeds((List<String>)value);
        }
        break;

      case DEF_VAL:
        if (value == null) {
          unsetDef_val();
        } else {
          setDef_val((Long)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case SEEDS:
        return getSeeds();

      case DEF_VAL:
        return getDef_val();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case SEEDS:
        return isSetSeeds();
      case DEF_VAL:
        return isSetDef_val();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof generate_args)
        return this.equals((generate_args)that);
      return false;
    }

    public boolean equals(generate_args that) {
      if (that == null)
        return false;

      boolean this_present_seeds = true && this.isSetSeeds();
      boolean that_present_seeds = true && that.isSetSeeds();
      if (this_present_seeds || that_present_seeds) {
        if (!(this_present_seeds && that_present_seeds))
          return false;
        if (!this.seeds.equals(that.seeds))
          return false;
      }

      boolean this_present_def_val = true;
      boolean that_present_def_val = true;
      if (this_present_def_val || that_present_def_val) {
        if (!(this_present_def_val && that_present_def_val))
          return false;
        if (this.def_val != that.def_val)
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      List<Object> list = new ArrayList<Object>();

      boolean present_seeds = true && (isSetSeeds());
      list.add(present_seeds);
      if (present_seeds)
        list.add(seeds);

      boolean present_def_val = true;
      list.add(present_def_val);
      if (present_def_val)
        list.add(def_val);

      return list.hashCode();
    }

    @Override
    public int compareTo(generate_args other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;

      lastComparison = Boolean.valueOf(isSetSeeds()).compareTo(other.isSetSeeds());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetSeeds()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.seeds, other.seeds);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetDef_val()).compareTo(other.isSetDef_val());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetDef_val()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.def_val, other.def_val);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("generate_args(");
      boolean first = true;

      sb.append("seeds:");
      if (this.seeds == null) {
        sb.append("null");
      } else {
        sb.append(this.seeds);
      }
      first = false;
      if (!first) sb.append(", ");
      sb.append("def_val:");
      sb.append(this.def_val);
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws TException {
      // check for required fields
      // check for sub-struct validity
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
        __isset_bitfield = 0;
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class generate_argsStandardSchemeFactory implements SchemeFactory {
      public generate_argsStandardScheme getScheme() {
        return new generate_argsStandardScheme();
      }
    }

    private static class generate_argsStandardScheme extends StandardScheme<generate_args> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, generate_args struct) throws TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
            break;
          }
          switch (schemeField.id) {
            case 1: // SEEDS
              if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
                {
                  org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                  struct.seeds = new ArrayList<String>(_list0.size);
                  String _elem1;
                  for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                  {
                    _elem1 = iprot.readString();
                    struct.seeds.add(_elem1);
                  }
                  iprot.readListEnd();
                }
                struct.setSeedsIsSet(true);
              } else {
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 2: // DEF_VAL
              if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
                struct.def_val = iprot.readI64();
                struct.setDef_valIsSet(true);
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

      public void write(org.apache.thrift.protocol.TProtocol oprot, generate_args struct) throws TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.seeds != null) {
          oprot.writeFieldBegin(SEEDS_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.seeds.size()));
            for (String _iter3 : struct.seeds)
            {
              oprot.writeString(_iter3);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(DEF_VAL_FIELD_DESC);
        oprot.writeI64(struct.def_val);
        oprot.writeFieldEnd();
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class generate_argsTupleSchemeFactory implements SchemeFactory {
      public generate_argsTupleScheme getScheme() {
        return new generate_argsTupleScheme();
      }
    }

    private static class generate_argsTupleScheme extends TupleScheme<generate_args> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, generate_args struct) throws TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetSeeds()) {
          optionals.set(0);
        }
        if (struct.isSetDef_val()) {
          optionals.set(1);
        }
        oprot.writeBitSet(optionals, 2);
        if (struct.isSetSeeds()) {
          {
            oprot.writeI32(struct.seeds.size());
            for (String _iter4 : struct.seeds)
            {
              oprot.writeString(_iter4);
            }
          }
        }
        if (struct.isSetDef_val()) {
          oprot.writeI64(struct.def_val);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, generate_args struct) throws TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(2);
        if (incoming.get(0)) {
          {
            org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
            struct.seeds = new ArrayList<String>(_list5.size);
            String _elem6;
            for (int _i7 = 0; _i7 < _list5.size; ++_i7)
            {
              _elem6 = iprot.readString();
              struct.seeds.add(_elem6);
            }
          }
          struct.setSeedsIsSet(true);
        }
        if (incoming.get(1)) {
          struct.def_val = iprot.readI64();
          struct.setDef_valIsSet(true);
        }
      }
    }

  }

  public static class generate_result implements org.apache.thrift.TBase<generate_result, generate_result._Fields>, java.io.Serializable, Cloneable, Comparable<generate_result>   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("generate_result");

    private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField("success", org.apache.thrift.protocol.TType.I64, (short)0);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new generate_resultStandardSchemeFactory());
      schemes.put(TupleScheme.class, new generate_resultTupleSchemeFactory());
    }

    public long success; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      SUCCESS((short)0, "success");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 0: // SUCCESS
            return SUCCESS;
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
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    private static final int __SUCCESS_ISSET_ID = 0;
    private byte __isset_bitfield = 0;
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.SUCCESS, new org.apache.thrift.meta_data.FieldMetaData("success", org.apache.thrift.TFieldRequirementType.DEFAULT,
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(generate_result.class, metaDataMap);
    }

    public generate_result() {
    }

    public generate_result(
      long success)
    {
      this();
      this.success = success;
      setSuccessIsSet(true);
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public generate_result(generate_result other) {
      __isset_bitfield = other.__isset_bitfield;
      this.success = other.success;
    }

    public generate_result deepCopy() {
      return new generate_result(this);
    }

    @Override
    public void clear() {
      setSuccessIsSet(false);
      this.success = 0;
    }

    public long getSuccess() {
      return this.success;
    }

    public generate_result setSuccess(long success) {
      this.success = success;
      setSuccessIsSet(true);
      return this;
    }

    public void unsetSuccess() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __SUCCESS_ISSET_ID);
    }

    /** Returns true if field success is set (has been assigned a value) and false otherwise */
    public boolean isSetSuccess() {
      return EncodingUtils.testBit(__isset_bitfield, __SUCCESS_ISSET_ID);
    }

    public void setSuccessIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __SUCCESS_ISSET_ID, value);
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case SUCCESS:
        if (value == null) {
          unsetSuccess();
        } else {
          setSuccess((Long)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case SUCCESS:
        return getSuccess();

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case SUCCESS:
        return isSetSuccess();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof generate_result)
        return this.equals((generate_result)that);
      return false;
    }

    public boolean equals(generate_result that) {
      if (that == null)
        return false;

      boolean this_present_success = true;
      boolean that_present_success = true;
      if (this_present_success || that_present_success) {
        if (!(this_present_success && that_present_success))
          return false;
        if (this.success != that.success)
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      List<Object> list = new ArrayList<Object>();

      boolean present_success = true;
      list.add(present_success);
      if (present_success)
        list.add(success);

      return list.hashCode();
    }

    @Override
    public int compareTo(generate_result other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;

      lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(other.isSetSuccess());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetSuccess()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.success, other.success);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
      }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("generate_result(");
      boolean first = true;

      sb.append("success:");
      sb.append(this.success);
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws TException {
      // check for required fields
      // check for sub-struct validity
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
        __isset_bitfield = 0;
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class generate_resultStandardSchemeFactory implements SchemeFactory {
      public generate_resultStandardScheme getScheme() {
        return new generate_resultStandardScheme();
      }
    }

    private static class generate_resultStandardScheme extends StandardScheme<generate_result> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, generate_result struct) throws TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
            break;
          }
          switch (schemeField.id) {
            case 0: // SUCCESS
              if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
                struct.success = iprot.readI64();
                struct.setSuccessIsSet(true);
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

      public void write(org.apache.thrift.protocol.TProtocol oprot, generate_result struct) throws TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.isSetSuccess()) {
          oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
          oprot.writeI64(struct.success);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class generate_resultTupleSchemeFactory implements SchemeFactory {
      public generate_resultTupleScheme getScheme() {
        return new generate_resultTupleScheme();
      }
    }

    private static class generate_resultTupleScheme extends TupleScheme<generate_result> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, generate_result struct) throws TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetSuccess()) {
          optionals.set(0);
        }
        oprot.writeBitSet(optionals, 1);
        if (struct.isSetSuccess()) {
          oprot.writeI64(struct.success);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, generate_result struct) throws TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(1);
        if (incoming.get(0)) {
          struct.success = iprot.readI64();
          struct.setSuccessIsSet(true);
        }
      }
    }

  }

}