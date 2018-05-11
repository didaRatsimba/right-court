package br.pegz.tutorials.rightcourt.configuration.features;

import br.pegz.tutorials.rightcourt.configuration.features.enums.RightCourtFeatures;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.core.manager.EnumBasedFeatureProvider;
import org.togglz.core.spi.FeatureProvider;

@Configuration
public class FeatureConfiguration {

    @Bean
    public FeatureProvider featureProvider() {
        return new EnumBasedFeatureProvider(RightCourtFeatures.class);
    }
}
