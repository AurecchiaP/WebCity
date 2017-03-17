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
var statistic1Text = document.getElementById("statistic1");
var statistic2Text = document.getElementById("statistic2");
var statistic3Text = document.getElementById("statistic3");


// init();


/**
 * takes care of initialising the visualisation
 */

//TODO parameterize sizes
function init(json) {

    console.log(json);
    window.requestAnimationFrame(render);

    canvas = document.getElementById('canvas');
    mouse = new THREE.Vector2();
    raycaster = new THREE.Raycaster();

    scene = new THREE.Scene();
    scene.background = new THREE.Color(0xffffff);

    // TODO try to make it better
    // TODO try to fit it exactly and change direction by setting position and target
    // TODO try different materials and stuff
    var light = new THREE.DirectionalLight( 0xffffff, 0.5 );
    light.position.set(-100,-100,200);

    // shadow settings
    light.castShadow = true;

    // TODO high map size vs shadowMap type
    light.shadow.mapSize.width = 4096;
    light.shadow.mapSize.height = 4096;
    light.shadow.camera.near = 125;

    // z of light position + margin of 5
    light.shadow.camera.far = 1205;
    light.shadow.camera.fov = 90;

    light.shadow.camera.left = 0;
    light.shadow.camera.right = 900;
    light.shadow.camera.top = 900;
    light.shadow.camera.bottom = -200;

    light.shadow.bias = 0.00001;

    light.shadow.shadowDarkness = 1;

    scene.add( light );

    var ambientLight = new THREE.AmbientLight( 0x505050 ); // soft white light
    scene.add( ambientLight );

    // helper to see bounding box and direction of shadow box
    // var helper = new THREE.CameraHelper( light.shadow.camera );
    // scene.add( helper );

    camera = new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 1, 10000);
    camera.position.z = 2000;

    // OrbitControls to move around the visualization
    controls = new THREE.OrbitControls(camera);

    // max and min distance on z axis
    controls.maxDistance = 7000;
    controls.minDistance = 0;
    controls.addEventListener('change', render);

    renderer = new THREE.WebGLRenderer({antialias: true});
    renderer.shadowMap.enabled = true;

    // quality vs performance
    // renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    renderer.shadowMap.type = THREE.PCFShadowMap;
    // renderer.shadowMap.type = THREE.BasicShadowMap;

    // render shadows only when needed
    renderer.shadowMap.autoUpdate = false;
    renderer.setSize(canvas.clientWidth, canvas.clientHeight);

    document.body.appendChild(renderer.domElement);

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
                nameText.innerText = "Package name: " + hoveredCube.object.name;

                statistic1Text.innerText = "Contained classes: " + hoveredCube.object.classes;
                statistic2Text.innerText = "Total classes: " + hoveredCube.object.totalClasses;
                statistic3Text.innerText = "None";
            }
            else if (hoveredCube.object.type == "class") {
                nameText.innerText = "Class name: " + hoveredCube.object.name;
                statistic1Text.innerText = "Contained methods: " + hoveredCube.object.methods;
                statistic2Text.innerText = "Contained attributes: " + hoveredCube.object.attributes;
                statistic3Text.innerText = "Lines of code: " + hoveredCube.object.linesOfCode;
            }
        }
    }
    else {
        if (!isPinned) {
            nameText.innerText = "None";

            statistic1Text.innerText = "None";
            statistic2Text.innerText = "None";
        }
    }

    renderer.render(scene, camera);
}