package edu.arizona.kfs.gl.web.struts;


import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;

/*
 * Extended for lookupable -- Rice was somehow picking up super's default without this
 */
@SuppressWarnings("deprecation")// Lookupable
public class GecMultipleValueLookupForm extends MultipleValueLookupForm {

    private Lookupable lookupable;



    @Override
    public Lookupable getLookupable() {
        return lookupable;
    }


    @Override
    public void setLookupable(Lookupable lookupable) {
        this.lookupable = lookupable;
    }

}
