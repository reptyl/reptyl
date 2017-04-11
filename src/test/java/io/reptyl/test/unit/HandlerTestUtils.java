package io.reptyl.test.unit;

import io.undertow.util.HttpString;
import io.undertow.util.PathTemplate;
import io.undertow.util.PathTemplateMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HandlerTestUtils {

    public static String getFirstTemplateString(Map<HttpString, PathTemplateMatcher<?>> matches) {

        List<PathTemplateMatcher<?>> pathTemplateMatchers = new ArrayList<>(matches.values());
        PathTemplateMatcher<?> pathTemplateMatcher = pathTemplateMatchers.get(0);
        Set<PathTemplate> pathTemplates = pathTemplateMatcher.getPathTemplates();
        return new ArrayList<>(pathTemplates).get(0).getTemplateString();
    }
}
