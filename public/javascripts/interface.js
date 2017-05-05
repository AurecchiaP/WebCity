var submitButton = document.getElementById("submitButton");
var btn = document.getElementById("testBtn");
var inputField = document.getElementById("inputField");
var currentCommit, repositoryOwner, repositoryName, repositoryUrl;


/**
 * sends a request to the server with the repository linked in the input field; if valid, visualizes it
 */

submitButton.onclick = function () {

    // send repo link to server
    var r = jsRoutes.controllers.HomeController.visualization();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: inputField.value
        },
        success: function () {

            // the linked repo is valid

            // show success message and progress bar
            $("#successMessage").css('opacity', '1');
            setTimeout(function () {
                $("#successMessage").css('opacity', '0');
            }, 2000);

            document.getElementById("progressBar").style.width = "100%";

            // start downloading
            var id = setInterval(poll, 1000);
            getData(id);


            console.log("valid repository");

        }, error: function () {

            // show error message
            $("#errorMessage").css('opacity', '1');
            setTimeout(function () {
                $("#errorMessage").css('opacity', '0');
            }, 2000);
            console.log("invalid repository");
        }
    });
};

/**
 * polls the server to get percentage on the download
 */
function poll() {
    var r = jsRoutes.controllers.HomeController.poll();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        success: function (data) {

            //update the progress bar with the data received from server
            var json = JSON.parse(data);
            $('.progress-bar').css('width', json.percentage + '%').attr('aria-valuenow', json.percentage).html(json.taskName);
            // $('.progress-bar').css('width', json.percentage+'%').attr('aria-valuenow', json.percentage).html(+ json.task - 2 + '/3');
            // $('.progress-bar').css('width', json.percentage + '%');

        }, error: function () {
            console.log("poll error");
        }
    });
}

/**
 * call server to get the data for the visualization
 */
function getData(id) {
    var r = jsRoutes.controllers.HomeController.getVisualizationData();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        success: function (data) {

            // stop polling
            clearInterval(id);
            console.log("data fetch succesful");

            // initialize th visualization
            var json = JSON.parse(data);
            console.log(json);
            var repository = json.details.repository.split("/");
            repositoryName = repository[1];
            repositoryOwner = repository[0];
            repositoryUrl = json.details.repositoryUrl;
            addCommits(json.commits);
            currentCommit = json.commits[0].name;
            $('#current-commit').text(currentCommit);
            init(json.visualization);
        }, error: function () {

            // stop polling
            clearInterval(id);
            console.log("data fetch error");
        }
    });
}


var searchObject;
var searchSelectedItem;
var searchInput = $('#search-input');
var searchList = $('#search-list');
var searchListItems;

var searchWorker;

searchInput.on('keyup', function () {
    var input = searchInput.val();
    // if we didn't instantiate the worker it means that the browser doesn't support them
    if (typeof(searchWorker) !== "undefined") {

        // make the list of classes into a plain list (can't send HTML elements to web workers)
        var data = [];
        for (var j = 0; j < searchListItems.length; ++j) {
            data[j] = searchListItems[j].innerText;
        }

        // send data to web worker
        searchWorker.postMessage([input, data]);
    }
    // else go through the list manually
    else {
        var it;
        for (var i = 0; i < searchListItems.length; ++i) {
            it = searchListItems[i];
            if (it.innerText.includes(input)) {
                it.style.display = "block";
            } else {
                it.style.display = "none";
            }
        }
    }
});

searchInput.on('focus', function () {
    searchList.css('display', 'block');
});


searchInput.on('blur', function () {
    searchList.css('display', 'none');
});

// prevent searchList from disappearing
searchList.on('mousedown', function () {
    event.preventDefault();
});

function setSearchResults() {
    searchList.empty();

    // populate the searchList
    for (var i = 0; i < meshes.length; ++i) {
        if (meshes[i].type === "class") {
            searchList.append(" <a href='#' class='search-list-item list-group-item list-group-item-action'>"
                + meshes[i].filename + ":" + meshes[i].name + "<br><small>" + meshes[i].type + "</small></a>");

        } else {
            searchList.append(" <a href='#' class='search-list-item list-group-item list-group-item-action'>"
                + meshes[i].name + "<br><small>" + meshes[i].type + "</small></a>");
        }

    }
    searchListItems = $('.search-list-item');

    searchListItems.on('click', function (e) {
        var newSearchObject = meshes[searchListItems.index(e.target)];
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
