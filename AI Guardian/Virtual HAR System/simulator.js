const simulationData = JSON.parse(document.getElementById('simulationData').textContent);
const { patientType, roomType, patientData, roomData } = simulationData;

let scene, camera, renderer, human, room;
let currentActivity = 'stand';
let csiData = [];
let lastActivityTime = Date.now();
const inactivityThreshold = 5000; // 5 seconds of inactivity before considering 'no-activity'

const colors = {
    wall: 0xE5E0D5,
    floor: 0xD7CCC8,
    bed: 0x8D6E63,
    bedding: 0xFFCCBC,
    chair: 0x795548,
    humanBody: 0x64B5F6,
    humanHead: 0xFFB74D,
    door: 0xA1887F
};

function init() {
    scene = new THREE.Scene();
    camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);
    renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(window.innerWidth, window.innerHeight);
    document.body.appendChild(renderer.domElement);

    const ambientLight = new THREE.AmbientLight(0x404040);
    scene.add(ambientLight);
    const directionalLight = new THREE.DirectionalLight(0xffffff, 0.5);
    directionalLight.position.set(1, 1, 1);
    scene.add(directionalLight);

    createRoom();
    createHuman();

    camera.position.set(0, 2, 5);
    camera.lookAt(0, 1, 0);

    startContinuousDataCollection();
    animate();
}

function createRoom() {
    room = new THREE.Group();

    // Floor
    const floorGeometry = new THREE.PlaneGeometry(5, 5);
    const floorMaterial = new THREE.MeshPhongMaterial({ color: colors.floor });
    const floor = new THREE.Mesh(floorGeometry, floorMaterial);
    floor.rotation.x = -Math.PI / 2;
    room.add(floor);

    // Walls
    const wallMaterial = new THREE.MeshPhongMaterial({ color: colors.wall });
    const backWall = new THREE.Mesh(new THREE.PlaneGeometry(5, 3), wallMaterial);
    backWall.position.set(0, 1.5, -2.5);
    room.add(backWall);

    const leftWall = new THREE.Mesh(new THREE.PlaneGeometry(5, 3), wallMaterial);
    leftWall.position.set(-2.5, 1.5, 0);
    leftWall.rotation.y = Math.PI / 2;
    room.add(leftWall);

    const rightWall = new THREE.Mesh(new THREE.PlaneGeometry(5, 3), wallMaterial);
    rightWall.position.set(2.5, 1.5, 0);
    rightWall.rotation.y = -Math.PI / 2;
    room.add(rightWall);

    // Door
    const doorGeometry = new THREE.BoxGeometry(1, 2, 0.1);
    const doorMaterial = new THREE.MeshPhongMaterial({ color: colors.door });
    const door = new THREE.Mesh(doorGeometry, doorMaterial);
    door.position.set(2, 1, 2.45);
    room.add(door);

    // Bed
    const bedFrame = new THREE.Mesh(
        new THREE.BoxGeometry(roomData.bedSize.width, 0.5, roomData.bedSize.length),
        new THREE.MeshPhongMaterial({ color: colors.bed })
    );
    bedFrame.position.set(-1, 0.25, -1.5);
    room.add(bedFrame);

    const bedding = new THREE.Mesh(
        new THREE.BoxGeometry(roomData.bedSize.width - 0.1, 0.1, roomData.bedSize.length - 0.1),
        new THREE.MeshPhongMaterial({ color: colors.bedding })
    );
    bedding.position.set(-1, 0.55, -1.5);
    room.add(bedding);

    // Chair (only for rooms with chairs)
    if (roomData.hasChair) {
        const chairSeat = new THREE.Mesh(
            new THREE.BoxGeometry(0.6, 0.1, 0.6),
            new THREE.MeshPhongMaterial({ color: colors.chair })
        );
        chairSeat.position.set(1.5, 0.5, 1);
        room.add(chairSeat);

        const chairBack = new THREE.Mesh(
            new THREE.BoxGeometry(0.6, 0.6, 0.1),
            new THREE.MeshPhongMaterial({ color: colors.chair })
        );
        chairBack.position.set(1.5, 0.8, 1.25);
        room.add(chairBack);

        // Chair legs
        const legGeometry = new THREE.CylinderGeometry(0.05, 0.05, 0.5);
        const legMaterial = new THREE.MeshPhongMaterial({ color: colors.chair });
        const legPositions = [
            [1.25, 0.25, 0.75],
            [1.75, 0.25, 0.75],
            [1.25, 0.25, 1.25],
            [1.75, 0.25, 1.25]
        ];

        legPositions.forEach(pos => {
            const leg = new THREE.Mesh(legGeometry, legMaterial);
            leg.position.set(...pos);
            room.add(leg);
        });
    }

    scene.add(room);
}

