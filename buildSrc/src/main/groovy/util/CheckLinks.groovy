/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package util

import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.gradle.api.logging.Logger
import org.apache.tools.ant.util.FileUtils

class CheckLinks {
    Logger logger = null
    List<String> excludeFromChecks = []
    final Map<File, List> deadLinks = [:]
    Map<Object, Object> isDead = [:]
    def baseDir
    // Extra site roots to resolve local links against. The dev and user sites are
    // generated separately but deployed together under the same domain, so a link
    // from one site into the other only resolves once both are merged on the server.
    // Listing the sibling site's build output here lets those cross-site links be
    // validated instead of reported as false positives.
    List additionalRoots = []

    boolean checkIsDead(String link, currentPath) {
        if (excludeFromChecks.any { link.startsWith(it) || (!it.startsWith('http') && link.endsWith(it)) }) {
            // skip checking those links because they dramatically increase build time
            // while being most likely ok because generated through changelog parsing
            return false
        }

        def lower = link.toLowerCase()

        // Same-page anchors and non-navigational schemes are not checkable links.
        // Values containing whitespace are not real href/src targets either - they are
        // usually fragments of inline JavaScript (e.g. "' + r.url + '") caught by the
        // attribute regex - so skip them rather than treat them as dead links.
        if (!link || link.startsWith('#') || link =~ /\s/ || lower ==~ /^(mailto|tel|javascript|data):.*/) {
            return false
        }

        // Treat protocol-relative links (//host/path) as https for remote checking.
        if (link.startsWith('//')) {
            return checkRemote('https:' + link)
        }

        if (lower.startsWith('http://') || lower.startsWith('https://')) {
            return checkRemote(link)
        }

        // Anything else is a link into the generated site: verify it exists on disk.
        // We resolve against the built site using File (never by hand-building a
        // "file://" string) so paths containing spaces or other characters that are
        // illegal in a URI - e.g. a CI workspace named "Groovy dev website" - do not
        // spuriously report every local link as dead.
        return !localTargetExists(link, currentPath)
    }

    private boolean localTargetExists(String link, currentPath) {
        // Drop any fragment (#...) or query (?...) before resolving to a file.
        def path = link.replaceFirst(/[#?].*$/, '')
        if (!path) {
            return true
        }
        try {
            path = URLDecoder.decode(path, 'UTF-8')
        } catch (ignored) {
            // leave the raw path if it is not valid percent-encoding
        }
        boolean rootAbsolute = path.startsWith('/')
        String relPath = rootAbsolute ? path.substring(1) : path
        def roots = [baseDir, *additionalRoots].findAll { it }.collect { it as File }
        return roots.any { File root ->
            File base = (rootAbsolute || !currentPath) ? root : new File(root, currentPath.toString())
            targetResolves(base, relPath)
        }
    }

    // The web server serves extensionless "clean" URLs (e.g. blog/foo -> blog/foo.html)
    // and directory URLs (foo/ -> foo/index.html), so accept those forms as well.
    private boolean targetResolves(File base, String relPath) {
        resolves(base, relPath) ||
                resolves(base, relPath + '.html') ||
                resolves(base, relPath + '/index.html')
    }

    private boolean resolves(File base, String relPath) {
        // Normalise ".." segments lexically before touching the filesystem: a link such
        // as "../releasenotes/foo.html" from a wiki/ page must resolve against a sibling
        // site root even when that root has no wiki/ directory to traverse back out of.
        new File(base, relPath).toPath().normalize().toFile().exists()
    }

    private boolean checkRemote(String link) {
        try {
            URL url = URI.create(link).toURL()
            logger?.debug("Checking URL: $url")
            def cx = url.openConnection()
            if (cx instanceof HttpURLConnection) {
                CloseableHttpClient httpclient = HttpClients.createDefault()
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(20_000)
                        .setConnectTimeout(20_000)
                        .setConnectionRequestTimeout(20_000)
                        .setCookieSpec(CookieSpecs.STANDARD)
                        .build()
                HttpGet httpget = new HttpGet(link)
                httpget.config = requestConfig
                CloseableHttpResponse response
                try {
                    response = httpclient.execute(httpget)
                    if (response.statusLine.statusCode == 404) {
                        return true
                    }
                } finally {
                    response?.close()
                }
            }
        } catch (e) {
            logger?.debug e.message
            return true
        }
        return false
    }

    def checkLink(List dead, int line, String link, currentPath) {
        if (!isDead.containsKey(link)) isDead[link] = checkIsDead(link, currentPath)
        if (isDead[link]) {
            dead << [line:line, link:link]
        }
    }

    def checkPage(File f) {
        def currentPath = FileUtils.getRelativePath(baseDir, f.parentFile)
        f.eachLine('utf-8') { String line, int nb ->
            def dead = []
            [/\shref=['"](.+?)['"]/, /src=['"](.+?)['"]/].each { regex ->
                def matcher = line =~ regex
                if (matcher) {
                    matcher.each {
                        def linkpath = it[1]
                        checkLink(dead, nb, linkpath, currentPath)
                    }
                }
            }
            if (dead) {
                deadLinks[f] = dead
            }
        }
    }

}
