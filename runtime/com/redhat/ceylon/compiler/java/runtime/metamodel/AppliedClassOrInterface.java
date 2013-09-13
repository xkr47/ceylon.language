package com.redhat.ceylon.compiler.java.runtime.metamodel;

import java.util.Collections;
import java.util.List;

import ceylon.language.Map;
import ceylon.language.Sequential;
import ceylon.language.empty_;
import ceylon.language.model.ClassOrInterface$impl;
import ceylon.language.model.Model$impl;

import com.redhat.ceylon.compiler.java.Util;
import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Ignore;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.Sequenced;
import com.redhat.ceylon.compiler.java.metadata.TypeInfo;
import com.redhat.ceylon.compiler.java.metadata.TypeParameter;
import com.redhat.ceylon.compiler.java.metadata.TypeParameters;
import com.redhat.ceylon.compiler.java.metadata.Variance;
import com.redhat.ceylon.compiler.java.runtime.model.ReifiedType;
import com.redhat.ceylon.compiler.java.runtime.model.TypeDescriptor;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedTypedReference;

@Ceylon(major = 5)
@com.redhat.ceylon.compiler.java.metadata.Class
@TypeParameters({
    @TypeParameter(value = "Type", variance = Variance.OUT),
    })
public abstract class AppliedClassOrInterface<Type> 
    implements ceylon.language.model.ClassOrInterface<Type>, ReifiedType {

    private volatile boolean initialised;
    final com.redhat.ceylon.compiler.typechecker.model.ProducedType producedType;
    protected com.redhat.ceylon.compiler.java.runtime.metamodel.FreeClassOrInterface declaration;
    protected ceylon.language.Map<? extends ceylon.language.model.declaration.TypeParameter, ? extends ceylon.language.model.Type<?>> typeArguments;
    protected ceylon.language.model.Class<? extends Object, ? super Sequential<? extends Object>> superclass;
    protected Sequential<ceylon.language.model.Interface<? extends Object>> interfaces;
    @Ignore
    protected final TypeDescriptor $reifiedType;
    
    AppliedClassOrInterface(@Ignore TypeDescriptor $reifiedType, com.redhat.ceylon.compiler.typechecker.model.ProducedType producedType){
        this.producedType = producedType;
        this.$reifiedType = Metamodel.getTypeDescriptorForProducedType(producedType);
    }

    @Override
    @Ignore
    public ceylon.language.model.Type$impl<Type> $ceylon$language$model$Type$impl() {
        return null;
    }

    @Override
    @Ignore
    public ceylon.language.model.Generic$impl $ceylon$language$model$Generic$impl() {
        return null;
    }

    @Override
    @Ignore
    public Model$impl $ceylon$language$model$Model$impl() {
        return null;
    }
    
    @Override
    @Ignore
    public ClassOrInterface$impl<Type> $ceylon$language$model$ClassOrInterface$impl() {
        return null;
    }

    protected void checkInit(){
        if(!initialised){
            synchronized(Metamodel.getLock()){
                if(!initialised){
                    init();
                    initialised = true;
                }
            }
        }
    }
    
    protected void init() {
        com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface decl = (com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface) producedType.getDeclaration();
        this.declaration = (FreeClassOrInterface) Metamodel.getOrCreateMetamodel(decl);
        this.typeArguments = Metamodel.getTypeArguments(declaration, producedType);
        
        com.redhat.ceylon.compiler.typechecker.model.ProducedType superType = decl.getExtendedType();
        if(superType != null){
            com.redhat.ceylon.compiler.typechecker.model.ProducedType superTypeResolved = superType.substitute(producedType.getTypeArguments());
            this.superclass = (ceylon.language.model.Class) Metamodel.getAppliedMetamodel(superTypeResolved);
        }
        
        List<com.redhat.ceylon.compiler.typechecker.model.ProducedType> satisfiedTypes = decl.getSatisfiedTypes();
        ceylon.language.model.Interface<?>[] interfaces = new ceylon.language.model.Interface[satisfiedTypes.size()];
        int i=0;
        for(com.redhat.ceylon.compiler.typechecker.model.ProducedType pt : satisfiedTypes){
            com.redhat.ceylon.compiler.typechecker.model.ProducedType resolvedPt = pt.substitute(producedType.getTypeArguments());
            interfaces[i++] = (ceylon.language.model.Interface<?>) Metamodel.getAppliedMetamodel(resolvedPt);
        }
        this.interfaces = Util.sequentialInstance(TypeDescriptor.klass(ceylon.language.model.Interface.class, ceylon.language.Anything.$TypeDescriptor), interfaces);
    }

    @Override
    @TypeInfo("ceylon.language::Map<ceylon.language.model.declaration::TypeParameter,ceylon.language.model::Type<ceylon.language::Anything>")
    public Map<? extends ceylon.language.model.declaration.TypeParameter, ? extends ceylon.language.model.Type<?>> getTypeArguments() {
        return typeArguments;
    }

    @Override
    @TypeInfo("ceylon.language.model.declaration::ClassOrInterfaceDeclaration")
    public ceylon.language.model.declaration.ClassOrInterfaceDeclaration getDeclaration() {
        checkInit();
        return declaration;
    }

    @Override
    @TypeInfo("ceylon.language::Sequential<ceylon.language.model::Interface<ceylon.language::Anything>>")
    public Sequential<? extends ceylon.language.model.Interface<? extends Object>> getSatisfiedTypes() {
        checkInit();
        return interfaces;
    }

    @Override
    @TypeInfo("ceylon.language.model::Class<ceylon.language::Anything,ceylon.language::Nothing>|ceylon.language::Null")
    public ceylon.language.model.Class<? extends Object, ? extends Object> getExtendedType() {
        checkInit();
        return superclass;
    }

    @Ignore
    @Override
    public <SubType, Type, Arguments extends Sequential<? extends Object>>
    ceylon.language.model.Method<SubType, Type, Arguments> getMethod(@Ignore TypeDescriptor $reifiedSubType, 
                                                                         @Ignore TypeDescriptor $reifiedType, 
                                                                         @Ignore TypeDescriptor $reifiedArguments, 
                                                                         String name){
        
        return getMethod($reifiedSubType, $reifiedType, $reifiedArguments, name, (Sequential)empty_.$get());
    }

    @Override
    @TypeParameters({
        @TypeParameter(value = "SubType"),
        @TypeParameter(value = "Type"),
        @TypeParameter(value = "Arguments", satisfies = "ceylon.language::Sequential<ceylon.language::Anything>")
    })
    @TypeInfo("ceylon.language.model::Method<SubType,Type,Arguments>|ceylon.language::Null")
    public <SubType, Type, Arguments extends Sequential<? extends Object>>
        ceylon.language.model.Method<SubType, Type, Arguments> getMethod(@Ignore TypeDescriptor $reifiedSubType, 
                                                                             @Ignore TypeDescriptor $reifiedType, 
                                                                             @Ignore TypeDescriptor $reifiedArguments, 
                                                                             String name, 
                                                                             @Name("types") @Sequenced Sequential<? extends ceylon.language.model.Type<?>> types) {
        
        checkInit();
        final FreeFunction method = declaration.findMethod(name);
        if(method == null)
            return null;
        return method.<SubType, Type, Arguments>getAppliedMethod(this.$reifiedType, $reifiedType, $reifiedArguments, types, this);
    }

    @Ignore
    @Override
    public <SubType, Kind extends ceylon.language.model.ClassOrInterface<? extends java.lang.Object>>
        ceylon.language.model.Member<SubType, Kind> getClassOrInterface(@Ignore TypeDescriptor $reifiedSubType, 
                                                                            @Ignore TypeDescriptor $reifiedKind, 
                                                                            String name){
        
        return getClassOrInterface($reifiedSubType, $reifiedKind, name, (Sequential)empty_.$get());
    }

    @Override
    @TypeParameters({
        @TypeParameter(value = "SubType"),
        @TypeParameter(value = "Kind", satisfies = "ceylon.language.model::ClassOrInterface<ceylon.language::Anything,ceylon.language::Nothing>")
    })
    @TypeInfo("ceylon.language.model::Member<SubType,Kind>|ceylon.language::Null")
    public <SubType, Kind extends ceylon.language.model.ClassOrInterface<? extends java.lang.Object>>
        ceylon.language.model.Member<SubType, Kind> getClassOrInterface(@Ignore TypeDescriptor $reifiedSubType, 
                                                                            @Ignore TypeDescriptor $reifiedKind, 
                                                                            String name, 
                                                                            @Name("types") @Sequenced Sequential<? extends ceylon.language.model.Type<?>> types) {
        
        checkInit();
        final FreeClassOrInterface type = declaration.findType(name);
        if(type == null)
            return null;
        return type.getAppliedClassOrInterface(this.$reifiedType, $reifiedKind, types, (AppliedClassOrInterface<SubType>)this);
    }

    @Override
    @TypeParameters({
        @TypeParameter(value = "SubType"),
        @TypeParameter(value = "Type")
    })
    @TypeInfo("ceylon.language.model::Attribute<SubType,Type>|ceylon.language::Null")
    public <SubType, Type>
        ceylon.language.model.Attribute<SubType, Type> getAttribute(@Ignore TypeDescriptor $reifiedSubType, 
                                                                        @Ignore TypeDescriptor $reifiedType, 
                                                                        String name) {
        
        checkInit();
        final FreeAttribute value = declaration.findValue(name);
        if(value == null)
            return null;
        com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration decl = (com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration) value.declaration;
        ProducedTypedReference typedReference = decl.getProducedTypedReference(producedType, Collections.<ProducedType>emptyList());
        TypeDescriptor reifiedValueType = Metamodel.getTypeDescriptorForProducedType(typedReference.getType());
        return AppliedAttribute.instance(this.$reifiedType, reifiedValueType, value, typedReference, decl, this);
    }

    @Override
    @TypeParameters({
        @TypeParameter(value = "SubType"),
        @TypeParameter(value = "Type")
    })
    @TypeInfo("ceylon.language.model::VariableAttribute<SubType,Type>|ceylon.language::Null")
    public <SubType, Type>
        ceylon.language.model.VariableAttribute<SubType, Type> getVariableAttribute(@Ignore TypeDescriptor $reifiedSubType, 
                                                                                        @Ignore TypeDescriptor $reifiedType, 
                                                                                        String name) {
        return (ceylon.language.model.VariableAttribute<SubType, Type>)getAttribute($reifiedSubType, $reifiedType, name);
    }
    
    @Override
    public String toString() {
        return Metamodel.toTypeString(this);
    }
    
    @Ignore
    @Override
    public TypeDescriptor $getType() {
        checkInit();
        return TypeDescriptor.klass(AppliedClassOrInterface.class, $reifiedType);
    }
}