function createHuman() {
    human = new THREE.Group();

    // Body
    const bodyGeometry = new THREE.BoxGeometry(patientData.width, patientData.height * 0.6, patientData.width * 0.4);
    const bodyMaterial = new THREE.MeshPhongMaterial({ color: colors.humanBody });
    const body = new THREE.Mesh(bodyGeometry, bodyMaterial);
    body.position.y = patientData.height * 0.3;
    human.add(body);

    // Head
    const headGeometry = new THREE.SphereGeometry(patientData.width * 0.3, 32, 32);
    const headMaterial = new THREE.MeshPhongMaterial({ color: colors.humanHead });
    const head = new THREE.Mesh(headGeometry, headMaterial);
    head.position.y = patientData.height * 0.75;
    human.add(head);

    // Arms
    const armGeometry = new THREE.BoxGeometry(patientData.width * 0.2, patientData.height * 0.4, patientData.width * 0.2);
    const leftArm = new THREE.Mesh(armGeometry, bodyMaterial);
    leftArm.position.set(-patientData.width * 0.3, patientData.height * 0.3, 0);
    human.add(leftArm);

    const rightArm = new THREE.Mesh(armGeometry, bodyMaterial);
    rightArm.position.set(patientData.width * 0.3, patientData.height * 0.3, 0);
    human.add(rightArm);

    // Legs
    const legGeometry = new THREE.BoxGeometry(patientData.width * 0.25, patientData.height * 0.4, patientData.width * 0.25);
    const leftLeg = new THREE.Mesh(legGeometry, bodyMaterial);
    leftLeg.position.set(-patientData.width * 0.15, -patientData.height * 0.1, 0);
    human.add(leftLeg);

    const rightLeg = new THREE.Mesh(legGeometry, bodyMaterial);
    rightLeg.position.set(patientData.width * 0.15, -patientData.height * 0.1, 0);
    human.add(rightLeg);

    human.position.set(0, patientData.height / 2, 0);
    scene.add(human);

    if (patientData.tremor > 0) {
        function applyTremor() {
            human.position.x += (Math.random() - 0.5) * patientData.tremor;
            human.position.y += (Math.random() - 0.5) * patientData.tremor;
            human.position.z += (Math.random() - 0.5) * patientData.tremor;
            requestAnimationFrame(applyTremor);
        }
        applyTremor();
    }
}

function animate() {
    requestAnimationFrame(animate);
    renderer.render(scene, camera);
    checkForInactivity();
}

function checkForInactivity() {
    if (Date.now() - lastActivityTime > inactivityThreshold && currentActivity !== 'no-activity') {
        currentActivity = 'no-activity';
        updateDataOutput();
    }
}

function isColliding(position) {
    const humanBoundingBox = new THREE.Box3().setFromObject(human);
    humanBoundingBox.min.set(position.x - patientData.width / 2, position.y, position.z - patientData.width / 4);
    humanBoundingBox.max.set(position.x + patientData.width / 2, position.y + patientData.height, position.z + patientData.width / 4);

    const obstacles = [
        new THREE.Box3(new THREE.Vector3(-2, 0, -2), new THREE.Vector3(0, 1, -1)),  // Bed
        new THREE.Box3(new THREE.Vector3(1.2, 0, 0.7), new THREE.Vector3(1.8, 1, 1.3)),  // Chair
        new THREE.Box3(new THREE.Vector3(-2.5, 0, -2.5), new THREE.Vector3(2.5, 3, -2.4)),  // Back wall
        new THREE.Box3(new THREE.Vector3(-2.5, 0, -2.5), new THREE.Vector3(-2.4, 3, 2.5)),  // Left wall
        new THREE.Box3(new THREE.Vector3(2.4, 0, -2.5), new THREE.Vector3(2.5, 3, 2.5)),  // Right wall
    ];

    return obstacles.some(obstacle => humanBoundingBox.intersectsBox(obstacle));
}

