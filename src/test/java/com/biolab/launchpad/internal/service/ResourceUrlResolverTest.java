package com.biolab.launchpad.internal.service;

import com.biolab.common.UrlStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("ResourceUrlResolver Tests")
class ResourceUrlResolverTest {

    private final ResourceUrlResolver resolver = new ResourceUrlResolver();

    @Test
    @DisplayName("READY -> returns internal URL regardless of external fallback settings")
    void ready_returnsInternalUrl() {
        assertEquals("http://s3/file.mp4",
                resolver.resolve("http://s3/file.mp4", UrlStatus.READY, "https://ext/file.mp4", true));
    }

    @Test
    @DisplayName("PENDING + allowExternalUrls + external URL present -> returns external URL")
    void pending_allowExternalUrls_externalPresent_returnsExternalUrl() {
        assertEquals("https://ext/file.mp4",
                resolver.resolve("http://s3/file.mp4", UrlStatus.PENDING, "https://ext/file.mp4", true));
    }

    @Test
    @DisplayName("PENDING + allowExternalUrls=false -> returns null")
    void pending_allowExternalUrls_false_returnsNull() {
        assertNull(resolver.resolve("http://s3/file.mp4", UrlStatus.PENDING, "https://ext/file.mp4", false));
    }

    @Test
    @DisplayName("PENDING + allowExternalUrls=true + no external URL -> returns null")
    void pending_allowExternalUrls_true_noExternalUrl_returnsNull() {
        assertNull(resolver.resolve("http://s3/file.mp4", UrlStatus.PENDING, null, true));
    }

    @Test
    @DisplayName("FAILED + allowExternalUrls + external URL present -> returns external URL")
    void failed_allowExternalUrls_returnsExternalUrl() {
        assertEquals("https://ext/file.mp4",
                resolver.resolve("http://s3/file.mp4", UrlStatus.FAILED, "https://ext/file.mp4", true));
    }

    @Test
    @DisplayName("FAILED + no fallback -> returns null")
    void failed_noFallback_returnsNull() {
        assertNull(resolver.resolve("http://s3/file.mp4", UrlStatus.FAILED, null, false));
    }
}
