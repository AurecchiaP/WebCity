/**
 * adds the list of versions to the dropdown menu
 */
function addVersions(commits, dates) {
    var items = $('.dropdown-items');
    for(var i = 0; i < commits.length; ++i) {
        // FIXME on click would also return the date!
        // searchList.append(" <a href='#' class='search-list-item list-group-item list-group-item-action'>"
        //     + meshes[i].filename + "</a>");
        items.append("<a href='#' class='list-group-item list-group-item-action'>" + commits[i] + "<small>" + dates[i] + "</small></a>");
    }
    items.on('click', $('.dropdown-item'), getVersion);
}

function getVersion(e) {

    var r = jsRoutes.controllers.HomeController.getVersion();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            version: e.target.innerText
        },
        success: function (data) {
            console.log("valid version");
            var json = JSON.parse(data);
            $('#current-version').text(e.target.innerText);
            clearVisualization();
            draw(json.visualization);

        }, error: function () {
            console.log("invalid version");
        }
    });
}