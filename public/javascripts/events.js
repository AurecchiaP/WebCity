/**
 * canvas doesn't propagate events, so when we click it the old active element doesn't lose focus;
 * therefore we manually blur the old focused element when we click the canvas
 */
function canvasClick() {
    document.activeElement.blur();
}

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
        if (intersects.length > 0) {
            var obj = intersects[0].object;
            var url;
            if (intersects[0].object.type === "class") {
                url = repositoryUrl + "/tree/" + currentCommit + obj.filename;
            }
            else {
                url = repositoryUrl + "/tree/" + currentCommit + obj.name;
            }
            var win = window.open(url, '_blank');
            win.focus();
        }
    }

}

/**
 * handles key-press events
 */
function onKeyPress(event) {
    var keys = {LEFT: 37, UP: 38, RIGHT: 39, BOTTOM: 40, P: 80};
    switch (event.keyCode) {

        // key 'p', pin object
        case keys.P:
            var intersects = raycaster.intersectObjects(meshes);

            // if we clicked "p" when hovering an object
            if (intersects.length > 0) {

                // if there is an already pinned object
                if (pinnedObject) {
                    // unpin the previously pinned object
                    pinnedObject.object.material.visible = false;

                    // if we pinned the same object twice, unpin it
                    if (pinnedObject.object.uuid === intersects[0].object.uuid) {
                        pinnedObject = undefined;
                        renderer.render(scene, camera);
                        renderer.render(scene, camera);
                        return;
                    }
                }

                // pin the new object
                pinnedObject = intersects[0];
                pinnedColor = pinnedObject.object.material.color;
                pinnedObject.object.material.visible = true;
                pinnedObject.object.material.color.set(0xF77A52);
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



