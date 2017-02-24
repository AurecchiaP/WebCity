var scene, camera, renderer, controls;
var geometry, material, mesh;

init();
animate();


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




    var r = jsRoutes.controllers.HomeController.testo();
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

function draw() {
    drawCube( 800, 10, 200, 200, 0, 0xff0000, "package" );
    drawCube( 200, 500, 0, 0, 250, 0x00ff00, "class" );
}

function drawCube(width, height, posX, posY, posZ, color, name) {
    geometry = new THREE.BoxGeometry( width, width, height );
    material = new THREE.MeshBasicMaterial( { color: color, wireframe: false } );
    mesh = new THREE.Mesh( geometry, material );
    scene.add( mesh );
    mesh.name = name;
    mesh.position.set( posX, posY, posZ );
}

function animate() {
    renderer.render( scene, camera );
}


var raycaster = new THREE.Raycaster();
var mouse = new THREE.Vector2();

function onMouseMove( event ) {

    // calculate mouse position in normalized device coordinates
    // (-1 to +1) for both components
    mouse.x = ( event.clientX / window.innerWidth ) * 2 - 1;
    mouse.y = - ( event.clientY / window.innerHeight ) * 2 + 1;
    render();
}

var hoveredCube = null;
var text2 = document.createElement('div');
document.body.appendChild(text2);


function render() {

    // update the picking ray with the camera and mouse position
    raycaster.setFromCamera( mouse, camera );

    // calculate objects intersecting the picking ray
    var intersects = raycaster.intersectObjects( scene.children );


    if( intersects.length > 0 ) {
        if( hoveredCube ) {
            // hoveredCube.object.material.color.set( 0xff0000 );
        }

        hoveredCube = intersects[0];
        // hoveredCube.object.material.color.set( 0xff00ff );


        text2.style.position = 'absolute';
        text2.style.width = 100;
        text2.style.height = 100;
        text2.innerHTML = hoveredCube.object.name;
        text2.style.top = event.clientY + 'px';
        text2.style.left = event.clientX + 'px';
        text2.hidden = false;

    }
    else {
        if(hoveredCube) {
            // hoveredCube.object.material.color.set( 0xff0000 );
            hoveredCube = null;
        }
        if(text2.hidden == false) {
            text2.hidden = true;
        }
    }

    renderer.render( scene, camera );

}

window.addEventListener( 'mousemove', onMouseMove, false );

window.requestAnimationFrame(render);