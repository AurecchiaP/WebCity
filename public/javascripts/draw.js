function draw() {
    drawCube( 800, 10, 200, 200, 0, 0xff0000, "package" );
    drawCube( 200, 500, 0, 0, 250, 0x00ff00, "class" );
}

function drawCube( width, height, posX, posY, posZ, color, name ) {
    geometry = new THREE.BoxGeometry( width, width, height );
    material = new THREE.MeshBasicMaterial( { color: color, wireframe: false } );
    mesh = new THREE.Mesh( geometry, material );
    scene.add( mesh );
    mesh.name = name;
    mesh.position.set( posX, posY, posZ );
}
