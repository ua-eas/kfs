package edu.arizona.kfs.module.prje.dataaccess;

import java.util.Collection;

import edu.arizona.kfs.module.prje.businessobject.PRJESet;
import edu.arizona.kfs.module.prje.businessobject.PRJEType;

public interface PRJEDao {
    Collection<PRJEType> getPRJETypes(PRJESet set, Boolean active);
    PRJEType getPRJEType(Integer id);
    Collection<PRJESet> getPRJESets(Boolean active);
    PRJESet getPRJESet(Integer id);
}
