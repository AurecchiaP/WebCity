var bin = {
    x11: 0,
    x21: 0,
    x12: 3500,
    x22: 3500
};

function draw(data, sizeX, sizeY) {

    drawCube(bin.x22, bin.x12, 10, 0, 0, 5, 0xdd5555, "package");
    var dt = {
        classes: [
            {
                attributes: 0,
                methods: 3,
                path: "box1"
            },
            {
                attributes: 0,
                methods: 5,
                path: "box2"
            },
            {
                attributes: 0,
                methods: 1,
                path: "box3"
            },
            {
                attributes: 0,
                methods: 2,
                path: "box4"
            },
            {
                attributes: 0,
                methods: 10,
                path: "box5"
            },
            {
                attributes: 0,
                methods: 3,
                path: "box6"
            }
        ]
    };
    fitInBin(dt, bin);
    // rec(data);
    // var split = Math.floor(data.children.length / 2);
    // var n = 100;
    // for (var i = 0; i < n; i++) {
    //     for (var j = 0; j < n; j++) {
    //         drawCube(
    //             30,
    //             30,
    //             30,
    //             -(n*30/2)+35*i,
    //             -(n*30/2)+35*j,
    //             10,
    //             0x220000,
    //             data.name + i.toString() + "-" + j.toString()
    //         );
    //     }
    // }

    // TODO merged get same color
    var geometry = mergeMeshes(meshes);
    var material = new THREE.MeshToonMaterial({
        color: 0xffffff,
        shading: THREE.SmoothShading,
        vertexColors: THREE.VertexColors,
        visible: true
    });
    // material.visible = true;
    mesh = new THREE.Mesh(geometry, material);
    scene.add(mesh);
    loaded();

}

function rec(data) {
    for (var child in data.children) {
        rec(child);
    }

    if (canFit(data.classes)) {

    } else {
        // TODO make new bin
    }
    fitInBin(data.classes);
}

function canFit(data) {
    // getSize to know if we can fit in current bin
    return true;
}

function fitInBin(data, bin) {
    // We sort the classes; and make a grid in the bin. if we could put the biggest one in every position of the grid,
    // then we can fit. do so with every class.
    var clss = data.classes;
    // sort by number of methods
    clss.sort(function (a, b) {
        return (a.methods > b.methods) ? 1 : ((b.methods > a.methods) ? -1 : 0);
    });
    var binRatio = Math.floor((bin.x12 - bin.x11)/(bin.x22 - bin.x21));
    // x := such that x*binRatio + x = total
    var cubesPerWidth = Math.floor(clss.length / (binRatio + 1));
    var cubesPerDepth = Math.floor(clss.length / cubesPerWidth);
    var gridXSpacing = (bin.x12 - bin.x11) / (cubesPerWidth);
    var gridYSpacing = (bin.x12 - bin.x11) / (cubesPerDepth);
    var count = 0;
    for (var i = 0; i < cubesPerDepth; i++) {
        for (var j = 0; j < cubesPerWidth; j++) {
            drawCube(clss[count].methods * 100, clss[count].methods * 100, clss[count].methods * 100,-(bin.x12/2) + gridXSpacing * (1 + j * 2) / 2,-(bin.x22/2) + gridYSpacing * (1 + i * 2) / 2, 10 + (clss[count].methods * 100/2), 0x005500, clss[count].path);
            count++;
            if(count >= clss.length) return;
        }
    }
}

var meshes = [];

function drawCube(width, depth, height, posX, posY, posZ, color, name) {
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
    mesh.name = name;
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