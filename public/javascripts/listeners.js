var commitsDropdown = $("#commits-dropdown");
var commitsDropdownFirst = $("#commits-dropdown-first");
var commitsDropdownLast = $("#commits-dropdown-last");

var commitsList = $("#commits-list");
var commitsListFirst = $("#commits-list-first");
var commitsListLast = $("#commits-list-last");

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