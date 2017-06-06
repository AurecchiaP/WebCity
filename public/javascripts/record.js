// variables to know the type of recording
var reversedRecording;
var orbit;
var generatingVideo = false;
var recording = false;

// array of web workers for recording
var recordWorkers;

// the array that will contain the 8 partial video before they are merged into one
var videoData = [];

/**
 * called from main; sets up the data and functions to be able to record the canvas
 */
function setupRecorder() {

    document.getElementById('record-button').onclick = function () {
        if (recording) {
            recording = false;

            // reset data
            videoData = [];
            files = [];
            count = 0;

            // reset canvas size to window,
            canvas.style.width = "100%";
            canvas.style.height = "100%";
            canvas.style.left = "0";
            renderer.setSize(canvas.clientWidth, canvas.clientHeight);
            camera.aspect = canvas.clientWidth / canvas.clientHeight;
            camera.updateProjectionMatrix();

            renderer.shadowMap.needsUpdate = true;
            render();
            $("#record-button").text("Record");
            $("#record-card-button").css("color", "rgba(220, 220, 220, 1)");
            $('#record-progress-bar').css('width','0');
        }
        else {
            if (!recordWorkers) {
                setupRecordWorkers();
            }

            var list = commitsList.children();

            if (commitsListFirstSelected >= 0 && commitsListLastSelected >= 0 && Math.abs(commitsListFirstSelected - commitsListLastSelected) >= 8) {
                if (commitsListLastSelected < commitsListFirstSelected) {
                    reversedRecording = true;
                }
                recording = true;
                orbit = $("#orbit-checkbox").is(':checked');
                var resolution = $("#resolution-input").val();

                $('#record-progress-bar').css('width','100%');

                $("#record-button").text("Cancel");

                if (resolution === "1280x720") {
                    canvas.style.width = "1280px";
                    canvas.style.height = "720px";
                    canvas.style.left = -640 + document.body.clientWidth / 2 + "px";
                }
                else if (resolution === "1920x1080") {
                    canvas.style.width = "1920px";
                    canvas.style.height = "1080px";
                    canvas.style.left = -960 + document.body.clientWidth / 2 + "px";
                }
                else if (resolution === "2560x1440") {
                    canvas.style.width = "2560px";
                    canvas.style.height = "1440px";
                    canvas.style.left = -1280 + document.body.clientWidth / 2 + "px";
                }

                renderer.setSize(canvas.clientWidth, canvas.clientHeight);
                camera.aspect = canvas.clientWidth / canvas.clientHeight;
                camera.updateProjectionMatrix();

                renderer.shadowMap.needsUpdate = true;
                render();

                $("#record-card-button").css("color", "rgba(220, 0, 0, 1)");

                firstCommitIndex = commitsListFirstSelected;
                currentCommitIndex = firstCommitIndex;
                lastCommitIndex = commitsListLastSelected;

                list[firstCommitIndex].click();
            }
            else {
                console.log("invalid first or last commit selected (minimum 8 commits of difference)");
            }
        }
    }
}

/**
 * initializes the web workers used to speed up the recording process
 */
function setupRecordWorkers() {
    recordWorkers = [];
    for (var i = 0; i < 8; ++i) {
        recordWorkers[i] = new Worker("assets/javascripts/record_workers.js");
        recordWorkers[i].onmessage = function (event) {
            var message = event.data;
            if (message.type === "ready") {
                this.postMessage({
                    type: "command",
                    arguments: ["-help"]
                });
            } else if (message.type === "stdout") {
                // console.log(message.data);
            } else if (message.type === "start") {
                // console.log(message.data);
            } else if (message.type === "done") {
                // console.log(message);
                var buffers = message.data;

                if (buffers.length) {
                    var buffer = buffers[0];
                    var arr = buffer.data;
                    var byteArray = new Uint8Array(arr);
                    var blob = new Blob([byteArray], {type: 'application/octet-stream'});

                    if (Number.isInteger(message.name)) {
                        videoData[message.name] = {
                            "name": "vid" + message.name + ".mp4",
                            "data": byteArray
                        };
                    }

                    if (!containsUndefined(videoData) && videoData.length === 8) {
                        this.postMessage({
                            type: "command",
                            arguments: ['-i', 'vid0.mp4', '-i', 'vid1.mp4', '-i', 'vid2.mp4', '-i', 'vid3.mp4',
                                '-i', 'vid4.mp4', '-i', 'vid5.mp4', '-i', 'vid6.mp4', '-i', 'vid7.mp4', '-filter_complex',
                                '[0:v:0] [1:v:0] [2:v:0] [3:v:0] [4:v:0] [5:v:0] [6:v:0] [7:v:0] concat=n=8:v=1 [v]',
                                '-map', '[v]', repositoryName + '.mp4'],
                            files: videoData,
                            name: "concat"
                        });
                        videoData = [];
                        files = [];
                        count = 0;
                    }

                    if (message.name === "concat") {
                        $("#record-button").removeClass("disabled");
                        var a = window.document.createElement('a');
                        a.href = window.URL.createObjectURL(blob);
                        a.download = buffer.name;
                        document.body.appendChild(a);
                        a.click();
                        document.body.removeChild(a);
                        generatingVideo = false;
                        $('#record-before').css("display", "none");
                        $('#record-progress-bar').css('width','0');
                    }
                }
            }
        };
    }
}


