

/**
 * called from main; sets up the data and functions to be able to record the canvas
 */
function setupRecorder() {
    console.log("setupRecorder");
    recording = false;

    var recorder = new RecordRTC(canvas.firstChild, {
        type: 'canvas'
    });
    document.getElementById('record-button').onclick = function () {
        if (recording) {
            recording = false;
            recorder.stopRecording(function () {
                var blob = recorder.getBlob();
                saveData(blob, "video.webm");
                // var url = URL.createObjectURL(blob);
                // window.open(url);
            });
        }
        else {
            recording = true;
            recorder.startRecording();
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
