package deakin.gopher.guardian.view.FallDetection

import android.Manifest
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
import deakin.gopher.guardian.databinding.ActivityFallDetectionBinding
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
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat


@UnstableApi
class FallDetectionActivity :  AppCompatActivity(), Player.Listener {
    private lateinit var binding: ActivityFallDetectionBinding

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
        PoseClassifierProcessor(this@FallDetectionActivity ,true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fall_detection)

        createNotificationChannel()

        binding = ActivityFallDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setPlayer()
        loadAndPrepareVideo()
        initClearButton()


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


        // Analyze the pose to detect falls
        val fallDetected = detectFall(pose)
        if (fallDetected) {
            Log.d(TAG, "Fall detected!")
            // Update UI or trigger an alert
            binding.fallAlertTextView.text = "Fall detected!"

            // Send a notification
            sendFallNotification()

            val fallAlertActivityIntent =
                    Intent(this@FallDetectionActivity, FallAlertActivity::class.java)
            startActivity(fallAlertActivityIntent)
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

        // Calculate midpoints for shoulders and hips
        val shoulderMidpointY = (leftShoulder.position.y + rightShoulder.position.y) / 2
        val hipMidpointY = (leftHip.position.y + rightHip.position.y) / 2

        // Calculate the vertical distance between shoulders and hips
        val torsoVerticalDistance = Math.abs(shoulderMidpointY - hipMidpointY)

        // Calculate torso angle (simplified approximation)
        val torsoAngle = Math.toDegrees(
            Math.atan2(
                (hipMidpointY - shoulderMidpointY).toDouble(),
                Math.abs(leftShoulder.position.x - leftHip.position.x).toDouble() // Approximation for torso horizontal distance
            )
        )

        // Detect forward fall based on rapid angle change and proximity to the ground
        val isTorsoHorizontal = torsoAngle < TORSO_HORIZONTAL_THRESHOLD
        val isNearGround = hipMidpointY > GROUND_LEVEL_THRESHOLD

        // Check if knees are close to the ground (lying posture)
        val kneesCloseToGround = (leftKnee.position.y > GROUND_LEVEL_THRESHOLD && rightKnee.position.y > GROUND_LEVEL_THRESHOLD)

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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Fall Alerts"
            val channelDescription = "Notifications for detected falls"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("FALL_ALERTS", channelName, importance).apply {
                description = channelDescription
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Function to send a notification
    private fun sendFallNotification() {
        val notificationId = 1
        val channelId = "FALL_ALERTS"

        // Check for POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission if not granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1 // Request code
                )
                return // Exit function until permission is granted
            }
        }

        // Create PendingIntent for notification click action
        val notificationIntent = Intent(this, FallAlertActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Fall Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for detected falls"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.fall_notification_icon_foreground)
            .setContentTitle("Fall Detected!")
            .setContentText("A fall was detected. Tap to check details.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // Display the notification
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    // Handle runtime permission request results
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notification permission granted")
                sendFallNotification() // Retry sending the notification
            } else {
                Log.d(TAG, "Notification permission denied")
            }
        }
    }


}