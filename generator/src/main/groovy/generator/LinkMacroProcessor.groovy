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
import org.asciidoctor.ast.ContentNode
import org.asciidoctor.extension.InlineMacroProcessor

@CompileStatic
class LinkMacroProcessor extends InlineMacroProcessor {
    private final Closure<String> urlBuilder

    LinkMacroProcessor(String macroName, Closure<String> urlBuilder) {
        super(macroName)
        this.urlBuilder = urlBuilder
    }

    @Override
    Object process(ContentNode parent, String target, Map<String, Object> attributes) {
        String href = urlBuilder.call(target)
        String text = (attributes['text'] ?: attributes['1'] ?: target).toString()
        Map<String, Object> options = [:]
        options.put('type', ':link')
        options.put('target', href)
        createPhraseNode(parent, 'anchor', text, attributes, options)
    }
}
