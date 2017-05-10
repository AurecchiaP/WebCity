/**
 * called from main; sets up the data and functions to be able to record the canvas
 */
function setupRecorder() {
    document.getElementById('record-button').onclick = function () {
        if (recording) {
            recording = false;
            recorder.stopRecording(function () {
                var blob = recorder.getBlob();
                saveData(blob, repositoryName + ".webm");
                // this.clearRecordedData();
                // var url = URL.createObjectURL(blob);
                // window.open(url);
            });
            $("#record-card-button").css("color", "rgba(220, 220, 220, 1)");
        }
        else {


            /* screenshot
            canvas.style.width = "3840px";
            canvas.style.height = "2160px";
            var image = renderer.domElement.toDataURL("image/png").replace("image/png", "image/octet-stream");  // here is the most important part because if you dont replace you will get a DOM 18 exception.
            window.location.href=image;
            return;
            */

            var list = commitsList.children();
            if (commitsListFirstSelected >= 0 && commitsListLastSelected > commitsListFirstSelected) {
                recorder = new RecordRTC(canvas.firstChild, {
                    type: 'canvas'

                });
                recording = true;
                // canvas.style.width = "2560px";
                // canvas.style.height = "1440px";
                canvas.style.width = "3840px";
                canvas.style.height = "2160px";
                // canvas.style.width = "640px";
                // canvas.style.height = "320px";
                $("#record-card-button").css("color", "rgba(220, 0, 0, 1)");
                recorder.startRecording();
                callNext(list, commitsListFirstSelected, commitsListLastSelected);
            }
            else {
                console.log("invalid first or last commit selected");
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
