package org.kuali.kfs.sys.businessobject;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class JwtDataTest {
    @Test
    public void testDateConstructor() {
        JwtData data = new JwtData("khuntley",100);

        Calendar issue = Calendar.getInstance();
        issue.setTime(data.getIssuedAt());

        Date expire = data.getExpired();

        issue.add(Calendar.SECOND,100);
        Assert.assertEquals(issue.getTime(),expire);
        Assert.assertEquals("khuntley",data.getPrincipalName());
    }
}