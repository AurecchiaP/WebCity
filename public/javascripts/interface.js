var submitButton = document.getElementById("submitButton");
var btn = document.getElementById("testBtn");
var inputField = document.getElementById("inputField");


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
            // $('.progress-bar').css('width', json.percentage+'%').attr('aria-valuenow', json.percentage).html(+ json.task - 2 + '/3');
            $('.progress-bar').css('width', json.percentage + '%');

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
            addVersions(json.commits, json.dates);
            $('#current-version').text(json.commits[0]);
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

searchInput.on('keyup', function (e) {
    var input = searchInput.val();
    if (input !== "") {
        for (var i = 0; i < searchListItems.length; ++i) {
            if (searchListItems[i].innerText.includes(input)) {
                searchListItems[i].style.display = "block";
            } else {
                searchListItems[i].style.display = "none";
            }
        }
    }

});

searchInput.on('focus', function (e) {
    searchList.css('display', 'block');
});


searchInput.on('blur', function (e) {
    searchList.css('display', 'none');
});

// prevent searchList from disappearing
searchList.on('mousedown', function (e) {
    event.preventDefault();
});

function setSearchResults() {
    searchList.empty();

    // populate the searchList
    for (var i = 0; i < meshes.length; ++i) {
        if (meshes[i].type === "class") {
            searchList.append(" <a href='#' class='search-list-item list-group-item list-group-item-action'>"
                + meshes[i].filename + "</a>");

        } else {
            searchList.append(" <a href='#' class='search-list-item list-group-item list-group-item-action'>"
                + meshes[i].name + "</a>");
        }

    }
    searchListItems = $('.search-list-item');
    // initially set all search results as invisible
    for (var j = 0; j < searchListItems.length; ++j) {
        searchListItems[j].style.display = "none";
    }

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
        searchSelectedItem = e.target;
        searchSelectedItem.classList.add("active");
        searchObject = newSearchObject;
        searchObject.material.visible = true;
        searchObject.material.color.set(0xFF0000);
        renderer.render(scene, camera);
        renderer.render(scene, camera);
    });
}
