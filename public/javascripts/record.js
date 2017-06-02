var reversedRecording;
var orbit;

/**
 * called from main; sets up the data and functions to be able to record the canvas
 */
function setupRecorder() {

    document.getElementById('record-button').onclick = function () {
        if (recording) {
            recording = false;
            videoData = [];
            files = [];
            count = 0;
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
                console.log("setting up record workers");
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
                    console.log("res set 720p");
                    canvas.style.width = "1280px";
                    canvas.style.height = "720px";
                    canvas.style.left = -640 + document.body.clientWidth / 2 + "px";
                }
                else if (resolution === "1920x1080") {
                    console.log("res set 1080p");
                    canvas.style.width = "1920px";
                    canvas.style.height = "1080px";
                    console.log(document.body.clientWidth);
                    canvas.style.left = -960 + document.body.clientWidth / 2 + "px";
                }
                else if (resolution === "2560x1440") {
                    console.log("res set 1440p");
                    canvas.style.width = "2560px";
                    canvas.style.height = "1440px";
                    console.log(document.body.clientWidth);
                    canvas.style.left = -1280 + document.body.clientWidth / 2 + "px";
                }

                renderer.setSize(canvas.clientWidth, canvas.clientHeight);
                camera.aspect = canvas.clientWidth / canvas.clientHeight;
                camera.updateProjectionMatrix();

                renderer.shadowMap.needsUpdate = true;
                render();

                $("#record-card-button").css("color", "rgba(220, 0, 0, 1)");
                CNfirst = commitsListFirstSelected;
                CNidx = CNfirst;
                CNlist = list;
                CNlast = commitsListLastSelected;
                CNlist[CNfirst].click();
            }
            else {
                console.log("invalid first or last commit selected (minimum 8 commits of difference)");
            }
        }
    }
}

/**
 * utility function called when the recording is stopped, used to download the video
 */
var saveData = (function () {
    var a = document.createElement("a");
    document.body.appendChild(a);
    a.style = "display: none";
    return function (blob, fileName) {
        var url = window.URL.createObjectURL(blob);
        a.href = url;
        a.download = fileName;
        a.click();
        window.URL.revokeObjectURL(url);
    };
}());

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
                console.log(message.data);
            } else if (message.type === "done") {
                console.log(message);
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
                        $('#record-before').css("display", "inline");
                    }

                    if (message.name === "concat") {
                        console.log(new Date().toLocaleTimeString());
                        $("#record-button").removeClass("disabled");
                        var a = window.document.createElement('a');
                        a.href = window.URL.createObjectURL(blob);
                        a.download = buffer.name;
                        document.body.appendChild(a);
                        a.click();
                        document.body.removeChild(a);
                        $('#record-before').css("display", "none");
                        $('#record-progress-bar').css('width','0');
                    }
                }
            }
        };
    }
}
