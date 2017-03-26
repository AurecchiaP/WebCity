

var submitButton = document.getElementById("submitButton");
var btn = document.getElementById("testBtn");
var inputField = document.getElementById("inputField");


/**
 * sends a request to the server with the repository linked in the input field; if valid, visualizes it
 */

submitButton.onclick = function() {

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

            // show success message
            $("#successMessage").css('opacity', '1');
            setTimeout(function() {$("#successMessage").css('opacity', '0');}, 2000);

            // the linked repo is valid
            document.getElementById("progressBar").style.width = "100%";

            // start downloading
            var id = setInterval(poll, 1000);
            getData(id);



            console.log("valid repository");

        }, error: function () {

            // show error message
            $("#errorMessage").css('opacity', '1');
            setTimeout(function() {$("#errorMessage").css('opacity', '0');}, 2000);
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
            $('.progress-bar').css('width', json.percentage+'%').attr('aria-valuenow', json.percentage).html(+ json.task - 2 + '/3');

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
            addVersions(json.versions);
            init(json.visualization);
        }, error: function () {

            // stop polling
            clearInterval(id);
            console.log("data fetch error");
        }
    });
}