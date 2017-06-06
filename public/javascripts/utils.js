/**
 * adds the list of commits to the dropdown menus
 */
function addCommits(commits) {
    var items = $('.dropdown-items');
    for (var i = 0; i < commits.length; ++i) {
        items.append("<a href='#' class='list-group-item list-group-item-action'><p>" +
            commits[i].name + "<br>" + commits[i].description + "<br>" + commits[i].author + ", " + commits[i].date + "</p></a>");
    }
    items.on('click', $('.dropdown-item'), getCommit);
}

/**
 * ask the server to send new data for the same version, but with different sizes of classes, packages and padding
 */
function getCommit(e) {

    // get the clicked commit
    var commit = e.target.innerText.split("\n")[0];

    // update the commit shown in the dropdown menu
    $("#commits-dropdown")[0].innerText = commit || currentCommit;

    var r = jsRoutes.controllers.HomeController.getCommit();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: currentRepo,
            commit: commit || currentCommit,
            type: versionType
        },
        success: function (data) {

            var json = JSON.parse(data);
            // update the local list of elements that should be visible in the visualization
            currentVisibles = json.visibles;

            // update the current commit
            currentCommit = commit || currentCommit;

            // remove the old objects of the visualization and load the new ones
            clearVisualization();
            draw(visualization);


        }, error: function () {
            console.log("invalid reload");
        }
    });
}

/**
 * ask the server to send new data for the same version, but with different sizes of classes, packages and padding
 */
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
            type: versionType,
            padding: $('#padding-input').val() || padding,
            minClassSize: $('#minClassesSize-input').val() || minClassSize
        },
        success: function (data) {

            // update the local values of MinClassSize, padding and packageHeight
            minClassSize = parseInt($('#minClassesSize-input').val() || minClassSize);
            padding = parseInt($('#padding-input').val() || padding);
            packageHeight = parseInt($('#packageHeight-input').val() || packageHeight);

            console.log("valid reload");

            /*  parse the new data received from server, clean the visualization from the old objects and visualize
             the new data */
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


/**
 * checks if any entry of the given array is Undefined
 *
 * @param {Array} arr - the array to be checked
 */
function containsUndefined(arr) {
    for (var i = 0; i < arr.length; ++i) {
        if (arr[i] === undefined) return true;
    }
    return false;
}

/**
 * rotates the visualization around the focus point, by radians amount
 *
 * @param {Number} radians - by how much we should rotate the visualization
 */
function rotateLeft(radians) {
    controls.rotateLeft(radians);
    controls.update();
    renderer.render(scene, camera);
    renderer.render(scene, camera);
}

/**
 * updates the list of entries that match the input in the search bar
 */
function setSearchResults() {
    var searchList = $('#search-list');
    searchList.empty();

    // populate the searchList
    for (var i = 0; i < meshes.length; ++i) {
        if (meshes[i].type === "class") {
            searchList.append(" <button class='search-list-item list-group-item list-group-item-action'><div class='grid'>"
                + meshes[i].filename + ":" + meshes[i].name + "<small>" + meshes[i].type + "</small></div></button>");

        } else {
            searchList.append(" <button class='search-list-item list-group-item list-group-item-action'><div class='grid'>"
                + meshes[i].name + "<small>" + meshes[i].type + "</small></div></button>");
        }

    }
    searchListItems = $('.search-list-item');

    searchListItems.on('click', function (e) {
        var newSearchObject = meshes[searchListItems.index(e.currentTarget)];

        // if an object is already selected
        if (searchObject) {
            searchSelectedItem.classList.remove("active");
            searchObject.material.visible = false;

            // we clicked twice on the same object, so it's not invisible; nothing else to do, return
            if (searchObject === newSearchObject) {
                searchObject = null;
                searchSelectedItem = null;
                renderer.render(scene, camera);
                renderer.render(scene, camera);
                return;
            }
        }
        // point camera at selected mesh
        var vector = new THREE.Vector3();
        vector.setFromMatrixPosition(newSearchObject.matrixWorld);
        controls.target.set(vector.x, vector.y, vector.z);
        controls.update();

        searchSelectedItem = e.target;
        searchSelectedItem.classList.add("active");
        searchObject = newSearchObject;
        searchObject.material.visible = true;
        searchObject.material.color.set(0xA9CF54);
        renderer.render(scene, camera);
        renderer.render(scene, camera);
    });
}