/**
 * called by init; takes care of adding the event listeners to various elements before we load the visualization
 */
function setupListeners() {

    // question mark button at the bottom right
    $("#info-button").on("click", function () {
        $("#info-content").css("display", "block");
    });

    $("#info-content-dismiss").on("click", function () {
        $("#info-content").css("display", "none");
    });

    // camera button on the header bar
    $("#record-card-button").on("click", function () {
        $("#record-card").css("display", "block");
    });

    $("#record-card-dismiss").on("click", function () {
        $("#record-card").css("display", "none");
    });

    // cog button on the header bar
    $("#options-card-button").on("click", function () {
        $("#options-card").css("display", "block");
    });

    $("#options-card-dismiss").on("click", function () {
        $("#options-card").css("display", "none");
    });

    // button that is visible after we click the cog button
    $("#reload-button").on("click", reloadVisualization);

    // dropdown elements that when focused make the lists appear
    var commitsDropdown = $("#commits-dropdown");
    var commitsDropdownFirst = $("#commits-dropdown-first");
    var commitsDropdownLast = $("#commits-dropdown-last");

    // main commits list
    commitsList = $("#commits-list");

    // commits lists used for the recording feature
    commitsListFirst = $("#commits-list-first");
    commitsListLast = $("#commits-list-last");

    commitsDropdown.on('focus', function () {
        commitsList.css('display', 'block');
    });

    commitsDropdown.on('blur', function () {
        commitsList.css('display', 'none');
    });

    commitsDropdownFirst.on('focus', function () {
        commitsListFirst.css('display', 'block');
    });

    commitsDropdownFirst.on('blur', function () {
        commitsListFirst.css('display', 'none');
    });

    commitsDropdownLast.on('focus', function () {
        commitsListLast.css('display', 'block');
    });

    commitsDropdownLast.on('blur', function () {
        commitsListLast.css('display', 'none');
    });

    commitsList.on('mousedown', function (e) {
        var element;
        if (e.target.parentElement.tagName !== "DIV") {
            element = e.target.parentElement;
        }
        else {
            element = e.target;
        }

        commitsDropdown.text(element.firstChild.innerText.split("\n")[0]);

        event.preventDefault();
    });

    commitsListFirst.on('mousedown', function (e) {
        var element;
        if (e.target.parentElement.tagName !== "DIV") {
            element = e.target.parentElement;
        }
        else {
            element = e.target;
        }

        commitsDropdownFirst.text(element.firstChild.innerText.split("\n")[0]);
        commitsListFirstSelected = commitsListFirst.children().index(element);

        event.preventDefault();
    });

    commitsListLast.on('mousedown', function (e) {
        var element;
        if (e.target.parentElement.tagName !== "DIV") {
            element = e.target.parentElement;
        }
        else {
            element = e.target;
        }

        commitsDropdownLast.text(element.firstChild.innerText.split("\n")[0]);
        commitsListLastSelected = commitsListLast.children().index(element);

        event.preventDefault();
    });

    var searchInput = $('#search-input');
    var searchList = $('#search-list');


    searchInput.on('keyup', function () {
        var input = $('#search-input').val();

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



}
