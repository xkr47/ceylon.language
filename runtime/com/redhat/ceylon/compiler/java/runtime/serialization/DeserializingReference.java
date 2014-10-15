package com.redhat.ceylon.compiler.java.runtime.serialization;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import ceylon.language.AssertionError;
import ceylon.language.Collection;
import ceylon.language.Finished;
import ceylon.language.Tuple;
import ceylon.language.meta.model.ClassModel;
import ceylon.language.meta.model.Type;
import ceylon.language.serialization.Deconstructed;
import ceylon.language.serialization.DeserializableReference;
import ceylon.language.serialization.RealizableReference;
import ceylon.language.serialization.Reference;

import com.redhat.ceylon.compiler.java.Util;
import com.redhat.ceylon.compiler.java.runtime.metamodel.AppliedClass;
import com.redhat.ceylon.compiler.java.runtime.metamodel.AppliedMemberClass;
import com.redhat.ceylon.compiler.java.runtime.metamodel.Metamodel;
import com.redhat.ceylon.compiler.java.runtime.model.ReifiedType;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;

public class DeserializingReference<Instance> 
    implements DeserializableReference<Instance>, RealizableReference<Instance>, $InstanceLeaker$<Instance>, ReifiedType {
    
    private final TypeDescriptor reified$Instance;
    
    private static final int ST_STATELESS = 0; 
    private static final int ST_UNINITIALIZED = 1;
    private static final int ST_UNINITIALIZED_REFS = 2;
    private static final int ST_INITIALIZED = 3;
    
    private int state;
    
    private final Object id;
    private final Instance instance;
    @SuppressWarnings("rawtypes")
    private final ClassModel classModel;
    private Deconstructed deconstructed;
    
    DeserializingReference(TypeDescriptor reified$Instance, 
            DeserializationContextImpl context, 
            Object id, 
            ClassModel classModel, Reference<?> outerReference) {
        this(reified$Instance, 
                id, 
                classModel,
                (Instance)createInstance(context, id, classModel, outerReference));
    }
    
    DeserializingReference(TypeDescriptor reified$Instance, 
            Object id, 
            @SuppressWarnings("rawtypes") ClassModel classModel, Instance instance) {
        this.reified$Instance = reified$Instance;
        this.id = id;
        this.classModel = classModel;
        this.state = ST_STATELESS;
        this.instance = instance;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <Instance> Instance createInstance(DeserializationContextImpl context, Object id,
            ClassModel classModel, Reference<?> outerReference)
            throws AssertionError {
        java.lang.Class<Instance> clazz;
        Class outerClass;
        Object outer;
        TypeDescriptor[] typeArguments = ((TypeDescriptor.Class)((ReifiedType)classModel).$getType$()).getTypeArguments();
        if (classModel instanceof AppliedClass) {
            // Class<Type, Arguments>
            clazz = (java.lang.Class)((TypeDescriptor.Class)typeArguments[0]).getKlass();
            outerClass = null;
            outer = null;
        } else if (classModel instanceof AppliedMemberClass) {
            // MemberClass<Container, Type, Arguments>
            clazz = (java.lang.Class)((TypeDescriptor.Class)typeArguments[1]).getKlass();
            outerClass = ((TypeDescriptor.Class)typeArguments[0]).getKlass();
            outer = context.leakReferred(outerReference.getId());
        } else {
            throw new AssertionError("unexpected class model: " 
                    + (classModel != null ? classModel.getClass().getName() : "null"));
        }
        // Construct arrays for types and arguments for reflective instantiation
        // of the serialization constructor
        Collection<?> typeArgs = classModel.getTypeArguments().getItems();
        Class<?>[] types = new Class[(outerClass != null ? 2 : 1) + Util.toInt(typeArgs.getSize())];
        Object[] args = new Object[(outer != null ? 2 : 1) + Util.toInt(typeArgs.getSize())];
        int ii = 0;
        if (outerClass != null) {
            types[ii] = outerClass;
            args[ii] = outer;
            ii++;
        }
        types[ii] = $Serialization$.class;
        args[ii] = null;
        ii++;
        for (int jj = 0 ; jj < typeArgs.getSize(); ii++, jj++) {
            types[ii] = TypeDescriptor.class;
            args[ii] = Metamodel.getTypeDescriptor((Type)typeArgs.getFromFirst(jj));
        }
        
        try {
            Constructor<Instance> ctor = clazz.getDeclaredConstructor(types);
            ctor.setAccessible(true);
            // Actually we need to pass something equivalent to the type descriptors here
            // because the companion instances can require those. But we don't have the deconstructed yet!
            // This means we have to obtain the type descriptors from the class model
            return ctor.newInstance(args);// Pass a null $Serialization$
        } catch (NoSuchMethodException e) {
            throw new AssertionError("class is not serializable " + classModel);
        } catch (InvocationTargetException e) {
            throw new AssertionError("error thrown during instantiation of " + classModel+ (e.getMessage() != null ? ": " + e.getMessage() : ""));
        } catch (SecurityException e) {
            // Should never happen
            throw new RuntimeException(e);
        } catch (InstantiationException|IllegalAccessException|IllegalArgumentException e) {
            // Should never happen
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Instantiates and returns a {@link DeserializingStatefulReference}
     */
    @Override
    public RealizableReference<Instance> deserialize(Deconstructed deconstructed) {
        this.deconstructed = deconstructed;
        if (getState() != ST_STATELESS) {
            throw new AssertionError("reference has already been deserialized: " + this);
        }
        this.state = ST_UNINITIALIZED;
        return this;
    }
    

    @Override
    public TypeDescriptor $getType$() {
        return TypeDescriptor.klass(DeserializingReference.class, reified$Instance);
    }

    @Override
    public Instance $leakInstance$() {
        return instance;
    }
    
    @Override
    public String toString() {
        switch (getState()) {
        case ST_STATELESS:
            return "DeserializableReference(" + id + ")";
        case ST_UNINITIALIZED:
            return "RealizableReference(" + id + "~>~" + deconstructed + ")";
        case ST_UNINITIALIZED_REFS:
            return "RealizableReference(" + id + "~>" + deconstructed + ")";
        case ST_INITIALIZED:
            return "RealizableReference(" + id + "->" + instance + ")";
        }
        throw new AssertionError("Illegal state " + getState());
    }
    
    @Override
    public Instance instance() {
        // !!!!! XXX MUST NOT LEAK PARTIALLY BUILT OBJECTS
        // XXX HERE we must ensure that this.instance has been 
        // reconstructed, but also that everything it references 
        // (transitively) has been reconstructed too.
        // We can do this by inspecting this.deconstructed finding the 
        // references (from this.context) and ensuring those are 
        // reconstructed.
        reconstruct();
        return instance;
    }
    
    /**
     * Reconstructs this instance
     */
    @Override
    public Object reconstruct() {
        if (getState() != ST_INITIALIZED) {
            LinkedList<DeserializingReference<?>> queue = new LinkedList<DeserializingReference<?>>();
            queue.addLast(this);
            while (!queue.isEmpty()) {
                DeserializingReference<?> r = queue.removeFirst();
                if (r.getState() == ST_UNINITIALIZED) {
                    ((Serializable)r.instance).$deserialize$(r.deconstructed);
                    r.state = ST_UNINITIALIZED_REFS;
                }
                if (r.getState() == ST_UNINITIALIZED_REFS) {
                    for (DeserializingReference<Object> referred : r.references()) {
                        if (referred.getState() == ST_STATELESS) {
                            throw new AssertionError("reference " + referred.getId() + " has not been deserialized");
                        }
                        DeserializingReference<?> statefulReferred = (DeserializingReference<?>)referred;
                        if (statefulReferred.getState() != ST_INITIALIZED) {
                            queue.addLast(statefulReferred);
                        }
                    }
                    // This is actually too weak: If an exception is thrown
                    // while initialising some other thing (already in, or 
                    // yet to be added to the queue), then it will be possible 
                    // to obtain a reference to a broken thing
                    // We could track this on a per-instance basis 
                    // (but that means tracking the reverse dependencies, transitively)
                    // Or on a per-context basis, so that instance always 
                    // throws if there was ever an exception, even if the 
                    // broken object is not each reachable from the 
                    // instance being sought
                    
                    r.state = ST_INITIALIZED;
                    r.deconstructed = null;
                }
            }
        }
        if (getState() != ST_INITIALIZED) {
            throw new AssertionError("broken graph");
        }
        return null;
    }
    
    /**
     * The references in the Deconstructed
     * @return
     */
    private Iterable<DeserializingReference<Object>> references() {
        return new Iterable<DeserializingReference<Object>>() {

            @Override
            public java.util.Iterator<DeserializingReference<Object>> iterator() {
                return new java.util.Iterator<DeserializingReference<Object>>() {
                    ceylon.language.Iterator it = deconstructed.iterator();
                    Object next = null;
                    
                    @Override
                    public boolean hasNext() {
                        if (next == null) {
                            Object vdValue = it.next();
                            while (true) {
                                if (vdValue instanceof Finished) {
                                    next = vdValue;
                                    break;
                                }
                                Object valueOrRef = ((Tuple)vdValue).getFromFirst(1);
                                if (valueOrRef instanceof Reference) {
                                    next = valueOrRef;
                                    break;
                                }
                                vdValue = it.next();
                            }
                        }
                        return !(next instanceof Finished);
                    }
                    @Override
                    public DeserializingReference<Object> next() {
                        if (next == null) {
                            hasNext();
                        }
                        if (next instanceof Finished) {
                            throw new NoSuchElementException();
                        }
                        DeserializingReference<Object> result = (DeserializingReference)next;
                        next = null;
                        return result;
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                    
                };
            }
            
        };
    }

    @Override
    public Object getId() {
        return id;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public ClassModel getClazz() {
        // note: we cannot call type() because the instance might not have 
        // had it's reified type arguments set yet.
        return classModel;
        //type_.type(reified$Instance, instance);
    }

    int getState() {
        return state;
    }
    
}
