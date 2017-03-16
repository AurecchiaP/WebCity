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


init();


/**
 * takes care of initialising the visualisation
 */
function init() {

    window.requestAnimationFrame(render);

    canvas = document.getElementById('canvas');
    mouse = new THREE.Vector2();
    raycaster = new THREE.Raycaster();

    scene = new THREE.Scene();
    scene.background = new THREE.Color(0xffffff);

    // var directionalLight = new THREE.DirectionalLight( 0xffffff, 0.5 );
    // directionalLight.position.set(1000,1000,5000);
    // directionalLight.castShadow = true;
    // directionalLight.shadow.camera.near = 1;
    // directionalLight.shadow.camera.far = 500;
    // scene.add( directionalLight );

    var light = new THREE.PointLight( 0xffffff, 1, 0, 2 );
    light.position.set( 500, 500, 500 );
    light.castShadow = true;
    light.shadow.mapSize.width = 1024;
    light.shadow.mapSize.height = 1024;

    light.shadow.camera.left = 500;
    scene.add( light );

// var ambientLight = new THREE.AmbientLight( 0x151515 ); // soft white light
// scene.add( ambientLight );

    camera = new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 1, 10000);
    camera.position.z = 5000;

    // OrbitControls to move around the visualization
    controls = new THREE.OrbitControls(camera);

    // max and min distance on z axis
    controls.maxDistance = 7000;
    controls.minDistance = 0;
    controls.addEventListener('change', render);

    renderer = new THREE.WebGLRenderer({antialias: true});
    renderer.shadowMap.enabled = true;
    renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    renderer.setSize(canvas.clientWidth, canvas.clientHeight);

    document.body.appendChild(renderer.domElement);


    // call to server to get the data to draw
    var r = jsRoutes.controllers.HomeController.getVisualizationData();
    $.ajax({
        url: r.url,
        type: r.type,
        success: function (data) {
            var json = JSON.parse(data);
            draw(json);
            console.log(json);
        }, error: function () {
            console.log("invalid server response");
        }
    });

    //Creating box
var boxGeometry = new THREE.BoxGeometry( 1, 1, 1 );
var boxMaterial = new THREE.MeshPhongMaterial( { color: 0xdddddd, specular: 0x999999, shininess: 15, shading: THREE.FlatShading } );
var box = new THREE.Mesh( boxGeometry, boxMaterial );
box.castShadow = true;
scene.add( box );



    var planeGeometry = new THREE.PlaneGeometry( 20, 20, 32, 32 );
    var planeMaterial = new THREE.MeshPhongMaterial( { color: 0x00dddd, specular: 0x009900, shininess: 10, shading: THREE.FlatShading } )
    var plane = new THREE.Mesh( planeGeometry, planeMaterial );
    plane.receiveShadow = true;
    scene.add( plane );
    plane.position.z = -2;
    // plane.rotation.x = -Math.PI/2;

//     camera.position.z = 5;
// camera.position.y = 2;
// camera.lookAt(box.position);

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