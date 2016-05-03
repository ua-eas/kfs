package org.kuali.kfs.sys.service;

import java.util.Optional;

public interface ApiKeyService {
    Optional<String> getPrincipalIdFromApiKey(String apiKey);
}
