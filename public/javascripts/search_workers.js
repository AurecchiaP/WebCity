self.addEventListener('message', function (e) {
    var input = e.data[0];
    var data = e.data[1];
    for (var i = 0; i < data.length; ++i) {

        if (data[i].includes(input)) {
            data[i] = "block";
        }
        else {
            data[i] = "none";
        }
    }


    self.postMessage(data);
}, false);