package edu.arizona.kfs.module.prje.dataaccess;

import java.util.Collection;

import edu.arizona.kfs.module.prje.businessobject.PRJESet;
import edu.arizona.kfs.module.prje.businessobject.PRJEType;

public interface PRJEDao {
    public Collection<PRJEType> getPRJETypes(PRJESet set, Boolean active);
    public PRJEType getPRJEType(Integer id);
    public Collection<PRJESet> getPRJESets(Boolean active);
    public PRJESet getPRJESet(Integer id);
}
