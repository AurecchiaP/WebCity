var meshes = [];
const scale = .1;
const packageHeight = 100;


/**
 * function that takes care of drawing the packages and classes
 * @param {object} pkg - the root package of the visualization to be drawn
 */
function draw(pkg) {

    // create the meshes for all the packages and classes
    recDraw(pkg);

    // merge the meshes into a single new mesh; this makes the visualization faster/less computationally expensive.
    // we still have the array `meshes` with the invisible, single meshes so that we can tell them apart for callbacks and
    // other similar things
    var geometry = mergeMeshes(meshes);

    // TODO best looking, but computationally expensive
    var material = new THREE.MeshStandardMaterial({
        color: 0xffffff,
        shading: THREE.SmoothShading,
        vertexColors: THREE.VertexColors,
        visible: true
    });

    // var material = new THREE.MeshToonMaterial({
    //     color: 0xffffff,
    //     shading: THREE.SmoothShading,
    //     vertexColors: THREE.VertexColors,
    //     visible: true
    // });

    mesh = new THREE.Mesh(geometry, material);


    // bounding box to know size of total mesh; then move camera to its center, and update OrbitControls accordingly
    var box = new THREE.Box3().setFromObject(mesh);
    camera.position.x -= -box.getSize().x / 2;
    camera.position.y -= -box.getSize().y / 2;
    controls.target.set(box.getSize().x / 2, box.getSize().y / 2, 0);
    controls.update();

    // add the mesh to the scene and notify that the visualization is ready
    mesh.castShadow = true;
    mesh.receiveShadow = true;
    scene.add(mesh);
    loaded(pkg);
}

/**
 * recursively call recDraw on child packages and draw the packages and their classes
 * @param {object} pkg - the root package of the visualization to be drawn
 */
function recDraw(pkg) {
    // recursion on the child packages, to be drawn first
    for (var i = 0; i < pkg.childPackages.length; ++i) {
        recDraw(pkg.childPackages[i]);
    }

    // draw the classes of pkg
    for (var j = 0; j < pkg.classes.length; ++j) {
        drawClass(pkg.classes[j]);
    }

    drawPackage(pkg);
}


/**
 * creates the mesh representing the given class, with the right position, size and attributes
 * @param {object} cls - the object representing the class to be drawn
 */
function drawClass(cls) {
    // adding 5 to attributes and methods, to have a lower bound (else we won't see the class)
    // var clsHeight = (cls.attributes + 5) * scale * 30;
    // var clsWidth = (cls.methods + 5) * scale;
    var clsHeight = (cls.methods + 5);
    var clsWidth = (cls.attributes + 5);

    // var clsHeight = (cls.methods);
    // var clsWidth = (cls.attributes);

    var posX = cls.cx * scale;
    var posY = cls.cy * scale;
    var posZ = (cls.cz * scale * 100) + ((clsHeight / 2) + 5);

    var color = cls.color;

    // create geometry and material for this class
    geometry = new THREE.BoxGeometry(clsWidth, clsWidth, clsHeight);
    for (var i = 0; i < geometry.faces.length; i++) {
        var face = geometry.faces[i];
        face.color.setHex(color);
    }
    material = new THREE.MeshToonMaterial({color: color, wireframe: false});

    // invisible material allows raycasting invisible objects
    material.visible = false;

    // create the mash with the needed data
    mesh = new THREE.Mesh(geometry, material);
    mesh.name = cls.name;
    mesh.methods = cls.methods;
    mesh.attributes = cls.attributes;
    mesh.linesOfCode = cls.linesOfCode;
    mesh.type = "class";

    // position the mesh
    mesh.translateX(posX);
    mesh.translateY(posY);
    mesh.translateZ(posZ);

    // add mesh to the array of meshes and the scene
    meshes.push(mesh);
    scene.add(mesh);
}


/**
 * creates the mesh representing the given package, with the right position, size and attributes
 * @param {object} pkg - the object representing the package to be drawn
 */
function drawPackage(pkg) {

    // size of the package
    var width = pkg.w * scale;
    var height = packageHeight * scale;

    // position of package
    var posX = pkg.cx * scale;
    var posY = pkg.cy * scale;
    var posZ = pkg.z * packageHeight * scale;

    var color = pkg.color;

    // create geometry and material for this package
    geometry = new THREE.BoxGeometry(width, width, height);
    for (var i = 0; i < geometry.faces.length; i++) {
        var face = geometry.faces[i];
        face.color.setHex(color);
    }
    material = new THREE.MeshToonMaterial({color: color, wireframe: false});

    // invisible material allows raycasting invisible objects
    material.visible = false;

    // create the mash with the needed data
    mesh = new THREE.Mesh(geometry, material);
    mesh.name = pkg.name;
    mesh.classes = pkg.classes.length;
    mesh.totalClasses = pkg.totalClasses;
    mesh.width = width;
    mesh.type = "package";

    // position the mesh
    mesh.translateX(posX);
    mesh.translateY(posY);
    mesh.translateZ(posZ);

    // add mesh to the array of meshes and the scene
    meshes.push(mesh);
    scene.add(mesh);
}


/**
 * readies the page when the visualization is loaded
 */
function loaded(data) {
    document.getElementById("loader-container").remove();
    classesText.innerText = "Total classes: " + data.totalClasses;
    render();
}


/**
 * merges together an array of meshes
 *
 * @param {Array} meshes - the array of meshes to merge
 * @returns the combined mesh
 */
function mergeMeshes(meshes) {
    var combined = new THREE.Geometry();

    for (var i = 0; i < meshes.length; i++) {
        meshes[i].updateMatrix();
        combined.merge(meshes[i].geometry, meshes[i].matrix);
    }

    return combined;
}