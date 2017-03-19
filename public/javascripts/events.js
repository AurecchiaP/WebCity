/**
 * handles wheel scrolls/zoom
 */
function onWheel(e) {
    controls.update();
    render();
}


/**
 * handles right click
 */
function onContextMenu(e) {

    intersects = raycaster.intersectObjects(meshes);
    if (intersects.length > 0 && intersects[0].object.type == "class") {
        var cls = intersects[0].object;
        console.log(cls);

        // TODO find a way to get the code, then maybe use bootrstrap popovers
    }
}

/**
 * handles key-press events
 */
function onKeyPress(e) {
    var keys = { LEFT: 37, UP: 38, RIGHT: 39, BOTTOM: 40 };
    switch ( event.keyCode ) {

        // directional keys, pan
        case keys.UP:
            render();
            break;
        case keys.BOTTOM:
            render();
            break;
        case keys.LEFT:
            render();
            break;
        case keys.RIGHT:
            render();
            break;

    // key g, pin object
        case 71:
        var intersects = raycaster.intersectObjects(meshes);

        if (pinnedObject) {
            pinnedObject.object.material.visible = false;
            pinnedObject.object.material.color.set(pinnedColor);
            pinnedObject = null;
            isPinned = false;
        }

        if (intersects.length > 0) {
            pinnedObject = intersects[0];
            pinnedColor = pinnedObject.object.material.color;
            pinnedObject.object.material.visible = true;
            pinnedObject.object.material.color.set(0xF1BB4E);
            isPinned = true;
            if (hoveredCube.object.type == "package") {
                nameText.innerText = "Package name: " + hoveredCube.object.name;

                statistic1Text.innerText = "Contained classes: " + hoveredCube.object.classes;
                statistic2Text.innerText = "Total classes: " + hoveredCube.object.totalClasses;
                statistic3Text.innerText = "None";
            }
            else if (hoveredCube.object.type == "class") {
                nameText.innerText = "Class name: " + hoveredCube.object.name;
                statistic1Text.innerText = "Contained methods: " + hoveredCube.object.methods;
                statistic2Text.innerText = "Contained attributes: " + hoveredCube.object.attributes;
                statistic3Text.innerText = "Lines of code: " + hoveredCube.object.linesOfCode;
            }
        }
        renderer.render(scene, camera);
        renderer.render(scene, camera);
    }
}


/**
 * updates the visualization when we move the mouse
 */
function onMouseMove(event) {
    mouse.x = ( ( event.clientX - renderer.domElement.offsetLeft ) / renderer.domElement.clientWidth ) * 2 - 1;
    mouse.y = -( ( event.clientY - renderer.domElement.offsetTop ) / renderer.domElement.clientHeight ) * 2 + 1;
    render();
}


/**
 * updates the visualization when we resize the window
 */
function onWindowResize(event) {

    camera.aspect = canvas.clientWidth / canvas.clientHeight;
    camera.updateProjectionMatrix();

    renderer.setSize(canvas.clientWidth, canvas.clientHeight);
    camera.updateProjectionMatrix();

    controls.update();

    onMouseMove(event);
}



