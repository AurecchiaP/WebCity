

var submitButton = document.getElementById("submitButton");
var inputField = document.getElementById("inputField");

/**
 * sends a request to the server with the repository linked in the input field; if valid, visualizes it
 */
submitButton.onclick = function() {

    document.getElementById("progressBar").style.width = "100%";

    var r = jsRoutes.controllers.HomeController.visualization();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: inputField.value
        },
        success: function (data) {
            var json = JSON.parse(data);
            init(json);
            console.log("success");
        }, error: function () {
            console.log("error");
        }
    });
};
