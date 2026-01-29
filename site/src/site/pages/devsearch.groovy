layout 'layouts/main.groovy', true,
        pageTitle: 'The Apache Groovy programming language - Search',
        mainContent: contents {
            div(id: 'content', class: 'page-1') {
                div(class: 'row') {
                    div(class: 'row-fluid') {

                        div(class: 'col-lg-3') {}

                        div(class: 'col-lg-8 col-lg-pull-0') {

                            include template: 'includes/contribute-button.groovy'

                            h1 {
                                i(class: 'fa-classic fa-solid fa-magnifying-glass') {}
                                yield ' Search'
                            }

                            p '''
                            You can search the Groovy developer documentation and site-dev pages.
                            Type your query below and see results instantly:
                        '''

                            div(id: 'search-container') {
                                input(type: 'search', id: 'q', placeholder: 'Search developer docs…', style: 'width: 100%; padding: 0.5em; font-size: 1em;')
                                div(id: 'results', style: 'margin-top: 1em;')
                            }

                            // Load Lunr.js and search logic
                            script(src: 'js/vendor/lunr.min.js', '')
                            script(src: 'js/search.js', '')

                            // Optional: init JS for live search
                            script '''
                            document.getElementById('q').addEventListener('input', function (e) {
                                const results = doSearch(e.target.value);
                                const out = document.getElementById('results');
                                out.innerHTML = '';
                                results.forEach(r => {
                                    const div = document.createElement('div');
                                    div.innerHTML = '<p><a href="' + r.url + '">' + r.title + '</a></p>';
                                    out.appendChild(div);
                                });
                            });
                        '''

                            hr(class: 'divider')
                        }
                    }
                }
            }
        }
