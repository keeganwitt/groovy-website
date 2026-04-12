layout 'layouts/main.groovy', true,
        pageTitle: 'The Apache Groovy programming language - Invoke dynamic support',
        mainContent: contents {
            div(id: 'content', class: 'page-1') {
                div(class: 'row') {
                    div(class: 'row-fluid') {
                        div(class: 'col-lg-3') {
                            ul(class: 'nav-sidebar') {
                                li {
                                    a(href: 'download.html', 'Download Groovy')
                                }
                                li {
                                    a(href: 'download.html#distro', class: 'anchor-link', 'Distributions')
                                }
                                li {
                                    a(href: 'download.html#sdkman', class: 'anchor-link', 'Through SDKMAN!')
                                }
                                li {
                                    a(href: 'download.html#buildtools', class: 'anchor-link', 'From your build tools')
                                }
                                li {
                                    a(href: 'download.html#otherways', class: 'anchor-link', 'Other ways to get Groovy')
                                }
                                li {
                                    a(href: 'versioning.html', 'Groovy version scheme')
                                }
                                li(class: 'active') {
                                    a(href: 'indy.html') {
                                        strong 'Invoke dynamic support'
                                    }
                                }
                            }
                        }

                        div(class: 'col-lg-8 col-lg-pull-0') {
                            include template: 'includes/contribute-button.groovy'
                            h1 {
                                i(class: 'fa-classic fa-solid fa-gear') {}
                                yield ' Invoke dynamic support'
                            }
                            article {
                                h2 'Introduction'
                                p '''
                                    Since Groovy 2.0, support was added for the JVM invokedynamic instruction.
                                    This instruction is supported since Java 7 and is a bytecode instruction in the JVM
                                    that allows easier implementation of dynamic languages.
                                    This instruction is also used internally, by the JVM, for lambda support in Java 8+.
                                '''
                                p '''
                                    This feature is not visible to the developer or the end user.
                                    It is a compilation and runtime feature only.
                                    It affects how Groovy compiles dynamic method calls, property access, and other
                                    dynamic operations into bytecode.
                                '''

                                h2 'Groovy 4.0+ (current)'
                                p '''
                                    From Groovy 4.0 onwards, invokedynamic bytecode generation is <b>enabled by default</b>.
                                    There is a single set of jars, compiled with invokedynamic enabled.
                                    The separate "-indy" jars and Maven classifiers from earlier versions are no longer needed.
                                '''
                                p '''
                                    It is still possible to disable invokedynamic for user-compiled code, which causes the
                                    compiler to fall back to classic call-site caching bytecode. This can be useful in cases
                                    where call-site caching is faster for specific workloads. To disable:
                                '''
                                ul {
                                    li {
                                        yield 'System property: '
                                        code 'groovy.target.indy=false'
                                    }
                                    li {
                                        yield 'Compiler configuration: set the '
                                        code 'indy'
                                        yield ' optimization option to '
                                        code 'false'
                                    }
                                }
                                p '''
                                    Note that disabling invokedynamic only affects the bytecode generated for your code.
                                    The Groovy runtime jars themselves are compiled with invokedynamic enabled.
                                '''
                                p '''
                                    There are also internal thresholds that can be tuned for performance
                                    (search the codebase for <code>groovy.indy.optimize.threshold</code>
                                    and <code>groovy.indy.fallback.threshold</code>).
                                '''

                                h2 'Groovy 2.0 to 3.x'

                                div(class: 'alert alert-info', role: 'alert') {
                                    yield 'This section applies only to Groovy versions before 4.0.'
                                }

                                h3 'Two JARs'
                                p 'The Groovy distribution came with two jars:'
                                ul {
                                    li {
                                        code 'groovy-x.y.z.jar'
                                        yield ': compiled with classic call-site caching'
                                    }
                                    li {
                                        code 'groovy-x-y-z-indy.jar'
                                        yield ': compiled with invokedynamic'
                                    }
                                }
                                p '''
                                    Both jars contained a fully working Groovy implementation capable of compiling user
                                    code using either invokedynamic or call-site caching. The sets of jars were mutually
                                    exclusive (don't put both on classpath) and the key difference was how the Groovy
                                    source files that make up Groovy itself were compiled.
                                '''
                                p '''
                                    When accessing a Groovy jar from a Maven repository, you could select the indy version using the 'indy' classifier.
                                '''

                                h3 'The compilation flag'
                                p '''
                                    Independently of the jar version used, invokedynamic support required a specific
                                    compilation flag (indy). If you wanted to compile your classes with invokedynamic
                                    support, this flag had to be set at compile time.
                                '''
                                p 'For user compiled classes:'
                                table(class: 'table') {
                                    tr {
                                        th 'indy flag'
                                        th 'off'
                                        th 'on'
                                    }
                                    tr {
                                        td 'normal jar'
                                        td 'call site caching'
                                        td 'invokedynamic'
                                    }
                                    tr {
                                        td 'indy jar'
                                        td 'call site caching'
                                        td 'invokedynamic'
                                    }
                                }

                                p 'For core Groovy classes:'
                                table(class: 'table') {
                                    tr {
                                        th 'indy flag'
                                        th 'off'
                                        th 'on'
                                    }
                                    tr {
                                        td 'normal jar'
                                        td 'call site caching'
                                        td 'call site caching'
                                    }
                                    tr {
                                        td 'indy jar'
                                        td 'invokedynamic'
                                        td 'invokedynamic'
                                    }
                                }
                                p '''
                                    So even if you used the indy jar, if you didn't use the invokedynamic flag at compile time,
                                    then the compiled classes would use the "old" format with call-site caching.
                                '''
                            }
                            hr(class: 'divider')
                        }
                    }
                }
            }
        }
