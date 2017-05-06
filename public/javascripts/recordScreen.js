

/**
 * called from main; sets up the data and functions to be able to record the canvas
 */
function setupRecorder() {
    console.log("setupRecorder");
    recording = false;

    // var recorder = new RecordRTC(, {
    var elementToShare = document.body;
    var canvas2d = document.createElement('canvas');
    var context = canvas2d.getContext('2d');

    canvas2d.width = elementToShare.clientWidth;
    canvas2d.height = elementToShare.clientHeight;

    canvas2d.style.position = 'absolute';
    canvas2d.style.top = 0;
    canvas2d.style.left = 0;
    canvas2d.style.zIndex = -1;
    (document.body || document.documentElement).appendChild(canvas2d);


    (function looper() {
        if(!recording) {
            return setTimeout(looper, 500);
        }
        html2canvas(elementToShare, {
            grabMouse: false,
            allowTaint: true,
            letterRendering: true,
            useCORS: true,
            onrendered: function(canvas) {
                context.clearRect(0, 0, canvas2d.width, canvas2d.height);
                context.drawImage(canvas, 0, 0, canvas2d.width, canvas2d.height);
                if(!recording) {
                    return;
                }
                setTimeout(looper, 1);
            }
        });
    })();

    var recorder = new RecordRTC(canvas2d, {
        type: 'canvas',
        showMousePointer: false
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
