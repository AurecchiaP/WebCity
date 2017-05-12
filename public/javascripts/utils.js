/**
 * adds the list of commits to the dropdown menu
 */
function addCommits(commits) {
    var items = $('.dropdown-items');
    console.log(commits);
    for(var i = 0; i < commits.length; ++i) {
        items.append("<a href='#' class='list-group-item list-group-item-action'>" +
            commits[i].name + "<br>" + commits[i].description + "<br>" + commits[i].author + ", " + commits[i].date + "</a>");
    }
    items.on('click', $('.dropdown-item'), getCommit);
}

function getCommit(e) {
    var commit;
    if(e.target.parentElement.tagName !== "DIV"){
        commit = e.target.parentElement.innerHTML.split("<")[0];
    }
    else {
        commit = e.target.innerHTML.split("<")[0];
    }

    $("#commits-dropdown")[0].innerText = commit || currentCommit;


    var r = jsRoutes.controllers.HomeController.reloadVisualization();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: currentRepo,
            commit: commit,
            padding: $('#padding-input').val() || padding,
            minClassSize: $('#minClassesSize-input').val() || minClassSize
        },
        success: function (data) {
            minClassSize = parseInt($('#minClassesSize-input').val() || minClassSize);
            padding = parseInt($('#padding-input').val() || padding);
            packageHeight =  parseInt($('#packageHeight-input').val() || packageHeight);
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
            packageHeight =  parseInt($('#packageHeight-input').val() || packageHeight);
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