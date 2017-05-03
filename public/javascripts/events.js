/**
 * handles wheel scrolls/zoom
 */
function onWheel(e) {
    controls.update();
    render();
}
/**
 * handles alt + leftClick
 */
function altClick(e) {
    if (event.altKey === true) {
        intersects = raycaster.intersectObjects(meshes);
        if (intersects.length > 0 && intersects[0].object.type === "class") {
            var cls = intersects[0].object;
            var url = repositoryUrl + "/tree/" + currentCommit + cls.filename;
            var win = window.open(url, '_blank');
            win.focus();
            // TODO find a way to get the code, then maybe use bootstrap popovers
        }
    }
}

/**
 * handles key-press events
 */
function onKeyPress(e) {
    var keys = {LEFT: 37, UP: 38, RIGHT: 39, BOTTOM: 40};
    switch (event.keyCode) {

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

        // key 'p', pin object
        case 80:
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
                if (hoveredCube.object.type === "package") {
                    nameText.innerText = hoveredCube.object.name;
                    statistic1.firstElementChild.innerText = "contained classes";
                    statistic1.firstElementChild.nextElementSibling.innerText = hoveredCube.object.classes;
                    statistic2.firstElementChild.innerText = "total classes";
                    statistic2.firstElementChild.nextElementSibling.innerText = hoveredCube.object.totalClasses;
                    statistic3.firstElementChild.innerText = "";
                    statistic3.firstElementChild.nextElementSibling.innerText = "";
                }
                else if (hoveredCube.object.type === "class") {
                    nameText.innerText = hoveredCube.object.filename + ":" + hoveredCube.object.name;
                    statistic1.firstElementChild.innerText = "methods";
                    statistic1.firstElementChild.nextElementSibling.innerText = hoveredCube.object.methods;
                    statistic2.firstElementChild.innerText = "attributes";
                    statistic2.firstElementChild.nextElementSibling.innerText = hoveredCube.object.attributes;
                    statistic3.firstElementChild.innerText = "lines of code";
                    statistic3.firstElementChild.nextElementSibling.innerText = hoveredCube.object.linesOfCode;
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



