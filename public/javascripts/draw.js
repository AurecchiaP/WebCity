function draw(data, sizeX, sizeY) {

    drawCube( sizeX, sizeY, 20, 0, 0, 0, 0xff0000, data.name );

    if (data.classes.length != 0 && data.children.length != 0) {


    }
    else if (data.classes.length != 0) {

    }
    else if (data.children.length != 0) {
        var split = Math.floor(data.children.length / 2);
        // for(var i = 0; i < data.children.length; i++) {
            drawCube(
                1730,
                1730,
                2000,
                865,
                865,
                1020,
                0x220000,
                data.name
            );
        drawCube(
            1730,
            1730,
            20,
            865 - 1740,
            865 - 1740,
            20,
            0x220000,
            data.name
        );
        drawCube(
            1730,
            1730,
            20,
            865,
            865 - 1740,
            20,
            0x220000,
            data.name
        );
        drawCube(
            1730,
            1730,
            20,
            865 - 1740,
            865,
            20,
            0x220000,
            data.name
        );
        // }
        loaded();
    }

    // drawCube( 800, 10, 200, 200, 0, 0xff0000, "package" );
    // drawCube( 200, 500, 0, 0, 250, 0x00ff00, "class" );
}

function drawCube( width, depth, height, posX, posY, posZ, color, name ) {
    geometry = new THREE.BoxGeometry( width, depth, height );
    // TODO basic material gives best performance
    material = new THREE.MeshToonMaterial( { color: color, wireframe: false } );
    mesh = new THREE.Mesh( geometry, material );
    scene.add( mesh );
    mesh.name = name;
    mesh.position.set( posX, posY, posZ );
}
