package com.zmd.library_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("library.loan")
public record LibraryConfig(
        @DefaultValue("14") int defaultDays,
        @DefaultValue("5") int maxActive,
        @DefaultValue("2") int maxRenewals,
        @DefaultValue("7") int renewalExtensionDays
) {}
