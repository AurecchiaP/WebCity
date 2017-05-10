/**
 * adds the list of commits to the dropdown menu
 */
function addCommits(commits) {
    var items = $('.dropdown-items');
    for(var i = 0; i < commits.length; ++i) {
        items.append("<a href='#' class='list-group-item list-group-item-action'>" +
            commits[i].name + "<br>" + commits[i].description + "<br>" +
            "<small>" + commits[i].author + ", " + commits[i].date + "</small></a>");
    }
    items.on('click', $('.dropdown-item'), getCommit);
}

function getCommit(e) {
    var commit = e.target.innerHTML.split("<")[0];

    var r = jsRoutes.controllers.HomeController.getCommit();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: currentRepo,
            commit: commit
        },
        success: function (data) {
            console.log("valid commit");
            var json = JSON.parse(data);
            currentCommit = commit;
            $('#current-commit').text(currentCommit);
            clearVisualization();
            draw(json.visualization);

        }, error: function () {
            console.log("invalid commit");
        }
    });
}

/**
 * adds the list of tags to the dropdown menu
 */
function addTags(tags) {
    var items = $('.dropdown-items');
    for(var i = 0; i < tags.length; ++i) {
        items.append("<a href='#' class='list-group-item list-group-item-action'>" + tags[i].name + "</a>");
    }
    items.on('click', $('.dropdown-item'), getCommit);
}

function getTags(e) {
    var tag = e.target.innerHTML.split("<")[0];

    var r = jsRoutes.controllers.HomeController.getTag();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: currentRepo,
            tag: tag
        },
        success: function (data) {
            console.log("valid tag");
            var json = JSON.parse(data);
            currentVersion = tag;
            $('#current-version').text(currentTag);
            clearVisualization();
            draw(json.visualization);

        }, error: function () {
            console.log("invalid tag");
        }
    });
}

function reloadVisualization() {
    var r = jsRoutes.controllers.HomeController.reloadVisualization();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: currentRepo,
            commit: currentCommit,
            padding: $('#padding-input').val() || padding,
            minClassSize: $('#minClassesSize-input').val() || minClassSize
},
        success: function (data) {
            minClassSize = parseInt($('#minClassesSize-input').val() || minClassSize);
            padding = parseInt($('#padding-input').val() || padding);
            console.log("valid reload");
            var json = JSON.parse(data);
            console.log(json);
            clearVisualization();
            draw(json.visualization);


        }, error: function () {
            console.log("invalid reload");
        }
    });
}