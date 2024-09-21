const simulationData = JSON.parse(document.getElementById('simulationData').textContent);
const { patientType, roomType, patientData, roomData } = simulationData;

let scene, camera, renderer, human, room;
let currentActivity = 'stand';
let csiData = [];
let lastActivityTime = Date.now();
const inactivityThreshold = 5000; // 5 seconds of inactivity before considering 'no-activity'

// Global variables for limbs and furniture
let leftArm, rightArm, leftLeg, rightLeg, torso, head;
let leftElbow, rightElbow, leftKnee, rightKnee;
let bed, chair;

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
    renderer.shadowMap.enabled = true;
    document.body.appendChild(renderer.domElement);

    const ambientLight = new THREE.AmbientLight(0x404040);
    scene.add(ambientLight);
    
    const directionalLight = new THREE.DirectionalLight(0xffffff, 0.7);
    directionalLight.position.set(5, 5, 5);
    directionalLight.castShadow = true;
    scene.add(directionalLight);

    createRoom();
    createHuman();

    camera.position.set(0, 2, 5);
    camera.lookAt(0, 1, 0);

    animate();
}

function createRoom() {
    room = new THREE.Group();

    // Floor
    const floorGeometry = new THREE.PlaneGeometry(5, 5);
    const floorMaterial = new THREE.MeshPhongMaterial({ color: colors.floor });
    const floor = new THREE.Mesh(floorGeometry, floorMaterial);
    floor.rotation.x = -Math.PI / 2;
    floor.receiveShadow = true;
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
    bed = new THREE.Group();
    const bedFrame = new THREE.Mesh(
        new THREE.BoxGeometry(roomData.bedSize.width, 0.5, roomData.bedSize.length),
        new THREE.MeshPhongMaterial({ color: colors.bed })
    );
    bedFrame.position.set(0, 0.25, 0);
    bedFrame.castShadow = true;
    bed.add(bedFrame);

    const bedding = new THREE.Mesh(
        new THREE.BoxGeometry(roomData.bedSize.width - 0.1, 0.1, roomData.bedSize.length - 0.1),
        new THREE.MeshPhongMaterial({ color: colors.bedding })
    );
    bedding.position.set(0, 0.55, 0);
    bedding.castShadow = true;
    bed.add(bedding);

    bed.position.set(-1, 0, -1.5);
    room.add(bed);

    // Chair (only for rooms with chairs)
    if (roomData.hasChair) {
        chair = new THREE.Group();
        const chairSeat = new THREE.Mesh(
            new THREE.BoxGeometry(0.6, 0.1, 0.6),
            new THREE.MeshPhongMaterial({ color: colors.chair })
        );
        chairSeat.position.set(0, 0.5, 0);
        chairSeat.castShadow = true;
        chair.add(chairSeat);

        const chairBack = new THREE.Mesh(
            new THREE.BoxGeometry(0.6, 0.6, 0.1),
            new THREE.MeshPhongMaterial({ color: colors.chair })
        );
        chairBack.position.set(0, 0.8, 0.25);
        chairBack.castShadow = true;
        chair.add(chairBack);

        const legGeometry = new THREE.CylinderGeometry(0.05, 0.05, 0.5);
        const legMaterial = new THREE.MeshPhongMaterial({ color: colors.chair });
        const legPositions = [
            [-0.25, 0.25, -0.25],
            [0.25, 0.25, -0.25],
            [-0.25, 0.25, 0.25],
            [0.25, 0.25, 0.25]
        ];

        legPositions.forEach(pos => {
            const leg = new THREE.Mesh(legGeometry, legMaterial);
            leg.position.set(...pos);
            leg.castShadow = true;
            chair.add(leg);
        });

        chair.position.set(1.5, 0, 1);
        room.add(chair);
    }

    scene.add(room);
}

