layout 'layouts/main.groovy', true,
        pageTitle: 'The Apache Groovy programming language - Continuous integration',
        mainContent: contents {
            div(id: 'content', class: 'page-1') {
                div(class: 'row') {
                    div(class: 'row-fluid') {
                        div(class: 'col-lg-8 col-lg-pull-0') {
                            include template: 'includes/contribute-button.groovy'
                            h1 {
                                i(class: 'fa-classic fa-solid fa-circle-notch') {}
                                yield ' Continuous integration'
                            }
                            article {
                                p """
                                    Our ${
                                    $a(href: 'http://ci.groovy-lang.org?guest=1', 'continuous integration server')
                                },
                                    sponsored by ${$a(href: 'http://www.jetbrains.com', 'JetBrains')},
                                    builds Groovy against multiple JDK versions:
                                """
                                hr(class: 'divider')

                                h2 'Groovy builds'

                                def renderBuilds = { Map builds ->
                                    table(class: 'table table-stripped') {
                                        thead {
                                            tr {
                                                th('Build name')
                                                th('Status')
                                            }
                                        }
                                        tbody {
                                            builds.each { name, ref ->
                                                def (id, branch) = ref
                                                tr {
                                                    td(name)
                                                    td {
                                                        a(href: "https://ci.groovy-lang.org/buildConfiguration/$id?guest=1") {
                                                            img(src: """https://ci.groovy-lang.org/app/rest/builds/buildType:(id:$id)${branch?",branch:$branch":''}/statusIcon""")
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                renderBuilds([
                                        'Groovy master, JDK 11': ['MasterTestJdk11', ''],
                                        'Groovy master, JDK 17': ['MasterTestJdk17', ''],
                                        'Groovy master, JDK 21': ['MasterTestJdk21', ''],
                                        'Groovy 4.0.X, JDK 8': ['Groovy_Groovy40xTestJdk8', ''],
                                        'Groovy 4.0.X, JDK 17': ['Groovy40xTestJdk17', ''],
                                        'Groovy 4.0.X, JDK 21': ['Groovy40xTestJdk21', ''],
                                        'Groovy 3.0.X, JDK 8': ['Groovy30xTestAllJdk8', ''],
                                        'Groovy 2.5.X, JDK 8': ['Groovy25xCheckJdk8', ''],
                                        'Groovy 2.5.X, JDK 11': ['Groovy25xTestJdk11', ''],
                                        'Groovy 2.4.X, JDK 8': ['Groovy24xCheckJdk8', '']
                                ])
                            }
                        }
                    }
                }
            }
        }
