package org.kuali.kfs.krad.comparator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NumericValueComparatorTest {
    private NumericValueComparator numericValueComparator;

    @Before
    public void setUp() {
        numericValueComparator = NumericValueComparator.getInstance();
    }

    @Test
    public void testNulls() {
        Assert.assertTrue(numericValueComparator.compare(null,null) == 0);
        Assert.assertTrue(numericValueComparator.compare("0",null) > 0);
        Assert.assertTrue(numericValueComparator.compare(null,"0") < 0);
    }

    @Test
    public void testOddCharacters() {
        Assert.assertTrue(numericValueComparator.compare("1000","1,000") == 0);
        Assert.assertTrue(numericValueComparator.compare("1000","1000&nbsp;") == 0);
        Assert.assertTrue(numericValueComparator.compare("1000","$1000") == 0);
        Assert.assertTrue(numericValueComparator.compare("1,000","1000") == 0);
        Assert.assertTrue(numericValueComparator.compare("1000&nbsp;","1000") == 0);
        Assert.assertTrue(numericValueComparator.compare("$1000","1000") == 0);
    }

    @Test
    public void testNegatives() {
        Assert.assertTrue(numericValueComparator.compare("-1000","(1000)") == 0);
        Assert.assertTrue(numericValueComparator.compare("(1000)","-1000") == 0);
    }

    @Test
    public void testValids() {
        Assert.assertTrue(numericValueComparator.compare("100","200") < 0);
        Assert.assertTrue(numericValueComparator.compare("200","200") == 0);
        Assert.assertTrue(numericValueComparator.compare("200","100") > 0);
    }

    @Test
    public void testInvalids() {
        Assert.assertTrue(numericValueComparator.compare("abc","def") < 0);
        Assert.assertTrue(numericValueComparator.compare("def","def") == 0);
        Assert.assertTrue(numericValueComparator.compare("def","abc") > 0);
    }
}