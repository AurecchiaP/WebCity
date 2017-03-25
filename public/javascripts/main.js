var scene, camera, renderer, controls;
var geometry, material, mesh;
var canvas, mouse;
var pinnedObject, isPinned, pinnedColor;
var raycaster;

var intersects = [];
var hoveredCube;

// the texts that contain statistics on the objects
var classesText = document.getElementById("classes");
var nameText = document.getElementById("name");
var statistic1 = document.getElementById("statistic1");
var statistic2 = document.getElementById("statistic2");
var statistic3 = document.getElementById("statistic3");

// init();


/**
 * takes care of initialising the visualisation
 */

//TODO parameterize sizes
function init(json) {

    window.requestAnimationFrame(render);

    canvas = document.getElementById('canvas');
    mouse = new THREE.Vector2();
    raycaster = new THREE.Raycaster();

    scene = new THREE.Scene();

    // TODO try to make it better
    // TODO try different materials and stuff
    var light = new THREE.DirectionalLight(0xffffff, 0.5);
    // light.position.set(-100,-100,200);
    light.position.set(0, 0, 1);

    // shadow settings
    light.castShadow = true;

    // TODO max between width and depth?
    var pkgWidth = Math.max(json.width * scale, json.depth * scale);

    // TODO high map size vs shadowMap type; 4096 THREE.PCFSoftShadowMap or 8192 THREE.PCFShadowMap
    light.shadow.mapSize.width = 4096;
    light.shadow.mapSize.height = 4096;
    light.shadow.camera.near = -pkgWidth * 0.05;

    // change values depending on angle of light
    light.shadow.camera.far = pkgWidth * 0.82;
    light.shadow.camera.fov = 90;

    // hardcoded for this light position and light target
    light.shadow.camera.left = 0;
    light.shadow.camera.right = pkgWidth * 0.9;
    light.shadow.camera.top = pkgWidth * 0.95;
    light.shadow.camera.bottom = -pkgWidth * 0.19;

    light.shadow.bias = 0.00001;

    light.shadow.shadowDarkness = 1;

    light.target.position.set(1, 1, -1);
    scene.add(light.target);
    scene.add(light);

    var ambientLight = new THREE.AmbientLight(0x505050); // soft white light
    scene.add(ambientLight);

    // helper to see bounding box and direction of shadow box
    // var helper = new THREE.CameraHelper( light.shadow.camera );
    // scene.add( helper );

    renderer = new THREE.WebGLRenderer({antialias: true, alpha: true});
    renderer.shadowMap.enabled = true;

    // quality vs performance
    renderer.shadowMap.type = THREE.PCFShadowMap; // default
    // renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    // renderer.shadowMap.type = THREE.BasicShadowMap;

    // render shadows only when needed
    renderer.shadowMap.autoUpdate = false;
    renderer.setSize(canvas.clientWidth, canvas.clientHeight);

    canvas.appendChild(renderer.domElement);

    camera = new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 1, 10000);
    camera.position.z = 2000;

    // OrbitControls to move around the visualization
    controls = new THREE.OrbitControls(camera);

    // max and min distance on z axis
    controls.maxDistance = 7000;
    controls.minDistance = 0;

    controls.enableDamping = true;
    controls.dampingFactor = 0.25;

    draw(json);
    console.log(json);
}


/**
 * updates the texts based on the hovered object, and updates the render
 */
function render() {
    // raycasting still slows down a bit, not as much as before
    raycaster.setFromCamera(mouse, camera);
    intersects = raycaster.intersectObjects(meshes);
    // if we intersected some objects
    if (intersects.length > 0) {

        // get the closest intersection
        hoveredCube = intersects[0];

        if (!isPinned) {

            if (hoveredCube.object.type == "package") {
                nameText.innerText = reverse(hoveredCube.object.name);

                statistic1.firstElementChild.innerText = "contained classes";
                statistic1.firstElementChild.nextElementSibling.innerText = hoveredCube.object.classes;
                statistic2.firstElementChild.innerText = "total classes";
                statistic2.firstElementChild.nextElementSibling.innerText = hoveredCube.object.totalClasses;
                statistic3.firstElementChild.innerText = "";
                statistic3.firstElementChild.nextElementSibling.innerText = "";
            }
            else if (hoveredCube.object.type == "class") {
                nameText.innerText = reverse(hoveredCube.object.name);

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
        if (!isPinned) {
            nameText.innerText = "";

            statistic1.firstElementChild.nextElementSibling.innerText = "0";
            statistic2.firstElementChild.nextElementSibling.innerText = "0";
        }
    }

    renderer.render(scene, camera);
}