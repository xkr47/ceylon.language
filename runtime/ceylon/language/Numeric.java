package ceylon.language;

import com.redhat.ceylon.compiler.java.metadata.Ceylon;
import com.redhat.ceylon.compiler.java.metadata.Name;
import com.redhat.ceylon.compiler.java.metadata.SatisfiedTypes;
import com.redhat.ceylon.compiler.java.metadata.TypeParameter;
import com.redhat.ceylon.compiler.java.metadata.TypeParameters;

@Ceylon
@TypeParameters(@TypeParameter(value = "Other",
    		satisfies="ceylon.language.Numeric<Other>"))
@SatisfiedTypes({"ceylon.language.Number", 
		         "ceylon.language.Comparable<Other>",
		         "ceylon.language.Summable<Other>",
		         "ceylon.language.Invertable<Other>"})
public interface Numeric<Other extends Numeric<Other>> 
    extends Number, Comparable<Other>, Summable<Other>,
            Invertable<Other> {
	
	public Other minus(@Name("other") Other number);
    public Other times(@Name("other") Other number);
    public Other divided(@Name("other") Other number);
    public Other power(@Name("other") Other number);
    
    public Other getMagnitude();
    public Other getFractionalPart();
    public Other getWholePart();
    
}
