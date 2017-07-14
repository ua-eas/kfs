package edu.arizona.kfs.gl.businessobject.lookup;

import org.kuali.rice.kns.lookup.KualiLookupableImpl;


public class GecLookupableImpl extends KualiLookupableImpl {
    /*
     * TL;DR: Marker class for Spring wiring
     *
     * Longer (how we get here):
     * jsp (2 w/ boClass prop hardwired for GecEntry/YeGecEntry), forces lookup of:
     * -> GecEnry.xml/YeGecEntry.xml, which both have lookupableID=gecGlEntryLookupable (this is this class)
     * -> gecGlEntryLookupable ID is defined in:
     * -> az/spring-gl.xml, and has a member of gecGlEntryLookupableHelperService
     * -> gecGlEntryLookupableHelperService, which is a GecEntryLookupableHelperServiceImpl
     * -> GecEntryLookupableHelperServiceImpl injected into the lookupable (us), and the search limit pulled
     * from *Entry.xml, which is our entire purpose: to _not_ receive non-deterministic partial result sets
     *
     *     This class is a gecGlEntryLookupable, and we needed our custom helper service set here. There is no
     * injection for lookupables into the form. Rather, the lookupableID is stored in the DataDictionary service,
     * by way of the BO spring wiring (i.e., *Entry.xml). In actuality, the helper service is set by ID, and not
     * by class type as it usually is configured in spring. The kuali framework makes this quite convoluted
     * and ends up feeling rigid (but hey, we got it going, so...).
     */
}
