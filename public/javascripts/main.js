var scene, camera, renderer, controls;
var geometry, material, mesh;
var canvas, mouse;
var pinnedObject;
var raycaster;
var light;
var recorder;
var vector = new THREE.Vector3();

var intersects = [];
var hoveredCube;

// the texts that contain statistics on the objects (lines of code, NOA, NOM...)
var classesText = document.getElementById("classes");
var nameText = document.getElementById("name");
var statistic1 = document.getElementById("statistic1");
var statistic2 = document.getElementById("statistic2");
var statistic3 = document.getElementById("statistic3");



/**
 * takes care of initialising the visualisation (sets up canvas, scene, lights, renderer, controls...)
 */
function init(json) {

    window.requestAnimationFrame(render);

    canvas = document.getElementById('canvas');
    mouse = new THREE.Vector2();
    raycaster = new THREE.Raycaster();

    scene = new THREE.Scene();
    scene.background = new THREE.Color(0xf0f0f0);

    scale = 1250 / Math.max(json.width, json.depth);

    light = new THREE.DirectionalLight(0xffffff, 0.5);


    // light.position.set(-100,-100,200);
    light.position.set(0, 0, 0);

    // shadow settings
    light.castShadow = true;

    // TODO high map size vs shadowMap type; 4096 THREE.PCFSoftShadowMap or 8192 THREE.PCFShadowMap
    light.shadow.mapSize.width = 4096;
    light.shadow.mapSize.height = 4096;

    // change values depending on angle of light
    light.shadow.camera.near = 0;
    light.shadow.camera.far = 5000;
    light.shadow.camera.fov = 90;

    // hardcoded for this light position and light target
    light.shadow.camera.left = -500;
    light.shadow.camera.right = 500;
    light.shadow.camera.top = 500;
    light.shadow.camera.bottom = -500;

    light.shadow.bias = 0.00001;

    scene.add(light, light.target);


    var ambientLight = new THREE.AmbientLight(0xffffff, 0.3); // soft white light
    scene.add(ambientLight);

    // var helper = new THREE.CameraHelper(light.shadow.camera);
    // scene.add(helper);

    renderer = new THREE.WebGLRenderer({
        preserveDrawingBuffer: true,
        antialias: true,
        alpha: true
    });
    renderer.shadowMap.enabled = true;

    renderer.shadowMap.autoUpdate = false;

    // quality vs performance
    renderer.shadowMap.type = THREE.PCFShadowMap; // default
    // renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    // renderer.shadowMap.type = THREE.BasicShadowMap;

    renderer.setSize(canvas.clientWidth, canvas.clientHeight);

    canvas.appendChild(renderer.domElement);

    camera = new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 1, 10000);
    camera.position.z = 2000;

    // OrbitControls to move around the visualization
    controls = new THREE.OrbitControls(camera, renderer.domElement);

    // max and min distance on z axis
    controls.maxDistance = 6500;
    controls.minDistance = 0;

    controls.enableDamping = true;
    controls.dampingFactor = 0.25;

    $("#info-button").on("click", function () {
        $("#info-content").css("display", "block");
    });

    $("#info-content-dismiss").on("click", function () {
        $("#info-content").css("display", "none");
    });

    $("#record-card-button").on("click", function () {
        $("#record-card").css("display", "block");
    });

    $("#record-card-dismiss").on("click", function () {
        $("#record-card").css("display", "none");
    });

    $("#options-card-button").on("click", function () {
        $("#options-card").css("display", "block");
    });

    $("#options-card-dismiss").on("click", function () {
        $("#options-card").css("display", "none");
    });

    $("#reload-button").on("click", reloadVisualization);

    draw(json);
    setupRecorder();
}


/**
 * updates the texts based on the hovered object, and updates the render
 */
var c = 0;
function render() {
    camera.getWorldDirection(vector);

    light.position.copy(camera.position);
    light.target.position.set(light.position.x + vector.x, light.position.y + vector.y, light.position.z + vector.z);

    // ray-casting still slows down a bit, not as much as before
    raycaster.setFromCamera(mouse, camera);
    intersects = raycaster.intersectObjects(meshes);
    // if we intersected some objects
    if (intersects.length > 0) {

        // get the closest intersection
        hoveredCube = intersects[0];

        if (!pinnedObject) {

            if (hoveredCube.object.type === "package") {
                nameText.innerText = hoveredCube.object.name;

                statistic1.firstElementChild.innerText = "contained classes";
                statistic1.firstElementChild.nextElementSibling.innerText = hoveredCube.object.classes;
                statistic2.firstElementChild.innerText = "total classes";
                statistic2.firstElementChild.nextElementSibling.innerText = hoveredCube.object.totalClasses;
                statistic3.firstElementChild.innerText = "";
                statistic3.firstElementChild.nextElementSibling.innerText = "";
            }
            else if (hoveredCube.object.type === "class") {
                nameText.innerText = hoveredCube.object.filename + ":" + hoveredCube.object.name;

                statistic1.firstElementChild.innerText = "methods";
                statistic1.firstElementChild.nextElementSibling.innerText = hoveredCube.object.methods;
                statistic2.firstElementChild.innerText = "attributes";
                statistic2.firstElementChild.nextElementSibling.innerText = hoveredCube.object.attributes;
                statistic3.firstElementChild.innerText = "lines of code";
                statistic3.firstElementChild.nextElementSibling.innerText = hoveredCube.object.linesOfCode;
            }
        }
    }
    else {
        if (!pinnedObject) {
            nameText.innerText = "";

            statistic1.firstElementChild.nextElementSibling.innerText = "0";
            statistic2.firstElementChild.nextElementSibling.innerText = "0";
        }
    }

    c++;
    if (c % 5 === 0) {
        renderer.shadowMap.needsUpdate = true;
    }
    renderer.render(scene, camera);
}