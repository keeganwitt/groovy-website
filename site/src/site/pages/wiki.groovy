import generator.DocUtils
import org.asciidoctor.ast.DocumentHeader

title = doc.structuredDoctitle.main

layout 'layouts/main.groovy', true,
        pageTitle: "The Apache Groovy programming language - Developer docs - $title",
        extraStyles: [relative('css/prettify.min.css')],
        extraFooter: contents {
            script(src:relative('js/vendor/prettify.min.js')) { }
            script { yieldUnescaped "document.addEventListener('DOMContentLoaded',prettyPrint)" }
        },
        mainContent: contents {
            Map options = [attributes:[DOCS_BASEURL:DocUtils.DOCS_BASEURL]]
            def notesAsHTML = asciidocText(notes,options)
            def matcher = notesAsHTML =~ /<h2 id="(.+?)">(.+?)<\/h2>/
            def sections = [:]
            while (matcher.find()) {
                sections[matcher.group(1)] = matcher.group(2)
            }

            div(id: 'content', class: 'page-1') {
                div(class: 'row') {
                    div(class: 'row-fluid') {
                        div(class: 'col-lg-3') {
                            ul(class: 'nav-sidebar') {
                                li(class:'active') {
                                    a(href: '#doc', title)
                                }
                                sections.each { k,v ->
                                    li {
                                        a(href:"#$k", class: 'anchor-link', v)
                                    }
                                }
                            }
                        }

                        div(class: 'col-lg-8 col-lg-pull-0') {
                            a(name:"doc"){}
                            h1(title)
                            def authors = doc.authors*.fullName
                            if (authors.size() == 1) {
                                p {
                                    yield 'Author: '
                                    i(authors[0])
                                }
                            } else if (authors) {
                                p {
                                    yield 'Authors: '
                                    i(authors.join(', '))
                                }
                            }
                            if (doc.attributes.revisionInfo?.date) {
                                p("Last update: ${doc.attributes.revisionInfo.date} (${doc.attributes.revisionInfo.remark?:'no comment'})")
                            }
                            hr()
                            yieldUnescaped notesAsHTML
                        }
                    }
                }
            }
        }
