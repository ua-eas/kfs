package org.kuali.kfs.module.purap.service;

import org.kuali.rice.kim.api.identity.Person;

import java.util.List;
import java.util.Map;

public interface MyOrdersService {
    public List<Map<String, Object>> getLatestOrders(Person user, Integer count);
}
