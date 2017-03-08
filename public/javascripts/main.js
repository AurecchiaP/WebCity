var scene, camera, renderer, controls;
var geometry, material, mesh;
var canvas;

init();


window.requestAnimationFrame( render );

var frame = 0;
var an;
var upDown = false;

function animate() {
    an = requestAnimationFrame(animate);
    if(frame < 10) {
        var i = meshes.length;
        while (i--) {
            if(i%2 == 0) {
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
        cancelAnimationFrame( an );
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

btn.onclick = function() {
    animate();
};

// document.body.appendChild(btn);


// Initialise the empty scene, with controls and camera
function init() {
    scene = new THREE.Scene();
    scene.background = new THREE.Color( 0xffffff );

    var light = new THREE.DirectionalLight( 0xffffff );
    light.position.set( 0, 1, 1 ).normalize();
    scene.add(light);

    camera = new THREE.PerspectiveCamera( 50, window.innerWidth / window.innerHeight, 1, 10000 );
    camera.position.z = 5000;

    controls = new THREE.OrbitControls( camera );
    controls.maxDistance = 7000;
    controls.minDistance = 0;
    controls.addEventListener( 'change', render );

    // TODO may have to remove antialias for performance
    // renderer = new THREE.WebGLRenderer({ antialias: true });
    canvas = document.getElementById('canvas');
    renderer = new THREE.WebGLRenderer({ antialias: true });
    // canvas = document.getElementById('canvas');
    renderer.setSize( canvas.clientWidth, canvas.clientHeight );

    document.body.appendChild( renderer.domElement );


    // call to server to get the data to draw
    var r = jsRoutes.controllers.HomeController.getClasses();
    $.ajax({
        url: r.url,
        type: r.type,
        success: function(data) {

            if(data) {
                var json = JSON.parse(data);
                draw(json, 3500, 3500);
                console.log(json);
            }
        }, error: function() {
            console.log("invalid server response");
        }
    });
}

function loaded() {
    document.getElementById("loader-container").remove();
    btn.style.display = "block";
    render();
}


// handle and update events with moving of the mouse
window.addEventListener( 'mousemove', onMouseMove, false );
window.addEventListener( 'resize', onMouseMove, false );
window.addEventListener("keydown", onKeyPress, false);


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
}

var mouse = new THREE.Vector2();

function onMouseMove( event ) {
    mouse.x = ( ( event.clientX - renderer.domElement.offsetLeft ) / renderer.domElement.clientWidth ) * 2 - 1;
    mouse.y = - ( ( event.clientY - renderer.domElement.offsetTop ) / renderer.domElement.clientHeight ) * 2 + 1;
    render();
}

window.addEventListener( 'resize', onWindowResize, false );

function onWindowResize(){

    camera.aspect = canvas.clientWidth/ canvas.clientHeight;

    camera.updateProjectionMatrix();

    renderer.setSize(canvas.clientWidth, canvas.clientHeight);
    camera.updateProjectionMatrix();
    controls.update();
    // controls.handleResize();

    render();
}


// cast a ray to know if we're intersecting an object
var raycaster = new THREE.Raycaster();
var intersects = [];
var hoveredCube = null;
var hoverText = document.createElement( 'div' );
hoverText.style.position = 'absolute';
hoverText.style.width = 100;
hoverText.style.height = 100;
hoverText.style.textShadow = "-1px 0 rgba(255,255,255,0.8), 0 1px rgba(255,255,255,0.8), 1px 0 rgba(255,255,255,0.8), 0 -1px rgba(255,255,255,0.8)";
document.body.appendChild( hoverText );

var classesText = document.getElementById("classes");
var nameText = document.getElementById("name");

function render() {
    // raycasting still slows down a bit, not as much as before
    raycaster.setFromCamera( mouse, camera );
    intersects = raycaster.intersectObjects( meshes );
    // if we intersected some objects
    if( intersects.length > 0 ) {
        // if( hoveredCube ) {
            // hoveredCube.object.material.color.set( 0xff0000 );
        // }
        // get the closest intersection
        hoveredCube = intersects[0];
        // hoveredCube.object.material.color.set( 0xff00ff );

        // update text
        // hoverText.innerHTML = hoveredCube.object.name;
        // hoverText.style.top = event.clientY + 'px';
        // hoverText.style.left = event.clientX + 'px';
        // hoverText.hidden = false;


        classesText.innerText = "Contained classes: " + hoveredCube.object.classes;
        nameText.innerText = "Package name: " + hoveredCube.object.name;

    }
    else {
        // if( hoveredCube ) {
            // hoveredCube.object.material.color.set( 0xff0000 );
            // hoveredCube = null;
        // }
        // hide text
        if( hoverText.hidden == false ) {
            hoverText.hidden = true;
        }
        nameText.innerText = "None";
    }

    renderer.render( scene, camera );
}