function createHuman() {
    human = new THREE.Group();

    const totalHeight = patientData.height;
    const legHeight = totalHeight * 0.5;
    const torsoHeight = totalHeight * 0.3;
    const headRadius = patientData.width * 0.2;
    const neckHeight = totalHeight * 0.05;
    const upperArmLength = torsoHeight * 0.4;
    const lowerArmLength = torsoHeight * 0.4;
    const upperLegLength = legHeight * 0.5;
    const lowerLegLength = legHeight * 0.5;

    // Torso
    const torsoGeometry = new THREE.BoxGeometry(patientData.width * 0.6, torsoHeight, patientData.width * 0.3);
    const torsoMaterial = new THREE.MeshPhongMaterial({ color: colors.humanBody });
    torso = new THREE.Mesh(torsoGeometry, torsoMaterial);
    torso.position.y = legHeight;
    human.add(torso);

    // Upper Legs
    const upperLegGeometry = new THREE.CylinderGeometry(patientData.width * 0.08, patientData.width * 0.07, upperLegLength, 32);
    leftLeg = new THREE.Mesh(upperLegGeometry, torsoMaterial);
    leftLeg.position.set(-patientData.width * 0.15, legHeight / 2, 0);
    human.add(leftLeg);

    rightLeg = new THREE.Mesh(upperLegGeometry, torsoMaterial);
    rightLeg.position.set(patientData.width * 0.15, legHeight / 2, 0);
    human.add(rightLeg);

    // Lower Legs
    const lowerLegGeometry = new THREE.CylinderGeometry(patientData.width * 0.07, patientData.width * 0.06, lowerLegLength, 32);
    leftKnee = new THREE.Mesh(lowerLegGeometry, torsoMaterial);
    leftKnee.position.set(0, -upperLegLength / 2, 0);
    leftLeg.add(leftKnee);

    rightKnee = new THREE.Mesh(lowerLegGeometry, torsoMaterial);
    rightKnee.position.set(0, -upperLegLength / 2, 0);
    rightLeg.add(rightKnee);

    // Upper Arms
    const upperArmGeometry = new THREE.CylinderGeometry(patientData.width * 0.06, patientData.width * 0.055, upperArmLength, 32);
    leftArm = new THREE.Mesh(upperArmGeometry, torsoMaterial);
    leftArm.position.set(-patientData.width * 0.35, torsoHeight * 0.4, 0);
    torso.add(leftArm);

    rightArm = new THREE.Mesh(upperArmGeometry, torsoMaterial);
    rightArm.position.set(patientData.width * 0.35, torsoHeight * 0.4, 0);
    torso.add(rightArm);

    // Lower Arms
    const lowerArmGeometry = new THREE.CylinderGeometry(patientData.width * 0.055, patientData.width * 0.05, lowerArmLength, 32);
    leftElbow = new THREE.Mesh(lowerArmGeometry, torsoMaterial);
    leftElbow.position.set(0, -upperArmLength / 2, 0);
    leftArm.add(leftElbow);

    rightElbow = new THREE.Mesh(lowerArmGeometry, torsoMaterial);
    rightElbow.position.set(0, -upperArmLength / 2, 0);
    rightArm.add(rightElbow);

    // Neck and Head
    const neckGeometry = new THREE.CylinderGeometry(patientData.width * 0.08, patientData.width * 0.08, neckHeight, 32);
    const neck = new THREE.Mesh(neckGeometry, torsoMaterial);
    neck.position.y = torsoHeight / 2 + neckHeight / 2;
    torso.add(neck);

    const headGeometry = new THREE.SphereGeometry(headRadius, 32, 32);
    const headMaterial = new THREE.MeshPhongMaterial({ color: colors.humanHead });
    head = new THREE.Mesh(headGeometry, headMaterial);
    head.position.y = torsoHeight / 2 + neckHeight + headRadius;
    torso.add(head);

    // Position the entire human group
    human.position.set(0, lowerLegLength, 0); // This places the feet on the ground
    scene.add(human);

    if (patientData.tremor > 0) {
        function applyTremor() {
            human.position.x += (Math.random() - 0.5) * patientData.tremor;
            human.position.z += (Math.random() - 0.5) * patientData.tremor;
            // Removed y-axis tremor to keep the human on the ground
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

function easeOutQuad(t) {
    return t * (2 - t);
}

function checkForInactivity() {
    if (Date.now() - lastActivityTime > inactivityThreshold && currentActivity !== 'no-activity') {
        currentActivity = 'no-activity';
        updateDataOutput();
    }
}

function performActivity(activity) {
    if (activity === 'sit' && !roomData.hasChair) {
        console.log("Can't sit, no chair available in this room type.");
        return;
    }

    currentActivity = activity;
    lastActivityTime = Date.now();
    const duration = 2000;
    const startPosition = human.position.clone();
    const startRotation = human.rotation.clone();
    let targetPosition, targetRotation;

    // Store initial rotations of limbs
    const startArmRotations = {
        left: leftArm.rotation.clone(),
        right: rightArm.rotation.clone()
    };
    const startLegRotations = {
        left: leftLeg.rotation.clone(),
        right: rightLeg.rotation.clone()
    };

    switch(activity) {
        case 'walk':
            targetPosition = new THREE.Vector3(
                Math.random() * 4 - 2,
                0, // Place on ground
                Math.random() * 4 - 2
            );
            targetRotation = new THREE.Euler(0, Math.atan2(targetPosition.x - startPosition.x, targetPosition.z - startPosition.z), 0);
            break;
        case 'kneel':
            targetPosition = new THREE.Vector3(human.position.x, 0, human.position.z); // Place on ground
            targetRotation = new THREE.Euler(0, human.rotation.y, 0);
            break;
        case 'sit':
            targetPosition = new THREE.Vector3(
                chair.position.x,
                chair.position.y + 0.15, // Adjust based on chair height and human height
                chair.position.z - 0.03
            );
            targetRotation = new THREE.Euler(0, Math.PI, 0);
            break;
        case 'stand':
            targetPosition = new THREE.Vector3(human.position.x, 0, human.position.z); // Place on ground
            targetRotation = new THREE.Euler(0, human.rotation.y, 0);
            break;
        case 'liedown':
            targetPosition = new THREE.Vector3(
                bed.position.x - roomData.bedSize.width / 3,
                bed.position.y + 0.85, // Slightly raised more
                bed.position.z
            );
            targetRotation = new THREE.Euler(0, 0, -Math.PI / 2);
            break;
        }

        const startTime = Date.now();

        function animateActivity() {
            const elapsedTime = Date.now() - startTime;
            const progress = Math.min(elapsedTime / duration, 1);
            const easedProgress = easeOutQuad(progress);
    
            human.position.lerpVectors(startPosition, targetPosition, easedProgress);
            human.rotation.x = THREE.MathUtils.lerp(startRotation.x, targetRotation.x, easedProgress);
            human.rotation.y = THREE.MathUtils.lerp(startRotation.y, targetRotation.y, easedProgress);
            human.rotation.z = THREE.MathUtils.lerp(startRotation.z, targetRotation.z, easedProgress);

        switch(activity) {
            case 'walk':
                const walkCycle = (elapsedTime % 1000) / 1000;
                const armSwing = Math.sin(walkCycle * Math.PI * 2) * Math.PI / 4;
                const legSwing = Math.sin(walkCycle * Math.PI * 2) * Math.PI / 6;

                leftArm.rotation.x = armSwing;
                rightArm.rotation.x = -armSwing;
                leftElbow.rotation.x = Math.abs(armSwing) / 2;
                rightElbow.rotation.x = Math.abs(armSwing) / 2;

                leftLeg.rotation.x = -legSwing;
                rightLeg.rotation.x = legSwing;
                leftKnee.rotation.x = Math.abs(legSwing) / 2;
                rightKnee.rotation.x = Math.abs(legSwing) / 2;

                human.position.y = startPosition.y + Math.sin(walkCycle * Math.PI * 2) * 0.05;
                break;

            case 'kneel':
                leftLeg.rotation.x = THREE.MathUtils.lerp(startLegRotations.left.x, -Math.PI / 2, easedProgress);
                rightLeg.rotation.x = THREE.MathUtils.lerp(startLegRotations.right.x, -Math.PI / 2, easedProgress);
                leftKnee.rotation.x = THREE.MathUtils.lerp(0, Math.PI / 2, easedProgress);
                rightKnee.rotation.x = THREE.MathUtils.lerp(0, Math.PI / 2, easedProgress);
                break;

            case 'sit':
                // Bend the legs
                leftLeg.rotation.x = THREE.MathUtils.lerp(startLegRotations.left.x, -Math.PI / 2, easedProgress);
                rightLeg.rotation.x = THREE.MathUtils.lerp(startLegRotations.right.x, -Math.PI / 2, easedProgress);
                leftKnee.rotation.x = THREE.MathUtils.lerp(0, Math.PI / 2, easedProgress);
                rightKnee.rotation.x = THREE.MathUtils.lerp(0, Math.PI / 2, easedProgress);
                    
                // Adjust torso
                torso.rotation.x = THREE.MathUtils.lerp(0, Math.PI / 16, easedProgress);
                    
                // Adjust arms
                leftArm.rotation.x = THREE.MathUtils.lerp(startArmRotations.left.x, -Math.PI / 6, easedProgress);
                rightArm.rotation.x = THREE.MathUtils.lerp(startArmRotations.right.x, -Math.PI / 6, easedProgress);
                    
                // Lower the body
                human.position.y = THREE.MathUtils.lerp(startPosition.y, targetPosition.y, easedProgress);
                break;
            case 'stand':
                // Reset all rotations to standing position
                leftLeg.rotation.x = THREE.MathUtils.lerp(leftLeg.rotation.x, 0, easedProgress);
                rightLeg.rotation.x = THREE.MathUtils.lerp(rightLeg.rotation.x, 0, easedProgress);
                leftKnee.rotation.x = THREE.MathUtils.lerp(leftKnee.rotation.x, 0, easedProgress);
                rightKnee.rotation.x = THREE.MathUtils.lerp(rightKnee.rotation.x, 0, easedProgress);
                leftArm.rotation.x = THREE.MathUtils.lerp(leftArm.rotation.x, 0, easedProgress);
                rightArm.rotation.x = THREE.MathUtils.lerp(rightArm.rotation.x, 0, easedProgress);
                torso.rotation.x = THREE.MathUtils.lerp(torso.rotation.x, 0, easedProgress);
                break;
            case 'liedown':
                // Rotate the entire human to lie parallel to the bed
                human.rotation.z = THREE.MathUtils.lerp(startRotation.z, -Math.PI / 2, easedProgress);
                    
                // Adjust limbs for a natural lying position
                leftArm.rotation.x = THREE.MathUtils.lerp(startArmRotations.left.x, -Math.PI / 6, easedProgress);
                rightArm.rotation.x = THREE.MathUtils.lerp(startArmRotations.right.x, -Math.PI / 6, easedProgress);
                leftElbow.rotation.x = THREE.MathUtils.lerp(0, -Math.PI / 8, easedProgress);
                rightElbow.rotation.x = THREE.MathUtils.lerp(0, -Math.PI / 8, easedProgress);
                leftLeg.rotation.x = THREE.MathUtils.lerp(startLegRotations.left.x, Math.PI / 16, easedProgress);
                rightLeg.rotation.x = THREE.MathUtils.lerp(startLegRotations.right.x, Math.PI / 16, easedProgress);
                leftKnee.rotation.x = THREE.MathUtils.lerp(leftKnee.rotation.x, Math.PI / 16, easedProgress);
                rightKnee.rotation.x = THREE.MathUtils.lerp(rightKnee.rotation.x, Math.PI / 16, easedProgress);
                torso.rotation.x = THREE.MathUtils.lerp(0, 0, easedProgress);
                break;
            }
    
            if (progress < 1) {
                requestAnimationFrame(animateActivity);
            } else {
                startContinuousDataCollection(activity);
            }
        }
    
        animateActivity();
    }
    
    function collectCSIData(activity) {
        const numSubcarriers = 30; // Adjusted to match real data
        const numAntennas = 3;
        let csi = [];
    
        // Constants for amplitude scaling and variability
        const AMPLITUDE_SCALE_FACTOR = 35; // Adjusted to match real data range
        const AMPLITUDE_OFFSET = 20; // Adjusted to match real data range
        const AMPLITUDE_VARIABILITY = 0.3; // Increased for more variability
        const PHASE_VARIABILITY = Math.PI / 2; // Increased for more variability
    
        // Function to simulate multipath effects
        function simulateMultipath(baseSignal, strength = 0.4) {
            return baseSignal * (1 + strength * (Math.random() - 0.5));
        }
    
        // Function to simulate fading effects
        function simulateFading(time, fadingRate = 0.1) {
            return Math.sin(time * fadingRate) * 0.5 + 0.5;
        }
    
        // Activity-specific modulation
        function activityModulation(activity, time) {
            switch(activity) {
                case 'kneel':
                    return Math.sin(time * 0.1) * 0.3 + 1;
                case 'walk':
                    return Math.sin(time * 0.5) * 0.4 + 1;
                case 'sit':
                    return Math.sin(time * 0.05) * 0.2 + 1;
                case 'stand':
                    return Math.sin(time * 0.01) * 0.1 + 1;
                case 'liedown':
                    return Math.sin(time * 0.03) * 0.25 + 1;
                default:
                    return 1;
            }
        }
    
        // Function to generate correlated noise
        function generateCorrelatedNoise(previousNoise = null, correlation = 0.7) {
            const newNoise = (Math.random() - 0.5) * 2;
            if (previousNoise === null) return newNoise;
            return correlation * previousNoise + (1 - correlation) * newNoise;
        }
    
        const time = Date.now() / 1000; // Current time in seconds
        let previousAmplitudeNoise = null;
        let previousPhaseNoise = null;
    
        for (let ant = 1; ant <= numAntennas; ant++) {
            for (let subcarrier = 1; subcarrier <= numSubcarriers; subcarrier++) {
                // Generate base amplitude and phase
                let baseAmplitude = Math.random();
                let basePhase = (Math.random() - 0.5) * Math.PI * 2;
    
                // Apply scaling and variability to amplitude
                let amplitude = baseAmplitude * AMPLITUDE_SCALE_FACTOR + AMPLITUDE_OFFSET;
                amplitude *= (1 + (Math.random() - 0.5) * AMPLITUDE_VARIABILITY);
    
                // Apply variability to phase
                let phase = basePhase + (Math.random() - 0.5) * PHASE_VARIABILITY;
    
                // Apply multipath, fading, and activity-specific effects
                amplitude *= simulateMultipath(amplitude);
                amplitude *= simulateFading(time + subcarrier * 0.1);
                amplitude *= activityModulation(activity, time + ant * 0.2);
    
                // Apply correlated noise
                previousAmplitudeNoise = generateCorrelatedNoise(previousAmplitudeNoise);
                amplitude += previousAmplitudeNoise * AMPLITUDE_SCALE_FACTOR * 0.1;
    
                previousPhaseNoise = generateCorrelatedNoise(previousPhaseNoise, 0.3);
                phase += previousPhaseNoise * PHASE_VARIABILITY * 0.1;
    
                // Apply tremor effect if present
                if (patientData.tremor > 0) {
                    amplitude += (Math.random() - 0.5) * patientData.tremor * AMPLITUDE_SCALE_FACTOR * 0.2;
                    phase += (Math.random() - 0.5) * patientData.tremor * PHASE_VARIABILITY * 0.2;
                }
    
                // Occasional outliers
                if (Math.random() < 0.01) { // 1% chance of an outlier
                    amplitude *= (Math.random() * 0.5 + 1.5); // Multiply by 1.5-2x
                }
    
                // Ensure amplitude stays within a realistic range
                amplitude = Math.max(AMPLITUDE_OFFSET * 0.5, Math.min(amplitude, AMPLITUDE_OFFSET * 2.5));
    
                // Ensure phase stays within -π to π range
                phase = ((phase + Math.PI) % (2 * Math.PI)) - Math.PI;
    
                csi.push({
                    antenna: `tx1rx${ant}_sub${subcarrier}`,
                    amplitude: amplitude,
                    phase: phase
                });
            }
        }
    
        csiData.push({
            activity: activity,
            timestamp: Date.now(),
            patientType: patientType,
            roomType: roomType,
            tremor: patientData.tremor,
            csi
        });
        updateDataOutput();
    }
    
    function startContinuousDataCollection(activity) {
        let samplesPerSecond = 1000; // 1000 samples per second
        let collectionDuration = 10 * 1000; // 10 seconds
    
        function collect() {
            collectCSIData(activity);
            if (Date.now() - startTime < collectionDuration) {
                setTimeout(collect, 1000 / samplesPerSecond);
            }
        }
    
        let startTime = Date.now();
        collect();
    }
    
    function updateDataOutput() {
        document.getElementById('dataOutput').innerHTML = `Current Activity: ${currentActivity}<br>CSI Data Points: ${csiData.length}`;
    }
    
    function exportData() {
        let csvContent = "timestamp,activity,patientType,roomType,tremor";
    
        for (let ant = 1; ant <= 3; ant++) {
            for (let sub = 1; sub <= 28; sub++) {
                csvContent += `,tx1rx${ant}_sub${sub}`;
            }
        }
        csvContent += "\n";
    
        csiData.forEach(data => {
            let row = `${data.timestamp},${data.activity},${data.patientType},${data.roomType},${data.tremor}`;
    
            data.csi.forEach(subcarrier => {
                row += `,${subcarrier.amplitude.toFixed(6)}+${subcarrier.phase.toFixed(6)}j`;
            });
    
            csvContent += row + "\n";
        });
    
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
