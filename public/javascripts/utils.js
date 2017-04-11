/**
 * adds the list of versions to the dropdown menu
 */
function addVersions(json) {

    for(var i = 0; i < json.length; ++i) {
        $('.dropdownItems').append("<a class='dropdown-item' href='#'>" + json[i] + "</a>");
    }
    $('.dropdownItems').on('click', $('.dropdown-item'), getVersion);
}

function getVersion(e) {
    console.log(e.target.innerText);

    var r = jsRoutes.controllers.HomeController.getVersion();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            version: e.target.innerText
        },
        success: function (data) {
            console.log("valid version");
            var json = JSON.parse(data);
            console.log(json);
            $('#current-version').text(e.target.innerText);
            clearVisualization();
            draw(json.visualization);

        }, error: function () {
            console.log("invalid version");
        }
    });
}