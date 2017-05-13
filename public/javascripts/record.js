/**
 * called from main; sets up the data and functions to be able to record the canvas
 */
function setupRecorder() {
    document.getElementById('record-button').onclick = function () {
        if (recording) {
            recording = false;
            $("#record-card-button").css("color", "rgba(220, 220, 220, 1)");
        }
        else {


            var list = commitsList.children();
            if (commitsListFirstSelected >= 0 && commitsListLastSelected > commitsListFirstSelected) {
                recording = true;
                var resolution = $("#resolution-input").val();

                if (resolution === "1920x1080") {
                    console.log("res set 1080p");
                    canvas.style.width = "1920px";
                    canvas.style.height = "1080px";
                    canvas.style.left = -960 + document.body.clientWidth / 2 + "px";
                }
                else if (resolution === "2560x1440") {
                    console.log("res set 1440p");
                    canvas.style.width = "2560px";
                    canvas.style.height = "1440px";
                    console.log(document.body.clientWidth);
                    canvas.style.left = -1280 + document.body.clientWidth / 2 + "px";
                }
                else if (resolution === "3840x2160") {
                    console.log("res set 2160p");
                    canvas.style.width = "3840px";
                    canvas.style.height = "2160px";
                    canvas.style.left = -1920 + document.body.clientWidth / 2 + "px";
                }
                else {
                }
                // canvas.style.top = "-50%";
                // canvas.style.left = "-1000px";
                renderer.setSize(canvas.clientWidth, canvas.clientHeight);
                camera.aspect = canvas.clientWidth / canvas.clientHeight;
                camera.updateProjectionMatrix();

                renderer.shadowMap.needsUpdate = true;
                render();

                $("#record-card-button").css("color", "rgba(220, 0, 0, 1)");
                CNidx = commitsListFirstSelected;
                CNlist = list;
                CNlast = commitsListLastSelected;
                callNext(CNlist, CNidx, CNlast);
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
