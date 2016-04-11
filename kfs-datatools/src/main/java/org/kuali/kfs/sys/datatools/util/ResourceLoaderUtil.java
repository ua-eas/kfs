package org.kuali.kfs.sys.datatools.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceLoaderUtil {
    protected static Pattern resourceJarUrlPattern = Pattern.compile("^.*?\\.jar!(.+)$");


    /**
     * ApplicationContext aware version of method
     */
    public static Resource getFileResource(String sourceName, ApplicationContext applicationContext) {
        return applicationContext.getResource(sourceName);
    }

    /**
     * Parses the path name from a resource's description
     * @param resource a resource which hides a path from us
     * @return the path name if we could parse it out
     */
    public static  String parseResourcePathFromUrl(Resource resource) throws IOException {
        final URL resourceUrl = resource.getURL();
        if (ResourceUtils.isJarURL(resourceUrl)) {
            final Matcher resourceUrlPathMatcher = resourceJarUrlPattern.matcher(resourceUrl.getPath());
            if (resourceUrlPathMatcher.matches() && !StringUtils.isBlank(resourceUrlPathMatcher.group(1))) {
                return "classpath:" + resourceUrlPathMatcher.group(1);
            }
        } else if (ResourceUtils.URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol()) && resource.exists()) {
            return "file:" + resourceUrl.getFile();
        }
        return null;
    }

    public static Resource getFileResource(String sourceName) {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader(getDefaultClassLoader());
        return resourceLoader.getResource(sourceName);
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ResourceLoaderUtil.class.getClassLoader();
        }
        return classLoader;
    }
}
