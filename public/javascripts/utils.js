/**
 * adds the list of commits to the dropdown menu
 */
function addCommits(commits) {
    var items = $('.dropdown-items');
    for(var i = 0; i < commits.length; ++i) {
        items.append("<a href='#' class='list-group-item list-group-item-action'>" +
            commits[i].name + "<small>" + commits[i].date + "</small></a>");
    }
    items.on('click', $('.dropdown-item'), getCommit);
}

function getCommit(e) {

    var r = jsRoutes.controllers.HomeController.getCommit();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            commit: e.target.innerText
        },
        success: function (data) {
            console.log("valid commit");
            var json = JSON.parse(data);
            currentCommit = e.target.innerText;
            $('#current-commit').text(currentCommit);
            clearVisualization();
            draw(json.visualization);

        }, error: function () {
            console.log("invalid commit");
        }
    });
}