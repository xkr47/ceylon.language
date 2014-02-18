//Addendum to GenericDeclaration
defineAttr(GenericDeclaration$meta$declaration.$$.prototype,'typeParameterDeclarations',function(){
  if (this.tipo) {
    var mm=this.tipo.$$metamodel$$;
    if (typeof(mm)==='function') {
      mm=mm(); this.tipo.$$metamodel$$=mm;
    }
    var tps=mm.$tp;
    if (tps) {
      var rv=[];
      for (var tp in tps) {
        rv.push(OpenTypeParam(this.tipo,tp));
      }
      return rv.reifyCeylonType({Absent$Iterable:{t:Null},Element$Iterable:{t:TypeParameter$meta$declaration}});
    }
    return getEmpty();
  }
  console.log("TODO GenericDeclaration.typeParameterDeclarations");
  return getEmpty();
},undefined,function(){return{mod:$$METAMODEL$$,$t:{t:Sequential,a:{Element:{t:TypeParameter$meta$declaration}}},$cont:GenericDeclaration,$an:function(){return[shared(),actual()];},d:['ceylon.language.meta.declaration','GenericDeclaration','$at','typeParameterDeclarations']};});
GenericDeclaration$meta$declaration.$$.prototype.getTypeParameterDeclaration=function (name$14){
  if (this.tipo) {
    var mm=this.tipo.$$metamodel$$;
    if (typeof(mm)==='function') {
      mm=mm(); this.tipo.$$metamodel$$=mm;
    }
    var tps=mm.$tp;
    if (tps) {
      for (var tp in tps) {
        var tpnm = tp;
        if (tp.indexOf('$')>0)tpnm=tp.substring(0,tp.indexOf('$'));
        if (name$14==tpnm) {
          return OpenTypeParam(this.tipo,tp);
        }
      }
    }
    return null;
  }
  console.log("TODO GenericDeclaration.getTypeParameterDeclaration(" + name$14 + ")");
  return null;
};
GenericDeclaration$meta$declaration.$$.prototype.getTypeParameterDeclaration.$$metamodel$$=function(){return{mod:$$METAMODEL$$,$t:{ t:'u', l:[{t:Null},{t:TypeParameter$meta$declaration}]},$ps:[{$nm:'name',$mt:'prm',$t:{t:String$}}],$cont:GenericDeclaration,$an:function(){return[shared(),actual()];},d:['ceylon.language.meta.declaration','GenericDeclaration','$m','getTypeParameterDeclaration']};};
 
