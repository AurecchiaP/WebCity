var meshes = [];
var scale;
var packageHeight;
var box;
var classes = [];
var recorder, recording = false;
var commitsList;


/**
 * function that takes care of drawing the packages and classes
 * @param {object} drwPkg - the root package of the visualization to be drawn
 */
function draw(drwPkg) {
    packageHeight = 20;

    // create the meshes for all the packages and classes
    var totalClasses = recDraw(drwPkg);

    // merge the meshes into a single new mesh; this makes the visualization faster/less computationally expensive.
    // we still have the array `meshes` with the invisible, single meshes so that we can tell them apart for callbacks and
    // other similar things
    geometry = mergeMeshes(meshes);

    // TODO best looking, but computationally expensive
    // var material = new THREE.MeshStandardMaterial({
    //     color: 0xffffff,
    //     shading: THREE.SmoothShading,
    //     vertexColors: THREE.VertexColors,
    //     visible: true
    // });

    material = new THREE.MeshPhongMaterial({
        shading: THREE.SmoothShading,
        vertexColors: THREE.VertexColors,
        visible: true
    });
    mesh = new THREE.Mesh(geometry, material);

    // bounding box to know size of total mesh; then move camera to its center, and update OrbitControls accordingly
    if (!box) {
        box = new THREE.Box3().setFromObject(mesh);
        camera.position.x -= -box.getSize().x / 2;
        camera.position.y -= -box.getSize().y / 2;
        controls.target.set(box.getSize().x / 2, box.getSize().y / 2, 0);
        controls.update();
    }

    // add the mesh to the scene and notify that the visualization is ready
    mesh.castShadow = true;
    mesh.receiveShadow = true;
    scene.add(mesh);
    loaded(totalClasses);
}

/**
 * cleans the scene of the current objects, to prepare for a new visualisation
 */
function clearVisualization() {
    scene.children.forEach(function (object) {
        if (object.type === "Mesh") {
            scene.remove(object);
            object.material.dispose();
            object.geometry.dispose();
            object = undefined;
        }
    });
    meshes.forEach(function (object) {
        scene.remove(object);
        object.material.dispose();
        object.geometry.dispose();
        object = undefined;
    });
    meshes = [];
}

/**
 * recursively call recDraw on child packages and draw the packages and their classes
 * @param {object} drwPkg - the root package of the visualization to be drawn
 */
function recDraw(drwPkg) {
    var totalClasses = 0;
    if (!drwPkg.visible || (drwPkg.width === 0 && drwPkg.depth === 0)) return 0;
    // recursion on the child packages, to be drawn first
    for (var i = 0; i < drwPkg.drawablePackages.length; ++i) {
        totalClasses += recDraw(drwPkg.drawablePackages[i]);
    }

    // draw the classes of pkg
    for (var j = 0; j < drwPkg.drawableClasses.length; ++j) {
        drawClass(drwPkg.drawableClasses[j]);
    }

    totalClasses += drawPackage(drwPkg, totalClasses);
    return totalClasses;
}


/**
 * creates the mesh representing the given class, with the right position, size and attributes
 * @param {object} drwCls - the object representing the class to be drawn
 */