function performActivity(activity) {
    if (activity === 'sit' && !roomData.hasChair) {
        console.log("Can't sit, no chair available in this room type.");
        return;
    }

    currentActivity = activity;
    lastActivityTime = Date.now();
    const duration = 1000 / patientData.speed;
    const startPosition = human.position.clone();
    const startRotation = human.rotation.clone();
    const startScale = human.scale.clone();
    let targetPosition, targetRotation, targetScale;

    switch(activity) {
        case 'walk':
            targetPosition = new THREE.Vector3(
                Math.random() * 4 - 2,
                patientData.height / 2,
                Math.random() * 4 - 2
            );
            let attempts = 0;
            while (isColliding(targetPosition) && attempts < 100) {
                targetPosition.set(
                    Math.random() * 4 - 2,
                    patientData.height / 2,
                    Math.random() * 4 - 2
                );
                attempts++;
            }
            if (attempts >= 100) {
                console.log("Couldn't find a valid position to walk to.");
                return;
            }
            targetRotation = new THREE.Euler(0, Math.atan2(targetPosition.x - startPosition.x, targetPosition.z - startPosition.z), 0);
            targetScale = startScale.clone();
            break;
        case 'kneel':
            targetPosition = new THREE.Vector3(human.position.x, patientData.height * 0.3, human.position.z);
            targetRotation = startRotation.clone();
            targetScale = new THREE.Vector3(1, 0.6, 1);
            break;
        case 'sit':
            targetPosition = new THREE.Vector3(1.5, patientData.height * 0.5, 1);
            targetRotation = new THREE.Euler(0, Math.PI / 2, 0);
            targetScale = new THREE.Vector3(1, 0.7, 1);
            break;
        case 'stand':
            targetPosition = new THREE.Vector3(human.position.x, patientData.height / 2, human.position.z);
            targetRotation = new THREE.Euler(0, 0, 0);
            targetScale = new THREE.Vector3(1, 1, 1);
            break;
        case 'liedown':
            targetPosition = new THREE.Vector3(-1, roomData.bedSize.width / 2 + 0.1, -1.5);
            targetRotation = new THREE.Euler(0, 0, -Math.PI / 2);
            targetScale = new THREE.Vector3(1, 0.4, 1);
            break;
    }

    const startTime = Date.now();

    function animateActivity() {
        const elapsedTime = Date.now() - startTime;
        const progress = Math.min(elapsedTime / duration, 1);

        human.position.lerpVectors(startPosition, targetPosition, progress);
        human.rotation.x = THREE.MathUtils.lerp(startRotation.x, targetRotation.x, progress);
        human.rotation.y = THREE.MathUtils.lerp(startRotation.y, targetRotation.y, progress);
        human.rotation.z = THREE.MathUtils.lerp(startRotation.z, targetRotation.z, progress);
        human.scale.lerpVectors(startScale, targetScale, progress);

        if (progress < 1) {
            requestAnimationFrame(animateActivity);
        } else {
            collectCSIData();
        }
    }

    animateActivity();
}

function collectCSIData() {
    // Don't collect data for 'sit' activity if there's no chair
    if (currentActivity === 'sit' && !roomData.hasChair) {
        return;
    }

    const numSubcarriers = 30;
    let csi = [];
    for (let i = 0; i < numSubcarriers; i++) {
        let amplitude = 1 + 0.2 * Math.random();
        let phase = Math.random() * 2 * Math.PI;
        
        // Add more variation to CSI data for Parkinson's patients
        if (patientData.tremor > 0) {
            amplitude += (Math.random() - 0.5) * patientData.tremor * 0.5;
            phase += (Math.random() - 0.5) * patientData.tremor * Math.PI;
        }
        
        csi.push({ amplitude, phase });
    }
    csiData.push({ 
        activity: currentActivity, 
        timestamp: Date.now(), 
        patientType: patientType,
        roomType: roomType,
        tremor: patientData.tremor,
        csi 
    });
    updateDataOutput();
}

function startContinuousDataCollection() {
    collectCSIData();
    setTimeout(startContinuousDataCollection, 1000); // Collect data every second
}

function updateDataOutput() {
    document.getElementById('dataOutput').innerHTML = `Current Activity: ${currentActivity}<br>CSI Data Points: ${csiData.length}`;
}

function exportData() {
    // Create CSV content
    let csvContent = "timestamp,activity,patientType,roomType,tremor";
    for (let i = 0; i < 30; i++) {
        csvContent += `,amplitude_${i},phase_${i}`;
    }
    csvContent += "\n";

    csiData.forEach(data => {
        csvContent += `${data.timestamp},${data.activity},${data.patientType},${data.roomType},${data.tremor}`;
        data.csi.forEach(subcarrier => {
            csvContent += `,${subcarrier.amplitude},${subcarrier.phase}`;
        });
        csvContent += "\n";
    });

    // Create and download the file
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    
    const downloadLink = document.createElement('a');
    downloadLink.href = url;
    downloadLink.download = 'hospital_activity_csi_data.csv';
    
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);

    URL.revokeObjectURL(url);

    alert('Data exported successfully as CSV. Check your downloads folder.');
}

init();

document.getElementById('walkBtn').addEventListener('click', () => performActivity('walk'));
document.getElementById('kneelBtn').addEventListener('click', () => performActivity('kneel'));
document.getElementById('sitBtn').addEventListener('click', () => performActivity('sit'));
document.getElementById('standBtn').addEventListener('click', () => performActivity('stand'));
document.getElementById('liedownBtn').addEventListener('click', () => performActivity('liedown'));
document.getElementById('exportBtn').addEventListener('click', exportData);

window.addEventListener('resize', () => {
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
});