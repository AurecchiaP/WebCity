var scene, camera, renderer, controls;
var geometry, material, mesh;
var canvas;
var pinnedObject, isPinned, pinnedColor;

init();


window.requestAnimationFrame(render);

var frame = 0;
var an;
var upDown = false;

function animate() {
    an = requestAnimationFrame(animate);
    if (frame < 10) {
        var i = meshes.length;
        while (i--) {
            if (i % 2 == 0) {
                if (upDown) {
                    meshes[i].scale.z += 0.5;
                }
                else {
                    meshes[i].scale.z -= 0.5;
                }
            } else {
                if (!upDown) {
                    meshes[i].scale.z += 0.5;
                }
                else {
                    meshes[i].scale.z -= 0.5;
                }
            }
        }
        frame += 0.5;
        renderer.render(scene, camera);
    }
    else {
        cancelAnimationFrame(an);
        frame = 0;
        upDown = !upDown;
    }
}

var btn = document.createElement("button");
btn.style.position = "absolute";
btn.style.top = "10px";
btn.style.left = "10px";
var t = document.createTextNode("transition");
btn.style.display = "none";
btn.appendChild(t);

btn.onclick = function () {
    animate();
};


// FIXME for now, no transition button
// document.body.appendChild(btn);


// Initialise the empty scene, with controls and camera
function init() {
    scene = new THREE.Scene();
    scene.background = new THREE.Color(0xffffff);

    var light = new THREE.DirectionalLight(0xffffff);
    light.position.set(0, 1, 1).normalize();
    scene.add(light);

    camera = new THREE.PerspectiveCamera(50, window.innerWidth / window.innerHeight, 1, 10000);
    camera.position.z = 5000;

    controls = new THREE.OrbitControls(camera);
    controls.maxDistance = 7000;
    controls.minDistance = 0;
    controls.addEventListener('change', render);

    // TODO may have to remove antialias for performance
    // renderer = new THREE.WebGLRenderer({ antialias: true });
    canvas = document.getElementById('canvas');
    renderer = new THREE.WebGLRenderer({antialias: true});
    // canvas = document.getElementById('canvas');
    renderer.setSize(canvas.clientWidth, canvas.clientHeight);

    document.body.appendChild(renderer.domElement);


    // call to server to get the data to draw
    var r = jsRoutes.controllers.HomeController.getVisualizationData();
    $.ajax({
        url: r.url,
        type: r.type,
        success: function (data) {

            if (data) {
                var json = JSON.parse(data);
                draw(json, 3500, 3500);
                console.log(json);
            }
        }, error: function () {
            console.log("invalid server response");
        }
    });
}

var classesText = document.getElementById("classes");
var packagesText = document.getElementById("packages");


function loaded() {
    document.getElementById("loader-container").remove();
    btn.style.display = "block";
    classesText.innerText = "Total classes: 1000";
    packagesText.innerText = "Total packages: 1001";
    render();
}


// handle and update events with moving of the mouse
window.addEventListener('mousemove', onMouseMove, false);
window.addEventListener('resize', onMouseMove, false);
window.addEventListener("keydown", onKeyPress, false);
window.addEventListener("click", onClick, false);


function onClick(e) {
}


function onKeyPress(e) {
    // e
    if (e.keyCode == 69) {
        camera.position.z -= 200;
        controls.update();
        render();

    }
    // f
    else if (e.keyCode == 70) {
        camera.position.z += 200;
        controls.update();
        render();
    }

    else if (e.keyCode == 71) {
        intersects = raycaster.intersectObjects(meshes);
        // if we intersected some objects
        if (pinnedObject) {
            pinnedObject.object.material.visible = false;
            pinnedObject.object.material.color.set(pinnedColor);
            pinnedObject = null;
            isPinned = false;
        }

        if (intersects.length > 0) {
            pinnedObject = intersects[0];
            pinnedColor = pinnedObject.object.material.color;
            pinnedObject.object.material.visible = true;
            pinnedObject.object.material.color.set(0xF1BB4E);
            isPinned = true;
            if (hoveredCube.object.type == "package") {
                nameText.innerText = "Package name: " + hoveredCube.object.name;

                statistic1Text.innerText = "Contained classes: " + hoveredCube.object.classes;
                statistic1Text.innerText = "Total classes: " + hoveredCube.object.totalClasses;
            }
            else if (hoveredCube.object.type == "class") {
                nameText.innerText = "Class name: " + hoveredCube.object.name;
                statistic1Text.innerText = "Contained methods: " + hoveredCube.object.methods;
                statistic2Text.innerText = "Contained attributes: " + hoveredCube.object.attributes;

            }
        }
        renderer.render(scene, camera);
        renderer.render(scene, camera);
    }
}

var mouse = new THREE.Vector2();

function onMouseMove(event) {
    mouse.x = ( ( event.clientX - renderer.domElement.offsetLeft ) / renderer.domElement.clientWidth ) * 2 - 1;
    mouse.y = -( ( event.clientY - renderer.domElement.offsetTop ) / renderer.domElement.clientHeight ) * 2 + 1;
    render();
}

window.addEventListener('resize', onWindowResize, false);

function onWindowResize() {

    camera.aspect = canvas.clientWidth / canvas.clientHeight;

    camera.updateProjectionMatrix();

    renderer.setSize(canvas.clientWidth, canvas.clientHeight);
    camera.updateProjectionMatrix();
    controls.update();

    render();
}


// cast a ray to know if we're intersecting an object
var raycaster = new THREE.Raycaster();
var intersects = [];
var hoveredCube = null;
var hoverText = document.createElement('div');
hoverText.style.position = 'absolute';
hoverText.style.width = 100;
hoverText.style.height = 100;
hoverText.style.textShadow = "-1px 0 rgba(255,255,255,0.8), 0 1px rgba(255,255,255,0.8), 1px 0 rgba(255,255,255,0.8), 0 -1px rgba(255,255,255,0.8)";
document.body.appendChild(hoverText);


var statistic1Text = document.getElementById("statistic1");
var statistic2Text = document.getElementById("statistic2");

var nameText = document.getElementById("name");

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
            }
            else if (hoveredCube.object.type == "class") {
                nameText.innerText = "Class name: " + hoveredCube.object.name;
                statistic1Text.innerText = "Contained methods: " + hoveredCube.object.methods;
                statistic2Text.innerText = "Contained attributes: " + hoveredCube.object.attributes;
            }
        }
    }
    else {
        if(!isPinned) {
            nameText.innerText = "None";

            statistic1Text.innerText = "None";
            statistic2Text.innerText = "None";
        }
    }

    renderer.render(scene, camera);
}