// the array where we store the screenshots of the canvas before we merge them into a video
var files = [];

/**
 * merges together an array of meshes
 *
 * @param {String} dataURI - the data that we want to transform into binary
 * @returns {Uint8Array} - the data in binary format
 */
function convertDataURIToBinary(dataURI) {
    var BASE64_MARKER = ';base64,';
    var base64Index = dataURI.indexOf(BASE64_MARKER) + BASE64_MARKER.length;
    var base64 = dataURI.substring(base64Index);
    var raw = window.atob(base64);
    var rawLength = raw.length;
    var array = new Uint8Array(new ArrayBuffer(rawLength));

    for (var i = 0; i < rawLength; i++) {
        array[i] = raw.charCodeAt(i);
    }
    return array;
}

var count = 0;

// variables to keep track of the status of the recording
var currentCommitIndex, firstCommitIndex, lastCommitIndex;

/**
 * takes a screenshot of the canvas and goes to the next version until we're done recording
 */
function callNext() {

    // take a screenshot of the canvas, and push it to the list of files with the right format so that FFmpeg.js
    // can find the pictures later
    var image = renderer.domElement.toDataURL('image/jpeg');

    if (count < 10) {
        files.push({
            "name": "img0000" + count++ + ".jpg",
            "data": convertDataURIToBinary(image)
        });
    }
    else if (count < 100) {
        files.push({
            "name": "img000" + count++ + ".jpg",
            "data": convertDataURIToBinary(image)
        });
    } else if (count < 1000) {
        files.push({
            "name": "img00" + count++ + ".jpg",
            "data": convertDataURIToBinary(image)
        });
    }
    else if (count < 10000) {
        files.push({
            "name": "img0" + count++ + ".jpg",
            "data": convertDataURIToBinary(image)
        });
    }
    else {
        files.push({
            "name": "img" + count++ + ".jpg",
            "data": convertDataURIToBinary(image)
        });
    }

    // if we are not done recording yet
    if (recording && (
        (currentCommitIndex <= lastCommitIndex && currentCommitIndex < lastCommitIndex - firstCommitIndex)
        ||
        (reversedRecording && currentCommitIndex >= lastCommitIndex && currentCommitIndex <= firstCommitIndex - lastCommitIndex))) {
        commitsList.children()[currentCommitIndex].click();
        var percentage;
        if (reversedRecording) {
            currentCommitIndex--;
            percentage = (firstCommitIndex - currentCommitIndex) / (firstCommitIndex - lastCommitIndex) * 66.6;


            $('.record-progress-bar').css('width', percentage + '%')
                .attr('aria-valuenow', percentage);
        }
        else {
            currentCommitIndex++;
            percentage = (currentCommitIndex) / (lastCommitIndex - firstCommitIndex) * 66.6;
            $('.record-progress-bar').css('width', percentage + '%')
                .attr('aria-valuenow', percentage);
        }

        if (orbit) {
            rotateLeft(Math.PI/180);
        }
    }
    // if we are done recording
    else {
        setTimeout(function () {
            if (recording) {
                generatingVideo = true;
                recording = false;
                var button = $("#record-button");
                button.text("Record");
                button.addClass("disabled");
                canvas.style.width = "100%";
                canvas.style.height = "100%";
                canvas.style.left = "0";
                renderer.setSize(canvas.clientWidth, canvas.clientHeight);
                camera.aspect = canvas.clientWidth / canvas.clientHeight;
                camera.updateProjectionMatrix();

                render();

                // update the progress bar
                updateGenerationProgress(0, Math.ceil(Math.abs((lastCommitIndex - firstCommitIndex))));

                // split the list of screenshots into 8 lists,
                // and sends each of them to a web worker that will merge them into a video
                var split = Math.abs(Math.ceil((lastCommitIndex - firstCommitIndex)/ 8));
                var start = Math.min(lastCommitIndex, firstCommitIndex);
                for (var i = 0; i < 8; ++i) {
                    recordWorkers[i].postMessage({
                        type: "command",
                        arguments: ['-r', '24', '-start_number', start + i * split, '-i', 'img%05d.jpg', '-v', 'verbose',
                            '-pix_fmt', 'yuv420p', '-vframes', split, 'vid' + i + '.mp4'],
                        files: files,
                        name: i
                    });
                }

                $("#record-card-button").css("color", "rgba(220, 220, 220, 1)");
                $('#record-before').css("display", "inline");
            }
        }, 100);
    }
}


/**
 * updates the progress bar of the recording; updates itself until we're done recording
 *
 * @param {Number} time - the time that has passed
 * @param {Number} expected - the expected time that the recording will take
 */
function updateGenerationProgress(time, expected) {
    // if the time passed is less than the expected, and we are recording or generating the video, update the progress
    if (time < expected && (recording || generatingVideo)) {
        var percentage = time/expected * 33.3;
        $('.record-progress-bar').css('width', 66.6 + percentage + '%')
            .attr('aria-valuenow', 66.6 + percentage);

        setTimeout(function () {
            updateGenerationProgress(time + 1, expected);
        },1000);
    }
}

