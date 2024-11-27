package deakin.gopher.guardian.view.FallDetection

import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RawRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import deakin.gopher.guardian.R
import deakin.gopher.guardian.databinding.ActivityPoseDetectVideoBinding
import deakin.gopher.guardian.view.FallDetection.ui.PoseGraphic
import deakin.gopher.guardian.view.FallDetection.util.classification.PoseClassifierProcessor
import deakin.gopher.guardian.view.FallDetection.util.getInputImageFrom
import deakin.gopher.guardian.view.caretaker.notifications.confirmincident.ConfirmIncidentActivity
import deakin.gopher.guardian.view.caretaker.notifications.falsealarm.FalseAlertConfirmedActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration


@UnstableApi
class FallAlertActivity :  AppCompatActivity(), Player.Listener {
    var fallAlertMenuButton: ImageView? = null
    private lateinit var binding: ActivityPoseDetectVideoBinding

    private val exoPlayer by lazy {
        ExoPlayer.Builder(this)
            .setPauseAtEndOfMediaItems(true)
            .build()
    }

    private val retriever by lazy(::MediaMetadataRetriever)

    private val poseOptions by lazy {
        AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.CPU_GPU)
            .build()
    }

    private val poseDetector: PoseDetector by lazy {
        PoseDetection.getClient(poseOptions)
    }

    private var job: Job? = null

    private val poseClassifierProcessor: PoseClassifierProcessor by lazy {
        PoseClassifierProcessor(this@FallAlertActivity ,true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pose_detect_video)

        binding = ActivityPoseDetectVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPlayer()
        loadAndPrepareVideo()
        initClearButton()




        val confirmIncidentButton = findViewById<ImageButton>(R.id.confirmIncidentButton)
        val falseAlarmButton = findViewById<ImageButton>(R.id.falseAlarmButton)

        confirmIncidentButton.setOnClickListener { v: View? ->
            val medicalDiagnosticsActivityIntent =
                Intent(this@FallAlertActivity, ConfirmIncidentActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }

        falseAlarmButton.setOnClickListener { v: View? ->
            val medicalDiagnosticsActivityIntent =
                Intent(this@FallAlertActivity, FalseAlertConfirmedActivity::class.java)
            startActivity(medicalDiagnosticsActivityIntent)
        }
    }

    private fun setPlayer() {
        binding.playerView.player = exoPlayer
        exoPlayer.addListener(this)
        exoPlayer.setPlaybackSpeed(0.25f)
    }

    private fun loadAndPrepareVideo() {
        val assetFileDescriptor = resources.openRawResourceFd(R.raw.fall1)
        retriever.setDataSource(
            assetFileDescriptor.fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        )

        val video = getRawResourceUriString(R.raw.fall1)
        val mediaItem = MediaItem.fromUri(video)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    private fun initClearButton() {
        binding.clearButton.setOnClickListener {
            binding.graphicOverlayVideo.clear()
        }
    }

    private fun getRawResourceUriString(@RawRes rawResourceId: Int): String {
        val packageName = packageName
        return "android.resource://$packageName/raw/" + resources.getResourceEntryName(rawResourceId)
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        Log.d(TAG, "onIsPlayingChanged: isPlaying : $isPlaying")
        startProcessJobIfNeeded(isPlaying)
        cancelProcessJobIfNeeded(isPlaying)
    }

    private fun startProcessJobIfNeeded(isPlaying: Boolean) {
        if (job == null && isPlaying) {
            job = lifecycleScope.launch {
                while (isActive) {
                    processFrame()
                    delay(50)
                }
            }
        }
    }

    private fun cancelProcessJobIfNeeded(isPlaying: Boolean) {
        if (!isPlaying && job != null) {
            job?.cancel()
            job = null
        }
    }

    private fun processFrame() {
        val frame = getCurrentFrame() ?: return
        val inputImage = getInputImageFrom(frame)
        poseDetector.process(inputImage)
            .addOnSuccessListener { onPoseDetectionSucceeded(it, frame) }
            .addOnFailureListener(::onPoseDetectionFailed)
    }

    private fun getCurrentFrame(): Bitmap? {
        val currentPosMillis = exoPlayer.currentPosition.toDuration(DurationUnit.MILLISECONDS)
        val currentPosMicroSec = currentPosMillis.inWholeMicroseconds
        val currentFrame =  retriever.getFrameAtTime(currentPosMicroSec, MediaMetadataRetriever.OPTION_CLOSEST)
        val bitmap = currentFrame?: return null
        return Bitmap.createScaledBitmap(bitmap, binding.playerView.width, binding.playerView.height, false)
    }

    private fun onPoseDetectionSucceeded(pose: Pose?, bitmap: Bitmap) {
        val pose = pose ?: return
        val allPose = pose.allPoseLandmarks
        if (allPose.isEmpty()) {
            Log.e(TAG, "onCreate: no pose detected")
            return
        }

        binding.graphicOverlayVideo.setImageSourceInfo(bitmap.width, bitmap.height, false)
        binding.graphicOverlayVideo.clear()
        binding.graphicOverlayVideo.add(PoseGraphic(binding.graphicOverlayVideo, pose))

        /* lifecycleScope.launch(Dispatchers.Default) {
             val exerciseResult = poseClassifierProcessor.getExerciseResult(pose)
             withContext(Dispatchers.Main) {
                 setExerciseStatistics(exerciseResult, binding.pushUps, binding.squads)
             }
         }*/

        // Analyze the pose to detect falls
        val fallDetected = detectFall(pose)
        if (fallDetected) {
            Log.d(TAG, "Fall detected!")
            // Update UI or trigger an alert
            binding.fallAlertTextView.text = "Fall detected!"
        } else {
            binding.fallAlertTextView.text = "No fall detected"
        }
    }

    // Function to detect falls based on pose landmarks
    private fun detectFall(pose: Pose): Boolean {
        // Get key landmarks
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)

        // Ensure all key landmarks are present
        if (leftShoulder == null || rightShoulder == null || leftHip == null || rightHip == null || leftKnee == null || rightKnee == null) {
            Log.d(TAG, "Missing landmarks for fall detection")
            return false
        }

        // Log positions of key landmarks
        Log.d(TAG, "Left Shoulder: (${leftShoulder.position.x}, ${leftShoulder.position.y})")
        Log.d(TAG, "Right Shoulder: (${rightShoulder.position.x}, ${rightShoulder.position.y})")
        Log.d(TAG, "Left Hip: (${leftHip.position.x}, ${leftHip.position.y})")
        Log.d(TAG, "Right Hip: (${rightHip.position.x}, ${rightHip.position.y})")
        Log.d(TAG, "Left Knee: (${leftKnee.position.x}, ${leftKnee.position.y})")
        Log.d(TAG, "Right Knee: (${rightKnee.position.x}, ${rightKnee.position.y})")

        // Calculate midpoints for shoulders and hips
        val shoulderMidpointY = (leftShoulder.position.y + rightShoulder.position.y) / 2
        val hipMidpointY = (leftHip.position.y + rightHip.position.y) / 2

        // Log midpoints
        Log.d(TAG, "Shoulder Midpoint Y: $shoulderMidpointY")
        Log.d(TAG, "Hip Midpoint Y: $hipMidpointY")

        // Calculate the vertical distance between shoulders and hips
        val torsoVerticalDistance = Math.abs(shoulderMidpointY - hipMidpointY)
        Log.d(TAG, "Torso Vertical Distance: $torsoVerticalDistance")

        // Calculate torso angle (simplified approximation)
        val torsoAngle = Math.toDegrees(
            Math.atan2(
                (hipMidpointY - shoulderMidpointY).toDouble(),
                Math.abs(leftShoulder.position.x - leftHip.position.x).toDouble() // Approximation for torso horizontal distance
            )
        )
        Log.d(TAG, "Torso Angle: $torsoAngle")

        // Detect forward fall based on rapid angle change and proximity to the ground
        val isTorsoHorizontal = torsoAngle < TORSO_HORIZONTAL_THRESHOLD
        val isNearGround = hipMidpointY > GROUND_LEVEL_THRESHOLD
        Log.d(TAG, "Is Torso Horizontal: $isTorsoHorizontal")
        Log.d(TAG, "Is Near Ground: $isNearGround")

        // Check if knees are close to the ground (lying posture)
        val kneesCloseToGround = (leftKnee.position.y > GROUND_LEVEL_THRESHOLD && rightKnee.position.y > GROUND_LEVEL_THRESHOLD)
        Log.d(TAG, "Knees Close to Ground: $kneesCloseToGround")

        // Final fall detection logic
        return isTorsoHorizontal && isNearGround && kneesCloseToGround
    }

    companion object {
        private const val TAG = "PoseDetectVideoActivity"
        private const val TORSO_HORIZONTAL_THRESHOLD = 20.0 // Adjust for more precision (degrees)
        private const val GROUND_LEVEL_THRESHOLD = 650.0 // Pixels; tweak based on video resolution
    }



    private fun onPoseDetectionFailed(e: Exception) {
        Log.e(TAG, "onPoseDetectionFailed: $e")
    }



    override fun onPause() {
        super.onPause()
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!exoPlayer.isPlaying) {
            exoPlayer.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }

}
