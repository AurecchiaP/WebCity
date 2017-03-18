

var submitButton = document.getElementById("submitButton");
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

            // the linked repo is valid
            document.getElementById("progressBar").style.width = "100%";

            // start downloading
            var id = setInterval(poll, 2000);
            getData(id);

            console.log("valid repository");

        }, error: function () {
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
            $('.progress-bar').css('width', json.percentage+'%').attr('aria-valuenow', json.percentage);
            $('.progress-bar').html(+ json.task+ '/5');

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
            console.log("get data success");

            // initialize th visualization
            var json = JSON.parse(data);
            init(json);
        }, error: function () {

            // stop polling
            clearInterval(id);
            console.log("get data error");
        }
    });
}