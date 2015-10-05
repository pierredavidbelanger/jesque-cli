package ca.pjer.jesque.cli.worker;

import net.greghaines.jesque.utils.ReflectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

class PathsClassLoader extends URLClassLoader {

    public PathsClassLoader(List<String> paths) throws Exception {
        super(toURLs(paths), ReflectionUtils.getDefaultClassLoader());
    }

    protected static URL[] toURLs(List<String> paths) throws Exception {
        URL[] urls = new URL[paths.size()];
        for (int i = 0; i < paths.size(); i++) {
            URI uri = new URI(paths.get(i));
            if (uri.getScheme() == null) {
                File file = new File(uri.toString());
                if (!file.exists()) {
                    throw new FileNotFoundException(uri.toString());
                }
                uri = file.toURI();
            }
            urls[i] = uri.toURL();
        }
        return urls;
    }
}
