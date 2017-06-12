// sizes for the visualization
var padding = 2;
var minClassSize = 1;
var packageHeight = 1;

// list of all the meshes
var meshes = [];

// array of classes in the visualization
var classes = [];

// list of commits
var commitsList;
// list of commits for the recording feature
var commitsListFirst;
var commitsListLast;
var commitsListFirstSelected = -1;
var commitsListLastSelected = -1;

// boolean that tells us if it's the first visualization we are drawing
var firstDraw = true;


/**
 * function that takes care of drawing the packages and classes
 * @param {object} drwPkg - the root package of the visualization to be drawn
 */
function draw(drwPkg) {

    // ratio to scale the visualization so that it doesn't get too big
    scale = 1250 / Math.max(drwPkg.width, drwPkg.depth);

    // create the meshes for all the packages and classes
    var totalClasses = recDraw(drwPkg);

    // merge the meshes into a single new mesh; this makes the visualization faster/less computationally expensive.
    // we still have the array `meshes` with the invisible, single meshes so that we can tell them apart for callbacks and
    // other similar things
    geometry = mergeMeshes(meshes);

    //  best looking material, but computationally expensive
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

    // if it's the first visualization that we load, center it
    if (firstDraw) {
        var center = getCenterPoint(mesh);
        camera.position.setX(center.x);
        controls.target.set(center.x, center.y, center.z);
        controls.update();
        firstDraw = false;
    }

    // add the mesh to the scene and notify that the visualization is ready
    mesh.castShadow = true;
    mesh.receiveShadow = true;
    scene.add(mesh);
    loaded(totalClasses);
}

/**
 * given a mesh, returns its center
 *
 * @param {Array} mesh - the mesh that we want to know the center of
 * @returns {Object} - the Three.Vector3 that gives the center position of the mesh
 */
function getCenterPoint(mesh) {
    var middle = new THREE.Vector3();
    var geometry = mesh.geometry;

    geometry.computeBoundingBox();

    middle.x = (geometry.boundingBox.max.x + geometry.boundingBox.min.x) / 2;
    middle.y = (geometry.boundingBox.max.y + geometry.boundingBox.min.y) / 2;
    middle.z = (geometry.boundingBox.max.z + geometry.boundingBox.min.z) / 2;

    mesh.localToWorld(middle);
    return middle;
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
 * @param {Object} drwPkg - the root package of the visualization to be drawn
 */
function recDraw(drwPkg) {
    var totalClasses = 0;

    if (!currentVisibles[drwPkg.pkg.name]) return totalClasses;

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
 * @param {Object} drwCls - the object representing the class to be drawn
 */
function drawClass(drwCls) {

    if (currentVisibles) {
        if (!currentVisibles[drwCls.cls.filename]) return;
    }
    classes.push(drwCls);

    // adding 10 to attributes and methods, to have a lower bound (else we won't see the class)
    var clsHeight = (drwCls.cls.methods + minClassSize) * scale;
    var clsWidth = (drwCls.cls.attributes + minClassSize) * scale;

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

    mesh.rotation.x = -Math.PI / 2;


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
        var drwCls = drwPkg.drawableClasses[j];
        if (currentVisibles[drwCls.cls.filename]) classes += 1;
    }
    mesh.classes = classes;

    mesh.totalClasses = totalClasses + classes;
    mesh.width = width;
    mesh.depth = depth;
    mesh.type = "package";
    mesh.realColor = color;

    mesh.rotation.x = -Math.PI / 2;


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

    // if we are recording we don't have to set up everything
    if (recording) {
        renderer.setSize(canvas.clientWidth, canvas.clientHeight);
        camera.aspect = canvas.clientWidth / canvas.clientHeight;
        camera.updateProjectionMatrix();

        renderer.shadowMap.needsUpdate = true;
        render();
        callNext();
        return;
    }

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


    // set up the results for the search feature
    setSearchResults();


    // if possible, set up the web worker for the search function
    if (typeof(Worker) !== "undefined") {
        if (typeof(searchWorker) === "undefined") {
            searchWorker = new Worker("assets/javascripts/search_workers.js");

            searchWorker.addEventListener('message', function (e) {
                for (var i = 0; i < searchListItems.length; ++i) {
                    searchListItems[i].style.display = e.data[i];
                }
            }, false);
        }
    }

    // add events for visualization callbacks
    canvas.addEventListener('click', canvasClick, false);
    window.addEventListener('resize', onWindowResize, false);
    window.addEventListener('click', altClick, false);
    window.addEventListener('mousemove', onMouseMove, false);
    window.addEventListener("keydown", onKeyPress, false);
    window.addEventListener('mousewheel', onWheel, false);
    render();
}

/**
 * merges together an array of meshes
 *
 * @param {Array} meshes - the array of meshes to merge
 * @returns {Object} - the combined mesh
 */
function mergeMeshes(meshes) {
    var combined = new THREE.Geometry();

    for (var i = 0; i < meshes.length; ++i) {
        meshes[i].updateMatrix();
        combined.merge(meshes[i].geometry, meshes[i].matrix);
    }

    return combined;
}