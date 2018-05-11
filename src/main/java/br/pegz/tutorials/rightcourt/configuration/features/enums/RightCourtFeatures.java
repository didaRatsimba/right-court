package br.pegz.tutorials.rightcourt.configuration.features.enums;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;

public enum RightCourtFeatures implements Feature {

    @EnabledByDefault
    @Label("Court Async Communication")
    COURT_ASYNC_FEATURE
}
