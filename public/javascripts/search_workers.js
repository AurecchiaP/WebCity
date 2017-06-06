/**
 * web worker used to make the search feature more responsive. It receives the input from the user and the list of
 * elements with their respective names, and checks if the names match the user's input.
 */
self.addEventListener('message', function (e) {
    // user input
    var input = e.data[0];
    // list of elements (classes, packages)
    var data = e.data[1];

    // iterate elements and see if their name matches the input
    for (var i = 0; i < data.length; ++i) {

        if (data[i].includes(input)) {
            data[i] = "block";
        }
        else {
            data[i] = "none";
        }
    }

    // return modified data
    self.postMessage(data);
}, false);