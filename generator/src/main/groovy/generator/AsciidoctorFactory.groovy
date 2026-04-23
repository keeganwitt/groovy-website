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
package generator

import groovy.transform.CompileStatic
import org.asciidoctor.Asciidoctor

@CompileStatic
class AsciidoctorFactory {
    @Lazy static Asciidoctor instance = createAsciidoctor()

    private static Asciidoctor createAsciidoctor() {
        def asciidoctor = Asciidoctor.Factory.create()
        registerLinkMacros(asciidoctor)
        asciidoctor
    }

    private static Closure<String> groovydocUrl(String base) {
        { String target ->
            def parts = target.split('#')
            def className = parts[0]
            def anchor = parts.length > 1 ? parts[1] : null
            (className == 'index'
                    ? base
                    : "${base}?${className.replace('.', '/')}.html${anchor ? '#' + anchor : ''}").toString()
        }
    }

    private static void registerLinkMacros(Asciidoctor asciidoctor) {
        String docsBase = System.getProperty('docs_baseurl') ?: 'https://docs.groovy-lang.org/latest'
        def registry = asciidoctor.javaExtensionRegistry()
        registry.inlineMacro(new LinkMacroProcessor('jdk',     groovydocUrl('https://docs.oracle.com/en/java/javase/11/docs/api/index.html')))
        registry.inlineMacro(new LinkMacroProcessor('gjdk',    groovydocUrl("${docsBase}/html/groovy-jdk/index.html".toString())))
        registry.inlineMacro(new LinkMacroProcessor('gapi',    groovydocUrl("${docsBase}/html/gapi/index.html".toString())))
        registry.inlineMacro(new LinkMacroProcessor('gapid',   groovydocUrl("${docsBase}/html/gapi/".toString())))
        registry.inlineMacro(new LinkMacroProcessor('dochome', { String target -> "${docsBase}/html/documentation/${target}".toString() }))
    }
}
