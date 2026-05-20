package com.biolab.launchpad.internal.service;

import com.biolab.common.UrlStatus;
import org.springframework.stereotype.Service;

@Service
public class ResourceUrlResolver {

    public String resolve(String internalUrl, UrlStatus urlStatus, String externalUrl, boolean allowExternalUrls) {
        if (urlStatus == UrlStatus.READY) return internalUrl;
        if (allowExternalUrls && externalUrl != null) return externalUrl;
        return null;
    }
}
