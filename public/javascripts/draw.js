function draw(data) {


    recDraw(data);

    // TODO merged get same color
    var geometry = mergeMeshes(meshes);
    // TODO best looking, but computationally expensive
    // var material = new THREE.MeshStandardMaterial({
    //     color: 0xffffff,
    //     shading: THREE.SmoothShading,
    //     vertexColors: THREE.VertexColors,
    //     visible: true
    // });

    var material = new THREE.MeshToonMaterial({
        color: 0xffffff,
        shading: THREE.SmoothShading,
        vertexColors: THREE.VertexColors,
        visible: true
    });

    mesh = new THREE.Mesh(geometry, material);


    // bounding box to know size of total mesh; then move camera to its center, and update OrbitControls accordingly
    var box = new THREE.Box3().setFromObject( mesh );
    camera.position.x -= -box.getSize().x/2;
    camera.position.y -= -box.getSize().y/2;
    controls.target.set(box.getSize().x/2,box.getSize().y/2,0);
    controls.update();

    scene.add(mesh);
    loaded();
}


var scale = .2;
function recDraw(data) {
    for (var i = 0; i < data.childPackages.length; ++i) {
        recDraw(data.childPackages[i]);
    }

    // drawCube(data.w, data.w, 10, data.cx, data.cy, data.z, 0xdd5555, data.name);
    // FIXME send only data, so that we dont use 10 parameters

    for (var i = 0; i < data.classes.length; ++i) {
        var cls = data.classes[i];
        var clsHeight = cls.attributes * scale * 30;
        drawClass(cls.methods * scale, cls.methods * scale, clsHeight, cls.cx * scale, cls.cy * scale, (cls.cz * scale) + ((clsHeight/2) + 5), 0x00000ff, cls);
    }

    drawCube(data.w * scale, data.w * scale, 10, data.cx * scale, data.cy * scale, data.z * scale, data.color, data);

}

function drawClass(width, depth, height, posX, posY, posZ, color, data) {
        geometry = new THREE.BoxGeometry(width, depth, height);
        for (var i = 0; i < geometry.faces.length; i++) {
            var face = geometry.faces[i];
            face.color.setHex(color);
        }
        // TODO basic material gives best performance
        material = new THREE.MeshToonMaterial({color: color, wireframe: false});
        // invisible material allows raycasting invisible objects
        material.visible = false;
        mesh = new THREE.Mesh(geometry, material);
        mesh.name = data.name;
        mesh.methods = data.methods;
        mesh.attributes = data.attributes;
        mesh.type = "class";
        mesh.translateX(posX);
        mesh.translateY(posY);
        mesh.translateZ(posZ);
        meshes.push(mesh);
        scene.add(mesh);
}


var meshes = [];

function drawCube(width, depth, height, posX, posY, posZ, color, data) {
    geometry = new THREE.BoxGeometry(width, depth, height);
    for (var i = 0; i < geometry.faces.length; i++) {
        var face = geometry.faces[i];
        face.color.setHex(color);
    }
    // TODO basic material gives best performance
    material = new THREE.MeshToonMaterial({color: color, wireframe: false});
    // invisible material allows raycasting invisible objects
    material.visible = false;
    mesh = new THREE.Mesh(geometry, material);
    mesh.name = data.name;
    mesh.classes = data.classes.length;
    mesh.totalClasses = data.totalClasses;
    mesh.width = width;
    mesh.type = "package";
    mesh.translateX(posX);
    mesh.translateY(posY);
    mesh.translateZ(posZ);
    meshes.push(mesh);
    scene.add(mesh);
}

function mergeMeshes(meshes) {
    var combined = new THREE.Geometry();

    for (var i = 0; i < meshes.length; i++) {
        meshes[i].updateMatrix();
        combined.merge(meshes[i].geometry, meshes[i].matrix);
    }

    return combined;
}