function draw(data, sizeX, sizeY) {


    recDraw(data);

    // TODO merged get same color
    var geometry = mergeMeshes(meshes);
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
    for (var i = 0; i < data.children.length; ++i) {
        recDraw(data.children[i]);
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

// function rec(data) {
//     for (var child in data.children) {
//         rec(child);
//     }
//
//     if (canFit(data.classes)) {
//
//     } else {
//         // TODO make new bin
//     }
//     fitInBin(data.classes);
// }

// function canFit(data) {
//     // getSize to know if we can fit in current bin
//     return true;
// }

// function fitInBin(data, bin) {
//     // We sort the classes; and make a grid in the bin. if we could put the biggest one in every position of the grid,
//     // then we can fit. do so with every class.
//     var clss = data.classes;
//     // sort by number of methods
//     clss.sort(function (a, b) {
//         return (a.methods > b.methods) ? 1 : ((b.methods > a.methods) ? -1 : 0);
//     });
//     var binRatio = Math.floor((bin.x12 - bin.x11) / (bin.x22 - bin.x21));
//     // x := such that x*binRatio + x = total
//     var cubesPerWidth = Math.floor(clss.length / (binRatio + 1));
//     var cubesPerDepth = Math.floor(clss.length / cubesPerWidth);
//     var gridXSpacing = (bin.x12 - bin.x11) / (cubesPerWidth);
//     var gridYSpacing = (bin.x12 - bin.x11) / (cubesPerDepth);
//     var count = 0;
//     for (var i = 0; i < cubesPerDepth; i++) {
//         for (var j = 0; j < cubesPerWidth; j++) {
//             drawCube(clss[count].methods, clss[count].methods, clss[count].methods, -(bin.x12 / 2) + gridXSpacing * (1 + j * 2) / 2, -(bin.x22 / 2) + gridYSpacing * (1 + i * 2) / 2, 10 + (clss[count].methods * 100 / 2), 0x005500, clss[count].path);
//             count++;
//             if (count >= clss.length) return;
//         }
//     }
// }

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