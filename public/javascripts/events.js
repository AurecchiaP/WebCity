/**
 * handles key-press events
 */
window.addEventListener("keydown", onKeyPress, false);
function onKeyPress(e) {
    // key: e, zoom in
    if (e.keyCode == 69) {
        camera.position.z -= 200;
        controls.update();
        render();

    }
    // key: f, zoom out
    else if (e.keyCode == 70) {
        camera.position.z += 200;
        controls.update();
        render();
    }

    // key g, pin object
    else if (e.keyCode == 71) {

        intersects = raycaster.intersectObjects(meshes);

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
            }
            else if (hoveredCube.object.type == "class") {
                nameText.innerText = "Class name: " + hoveredCube.object.name;
                statistic1Text.innerText = "Contained methods: " + hoveredCube.object.methods;
                statistic2Text.innerText = "Contained attributes: " + hoveredCube.object.attributes;
            }
        }
        renderer.render(scene, camera);
        renderer.render(scene, camera);
    }
}


/**
 * updates the visualization when we move the mouse
 */
window.addEventListener('mousemove', onMouseMove, false);
function onMouseMove(event) {
    mouse.x = ( ( event.clientX - renderer.domElement.offsetLeft ) / renderer.domElement.clientWidth ) * 2 - 1;
    mouse.y = -( ( event.clientY - renderer.domElement.offsetTop ) / renderer.domElement.clientHeight ) * 2 + 1;
    render();
}


/**
 * updates the visualization when we resize the window
 */
window.addEventListener('resize', onWindowResize, false);
function onWindowResize(event) {

    camera.aspect = canvas.clientWidth / canvas.clientHeight;
    camera.updateProjectionMatrix();

    renderer.setSize(canvas.clientWidth, canvas.clientHeight);
    camera.updateProjectionMatrix();

    controls.update();

    onMouseMove(event);
}



