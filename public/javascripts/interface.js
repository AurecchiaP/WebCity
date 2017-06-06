// information on the current repository
var currentCommit, repositoryOwner, repositoryName, repositoryUrl, currentRepo;

// if by commit or tags
var versionType;

// data for the visualization
var visualization;

// list of visible elements
var currentVisibles;

// if we are polling for the download or the parsing
var pollType;


// id to be identified by the server
const id = (Math.random().toString(36) + '00000000000000000').slice(2, 7);

/**
 * sends a request to the server with the repository linked in the input field; if valid, visualizes it
 */
document.getElementById("submitButton").onclick = function () {
    currentRepo = document.getElementById("inputField").value.replace(".git", "");
    $('#submitButton').addClass('disabled');
    var progressBar = document.getElementById("progressBar");
    progressBar.style.width = "100%";
    pollType = "download";
    var pollId = setInterval(poll, 1000);

    // send repo link to server
    var r = jsRoutes.controllers.HomeController.visualization();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: currentRepo,
            id: id
        },
        success: function (data) {
            // the linked repo is valid

            clearInterval(pollId);

            var json = JSON.parse(data);
            console.log(json);

            $("#commits-number").text("Number of commits: " + json.commits.length);
            $("#tags-number").text("Number of tags: " + json.tags.length);
            if (json.tags.length === 0) {
                $("#type-select").children()[1].setAttribute("disabled", "disabled");
            }

            $('.progress-bar').css('width', '0.0%').attr('aria-valuenow', "0.0%").html("");

            progressBar.style.width = "0%";

            $('#submit-card').css('display', 'block');

            console.log("valid repository");

            $("#visualize-button").on("click", function () {
                $('#visualize-button').addClass('disabled');
                progressBar.style.width = "100%";
                var id = setInterval(poll, 1000);
                versionType = $("#type-select").val();
                getData(id, versionType);
            });

        }, error: function () {
            $('#submitButton').removeClass('disabled');
            progressBar.style.width = "0%";

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
        data: {
            id: id
        },
        success: function (data) {

            var json = JSON.parse(data);

            //update the progress bar with the data received from server
            if (pollType === "download") {
                if (json.taskName === "Loading visualization") {
                    $('.progress-bar').css('width', '100%')
                        .attr('aria-valuenow', json.parsingPercentage).html(json.taskName);
                }
                else if (json.taskName === "Receiving objects") {
                    $('.progress-bar').css('width', json.percentage / 3 + '%')
                        .attr('aria-valuenow', json.percentage).html(json.taskName);

                }
                else if (json.taskName === "Resolving deltas") {
                    $('.progress-bar').css('width', 33.3 + (json.percentage / 3) + '%')
                        .attr('aria-valuenow', json.percentage).html(json.taskName);
                }
                else if (json.taskName === "Updating references") {
                    $('.progress-bar').css('width', 0 + (json.percentage) + '%')
                        .attr('aria-valuenow', json.percentage).html(json.taskName);
                }
            }

            else if (pollType === "parse") {
                $('.progress-bar').css('width', json.parsingPercentage + '%')
                    .attr('aria-valuenow', json.parsingPercentage).html("Parsing");
            }

        }, error: function () {
            console.log("poll error");
        }
    });
}

/**
 * call server to get the data for the visualization
 */
function getData(pollId, type) {

    pollType = "parse";

    var r = jsRoutes.controllers.HomeController.getVisualizationData();
    $.ajax({
        url: r.url,
        type: r.type,
        contentType: "application/json; charset=utf-8",
        data: {
            repository: currentRepo,
            type: type,
            id: id

        },
        success: function (data) {

            // stop polling
            clearInterval(pollId);
            console.log("data fetch succesful");

            var json = JSON.parse(data);

            // update the local information about the repository
            var repository = json.details.repository.split("/");
            repositoryName = repository[1];
            repositoryOwner = repository[0];
            repositoryUrl = json.details.repositoryUrl;

            // update the dropdown menus with the new list of commits
            addCommits(json.commits);
            currentCommit = json.commits[0].name;
            $('#current-commit').text(currentCommit);

            // initialize the visualization`
            visualization = json.visualization;
            currentVisibles = json.visibles;
            init(visualization);

        }, error: function () {
            $('#submitButton').removeClass('disabled');
            // stop polling
            clearInterval(pollId);
            console.log("data fetch error");
        }
    });
}