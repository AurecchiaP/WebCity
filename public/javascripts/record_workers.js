// see https://bgrins.github.io/videoconverter.js/

importScripts('ffmpeg-all-codecs.js');

var now = Date.now;

function print(text) {
    postMessage({
        'type' : 'stdout',
        'data' : text
    });
}

/**
 * takes care of building up the data in the right format to then send it to ffmpeg.js
 */
onmessage = function(event) {

    var message = event.data;

    if (message.type === "command") {

        var Module = {
            print: print,
            printErr: print,
            files: message.files || [],
            arguments: message.arguments || [],
            // Can play around with this option - must be a power of 2
            TOTAL_MEMORY: 1073741824
        };

        postMessage({
            'type' : 'start',
            'data' : Module.arguments.join(" ")
        });

        postMessage({
            'type' : 'stdout',
            'data' : 'Received command: ' +
            Module.arguments.join(" ") +
            ((Module.TOTAL_MEMORY) ? ".  Processing with " + Module.TOTAL_MEMORY + " bits." : "")
        });

        var time = now();
        var result = ffmpeg_run(Module);

        var totalTime = now() - time;
        postMessage({
            'type' : 'stdout',
            'data' : 'Finished processing (took ' + totalTime + 'ms)'
        });

        postMessage({
            'type' : 'done',
            'data' : result,
            'time' : totalTime,
            'name' : message.name
        });
    }
};

postMessage({
    'type' : 'ready'
});
