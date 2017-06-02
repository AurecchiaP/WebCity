/**
 * adds the list of commits to the dropdown menu
 */
function addCommits(commits) {
    var items = $('.dropdown-items');
    commitsNumber = commits.length;
    for (var i = 0; i < commits.length; ++i) {
        items.append("<a href='#' class='list-group-item list-group-item-action'><p>" +
            commits[i].name + "<br>" + commits[i].description + "<br>" + commits[i].author + ", " + commits[i].date + "</p></a>");
    }
    items.on('click', $('.dropdown-item'), getCommit);
}

function getCommit(e) {

    var commit = e.target.innerText.split("\n")[0];

    $("#commits-dropdown")[0].innerText = commit || currentCommit;

    var r = jsRoutes.controllers.HomeController.getCommit();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: currentRepo,
            commit: commit || currentCommit,
            type: type
        },
        success: function (data) {
            var json = JSON.parse(data);
            currentVisibles = json.visibles;
            currentCommit = commit || currentCommit;
            clearVisualization();
            draw(visualization);


        }, error: function () {
            console.log("invalid reload");
        }
    });
}

function reloadVisualization() {
    $("#reload-button").attr("disabled", "disabled");

    var r = jsRoutes.controllers.HomeController.reloadVisualization();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: currentRepo,
            commit: currentCommit,
            type: type,
            padding: $('#padding-input').val() || padding,
            minClassSize: $('#minClassesSize-input').val() || minClassSize
        },
        success: function (data) {
            minClassSize = parseInt($('#minClassesSize-input').val() || minClassSize);
            padding = parseInt($('#padding-input').val() || padding);
            packageHeight = parseInt($('#packageHeight-input').val() || packageHeight);
            console.log("valid reload");
            var json = JSON.parse(data);
            visualization = json.visualization;
            clearVisualization();
            draw(visualization);

            $("#reload-button").removeAttr("disabled");

        }, error: function () {
            console.log("invalid reload");
        }
    });
}

function containsUndefined(arr) {
    for (var i = 0; i < arr.length; ++i) {
        if (arr[i] === undefined) return true;
    }
    return false;
}

function rotateLeft(radians) {
    controls.rotateLeft(Math.PI/180);
    controls.update();
    renderer.render(scene, camera);
    renderer.render(scene, camera);
}