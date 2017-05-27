var submitButton = document.getElementById("submitButton");
var btn = document.getElementById("testBtn");
var inputField = document.getElementById("inputField");
var currentCommit, commitsNumber, repositoryOwner, repositoryName, repositoryUrl;
var currentRepo, type;
var visualization, currentVisibles;
var pollType;

const id = (Math.random().toString(36) + '00000000000000000').slice(2, 7);


/**
 * sends a request to the server with the repository linked in the input field; if valid, visualizes it
 */

submitButton.onclick = function () {
    currentRepo = inputField.value.replace(".git", "");
    $('#submitButton').addClass('disabled');
    document.getElementById("progressBar").style.width = "100%";
    pollType = "download";
    var pollId = setInterval(poll, 1000);

    // send repo link to server
    var r = jsRoutes.controllers.HomeController.visualization();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: currentRepo,
            id: id
        },
        success: function (data) {
            // the linked repo is valid

            clearInterval(pollId);

            var json = JSON.parse(data);
            console.log(json);

            $("#commits-number").text("Number of commits: " + json.commits.length);
            $("#tags-number").text("Number of tags: " + json.tags.length);
            if (json.tags.length === 0) {
                $("#type-select").children()[1].setAttribute("disabled", "disabled");
            }

            $('.progress-bar').css('width', '0.0%')
                .attr('aria-valuenow', "0.0%").html("");

            document.getElementById("progressBar").style.width = "0%";


            $('#submit-card').css('display', 'block');

            // start downloading
            // var id = setInterval(poll, 1000);
            // getData(id, type);


            console.log("valid repository");

        }, error: function () {
            $('#submitButton').removeClass('disabled');
            document.getElementById("progressBar").style.width = "0%";

            // show error message
            $("#errorMessage").css('opacity', '1');
            setTimeout(function () {
                $("#errorMessage").css('opacity', '0');
            }, 2000);
            console.log("invalid repository");
        }
    });
};

$("#visualize-button").on("click", function () {
    $('#visualize-button').addClass('disabled');
    document.getElementById("progressBar").style.width = "100%";
    var id = setInterval(poll, 1000);
    type = $("#type-select").val();
    getData(id, type);
});

/**
 * polls the server to get percentage on the download
 */
function poll() {
    var r = jsRoutes.controllers.HomeController.poll();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            id: id
        },
        success: function (data) {

            //update the progress bar with the data received from server
            var json = JSON.parse(data);


            if (pollType === "download") {
                if (json.taskName === "Loading visualization") {
                    $('.progress-bar').css('width', '100%')
                        .attr('aria-valuenow', json.parsingPercentage).html(json.taskName);
                }
                else if (json.taskName === "Receiving objects") {
                    $('.progress-bar').css('width', json.percentage / 3 + '%')
                        .attr('aria-valuenow', json.percentage).html(json.taskName);

                }
                else if (json.taskName === "Resolving deltas") {
                    $('.progress-bar').css('width', 33.3 + (json.percentage / 3) + '%')
                        .attr('aria-valuenow', json.percentage).html(json.taskName);
                }
                else
                if (json.taskName === "Updating references") {
                    $('.progress-bar').css('width', 0 + (json.percentage) + '%')
                        .attr('aria-valuenow', json.percentage).html(json.taskName);
                }
            }

            else if (pollType === "parse") {
                $('.progress-bar').css('width', json.parsingPercentage + '%')
                    .attr('aria-valuenow', json.parsingPercentage).html("Parsing");
            }

        }, error: function () {
            console.log("poll error");
        }
    });
}

/**
 * call server to get the data for the visualization
 */
function getData(pollId, type) {

    pollType = "parse";

    var r = jsRoutes.controllers.HomeController.getVisualizationData();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: currentRepo,
            type: type,
            id: id

        },
        success: function (data) {

            // stop polling
            clearInterval(pollId);
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
            visualization = json.visualization;
            currentVisibles = json.visibles;
            init(visualization);
        }, error: function () {
            $('#submitButton').removeClass('disabled');
            // stop polling
            clearInterval(pollId);
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
var recordWorker;

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
