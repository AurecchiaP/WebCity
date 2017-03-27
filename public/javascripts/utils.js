/**
 * adds the list of versions to the dropdown menu
 */
function addVersions(json) {
    for(var i = 0; i < json.length; ++i) {
        $('.dropdownItems').append("<a class='dropdown-item' href='#'>" + json[i] + "</a>");
    }
}
