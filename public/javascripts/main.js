var scene, camera, renderer, controls;
var geometry, material, mesh;

init();
window.requestAnimationFrame( render );


// Initialise the empty scene, with controls and camera
function init() {
    scene = new THREE.Scene();
    scene.background = new THREE.Color( 0xffffff );

    camera = new THREE.PerspectiveCamera( 50, window.innerWidth / window.innerHeight, 1, 10000 );
    camera.position.z = 5000;

    controls = new THREE.OrbitControls( camera );
    controls.addEventListener( 'change', render );

    renderer = new THREE.WebGLRenderer();
    renderer.setSize( window.innerWidth, window.innerHeight );

    document.body.appendChild( renderer.domElement );


    // call to server to get the data to draw
    var r = jsRoutes.controllers.HomeController.getClasses();
    $.ajax({
        url: r.url,
        type: r.type,
        success: function(data) {
            console.log(data);
            var json = JSON.parse(data)
        }, error: function() {
            console.log("fail")
        }
    });

    draw();
}


// handle and update events with moving of the mouse
window.addEventListener( 'mousemove', onMouseMove, false );

var mouse = new THREE.Vector2();
function onMouseMove( event ) {

    mouse.x = ( event.clientX / window.innerWidth ) * 2 - 1;
    mouse.y = - ( event.clientY / window.innerHeight ) * 2 + 1;
    render();
}



// cast a ray to know if we're intersecting an object
var raycaster = new THREE.Raycaster();
var hoveredCube = null;
var hoverText = document.createElement( 'div' );
hoverText.style.position = 'absolute';
hoverText.style.width = 100;
hoverText.style.height = 100;
document.body.appendChild( hoverText );

function render() {


    raycaster.setFromCamera( mouse, camera );
    var intersects = raycaster.intersectObjects( scene.children );

    // if we intersected some objects
    if( intersects.length > 0 ) {
        // if( hoveredCube ) {
            // hoveredCube.object.material.color.set( 0xff0000 );
        // }
        // get the closest intersection
        hoveredCube = intersects[0];
        // hoveredCube.object.material.color.set( 0xff00ff );

        // update text
        hoverText.innerHTML = hoveredCube.object.name;
        hoverText.style.top = event.clientY + 'px';
        hoverText.style.left = event.clientX + 'px';
        hoverText.hidden = false;

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
    }

    renderer.render( scene, camera );
}