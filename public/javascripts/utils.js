
/**
 * to reverse a string; only if string has unicode characters!
 */
function reverse(s){
    return s.split("").reverse().join("");
}

/**
 * adds the list of versions to the dropdown menu
 */
function addVersions(json) {
    for(var i = 0; i < json.length; ++i) {
        $('.dropdownItems').append("<a class='dropdown-item' href='#'>" + json[i] + "</a>");
    }
}
