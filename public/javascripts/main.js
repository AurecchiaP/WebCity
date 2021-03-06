// data for the 3D visualization
var scene, camera, renderer, controls;
var geometry, material, mesh;
var canvas, mouse;
var raycaster;
var light;
var vector = new THREE.Vector3();

// ratio to keep the visualization sized appropriately
var scale;

// the currently pinned object
var pinnedObject;

// information about the search status
var searchObject;
var searchSelectedItem;
var searchListItems;

// web worker for the search feature
var searchWorker;


// the texts that contain statistics on the objects (lines of code, NOA, NOM...)
var classesText = document.getElementById("classes");
var nameText = document.getElementById("name");
var statistic1 = document.getElementById("statistic1");
var statistic2 = document.getElementById("statistic2");
var statistic3 = document.getElementById("statistic3");


/**
 * takes care of initialising the visualisation (sets up canvas, scene, lights, renderer, controls...)
 *
 * @param {Object} json - the data for the visualization received from the server
 */
function init(json) {

    window.requestAnimationFrame(render);

    canvas = document.getElementById('canvas');
    mouse = new THREE.Vector2();
    raycaster = new THREE.Raycaster();

    scene = new THREE.Scene();
    scene.background = new THREE.Color(0xffffff);

    scale = 1250 / Math.max(json.width, json.depth);

    light = new THREE.DirectionalLight(0xffffff, 0.5);

    light.position.set(-100, 500, 200);

    // shadow settings
    light.castShadow = true;

    // TODO high map size vs shadowMap type; 4096 THREE.PCFSoftShadowMap or 8192 THREE.PCFShadowMap
    light.shadow.mapSize.width = 4096;
    light.shadow.mapSize.height = 4096;

    // change values depending on angle of light
    light.shadow.camera.near = 0;
    light.shadow.camera.far = 2000;
    light.shadow.camera.fov = 90;

    // calculate the size of the visualization so that it fits the screen
    var width = json.width * scale;
    var depth = json.depth * scale;
    var max = Math.max(width, depth);

    // hardcoded for this light position and light target
    light.shadow.camera.left = -(0.5 * max);
    light.shadow.camera.right = max;
    light.shadow.camera.top = 1.5 * max;
    light.shadow.camera.bottom = 0;

    light.shadow.bias = 0.00001;

    scene.add(light, light.target);

    var ambientLight = new THREE.AmbientLight(0xffffff, 0.3);
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
    renderer.shadowMap.type = THREE.PCFShadowMap;
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

    setupListeners();

    draw(json);

    setupRecorder();
}

/**
 * updates the texts based on the hovered object, and updates the render
 */
function render() {

    // cast ray from our position to mouse position
    raycaster.setFromCamera(mouse, camera);
    var intersects = raycaster.intersectObjects(meshes);

    // if we intersected some objects with our rays, find the closest object and put on screen its information
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
    renderer.render(scene, camera);
}