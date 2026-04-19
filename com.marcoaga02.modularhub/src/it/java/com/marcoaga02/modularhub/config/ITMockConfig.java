package com.marcoaga02.modularhub.config;

import com.marcoaga02.modularhub.shared.service.IdentityProviderService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class ITMockConfig {

    @Bean
    @Primary
    public IdentityProviderService identityProviderService() {
        return Mockito.mock(IdentityProviderService.class);
    }
}
