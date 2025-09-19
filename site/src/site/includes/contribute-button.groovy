div(id: 'contribute-btn') {
    button(type: 'button', class: 'btn btn-default',
            onclick: "window.location.href=\"https://github.com/apache/groovy-website/tree/asf-site/site/src/site/pages/${currentPage}.groovy\"") {
        i(class: 'fa-classic fa-regular fa-pen-to-square') {}
        yield ' Improve this doc'
    }
}