function drawClass(drwCls) {

    if (!drwCls.visible) return;
    classes.push(drwCls);

    // adding 10 to attributes and methods, to have a lower bound (else we won't see the class)
    var clsHeight = ((drwCls.cls.methods * 3) + 5) * scale;
    var clsWidth = ((drwCls.cls.attributes * 3) + 5) * scale;

    var posX = drwCls.cx * scale;
    var posY = drwCls.cy * scale;
    var posZ = (drwCls.z * scale * packageHeight) + (((clsHeight / 2) + (packageHeight / 2) * scale));

    var color = drwCls.color;

    // create geometry and material for this class
    geometry = new THREE.BoxGeometry(clsWidth, clsWidth, clsHeight);
    for (var i = 0; i < geometry.faces.length; i++) {
        var face = geometry.faces[i];
        face.color.setHex(color);
    }
    material = new THREE.MeshBasicMaterial({color: color, wireframe: false});

    // invisible material allows raycasting invisible objects
    material.visible = false;

    // create the mash with the needed data
    mesh = new THREE.Mesh(geometry, material);
    mesh.name = drwCls.cls.name;
    mesh.realColor = color;
    mesh.filename = drwCls.cls.filename;
    mesh.methods = drwCls.cls.methods;
    mesh.attributes = drwCls.cls.attributes;
    mesh.linesOfCode = drwCls.cls.linesOfCode;
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
 * @param {object} drwPkg - the object representing the package to be drawn
 * @param totalClasses - the number of classes contained in children packages
 */
function drawPackage(drwPkg, totalClasses) {
    // size of the package
    var width = drwPkg.width * scale;
    var depth = drwPkg.depth * scale;
    if (width === 0 || depth === 0) return 0;
    var height = packageHeight * scale;

    // position of package
    var posX = drwPkg.cx * scale;
    var posY = drwPkg.cy * scale;
    var posZ = drwPkg.z * packageHeight * scale;

    var color = drwPkg.color;

    // create geometry and material for this package
    geometry = new THREE.BoxGeometry(width, depth, height);
    for (var i = 0; i < geometry.faces.length; i++) {
        var face = geometry.faces[i];
        face.color.setHex(color);
    }
    material = new THREE.MeshBasicMaterial({color: color, wireframe: false});

    // invisible material allows raycasting invisible objects
    material.visible = false;

    // create the mash with the needed data
    mesh = new THREE.Mesh(geometry, material);
    mesh.name = drwPkg.pkg.name;
    var classes = 0;
    for (var j = 0; j < drwPkg.drawableClasses.length; ++j) {
        var cls = drwPkg.drawableClasses[j];
        if (cls.visible) classes += 1;
    }
    mesh.classes = classes;

    mesh.totalClasses = totalClasses + classes;
    mesh.width = width;
    mesh.depth = depth;
    mesh.type = "package";
    mesh.realColor = color;

    // position the mesh
    mesh.translateX(posX);
    mesh.translateY(posY);
    mesh.translateZ(posZ);

    // add mesh to the array of meshes and the scene
    meshes.push(mesh);
    scene.add(mesh);
    return classes;
}


/**
 * readies the page when the visualization is loaded
 */
function loaded(totalClasses) {
    $("#main-content").css('display', 'none');
    $("#container").css('display', 'block');
    $("#navbar-visualization-items").css('display', 'contents');
    classesText.innerText = totalClasses;

    // notify the renderer that our html canvas has appeared
    renderer.setSize(canvas.clientWidth, canvas.clientHeight);
    camera.aspect = canvas.clientWidth / canvas.clientHeight;
    camera.updateProjectionMatrix();

    // update shadows only once
    renderer.shadowMap.needsUpdate = true;

    var commitsDropdown = $("#commits-dropdown");
    commitsList = $("#commits-list");

    commitsDropdown.on('focus', function () {
        commitsList.css('display', 'block');
    });

    commitsDropdown.on('blur', function () {
        commitsList.css('display', 'none');
    });

    commitsList.on('mousedown', function (e) {
        commitsDropdown.text(e.target.innerText.split(/\r?\n/)[0]);
        event.preventDefault();
    });

    setSearchResults();

    // add events for visualization callbacks
    canvas.addEventListener('click', canvasClick, false);
    window.addEventListener('resize', onWindowResize, false);
    window.addEventListener('click', altClick, false);
    window.addEventListener('mousemove', onMouseMove, false);
    window.addEventListener("keydown", onKeyPress, false);
    window.addEventListener('mousewheel', onWheel, false);
    render();
}

function callNext(list, idx) {
    list[idx].click();
    idx++;
    // debug
    // if (idx < 30) {
    if (recording && idx < list.length) {
        setTimeout(function () {
            callNext(list, idx)
        }, 250);
    }
}


/**
 * merges together an array of meshes
 *
 * @param {Array} meshes - the array of meshes to merge
 * @returns the combined mesh
 */
function mergeMeshes(meshes) {
    var combined = new THREE.Geometry();

    for (var i = 0; i < meshes.length; ++i) {
        meshes[i].updateMatrix();
        combined.merge(meshes[i].geometry, meshes[i].matrix);
    }

    return combined;
}